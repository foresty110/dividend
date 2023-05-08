package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    //스크래핑 함수를 사용하기 위해서
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    public Company save(String ticker){
        //db에 이미 저장되어 있는 것은 저장하지 않는다.
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists){
            throw new RuntimeException("already existst ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }


    //이 메소드는 private라서 외부에서 접근이 불가능함
    private Company storeCompanyAndDividend(String ticker){
       //ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        //회사가 존재하지 않을 경우 여기서 종료한다.
        if (ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed to scrap ticker -> "+ticker);

        }
        //회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        //스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        //e를 다른 값으로 맵핑하기 위해서 map매소드를 사용한다.
        //e에 해당하는 값은 컬렉션의 아이템 하나하나이다.
        //즉, dividend의 아이템 하나하나가 e이고, DividendEntity가 되도록 하나하나 맵핑을 해준다.
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(),e))
                .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntityList);
        return company;
    }
}

package com.dayone.scraper;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";

    //회사명을 가져오는 url
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; // 60 * 60 * 25 ;

    //원래 url
    // 파라미터를 제거해도 동일한 페이지가 나오는 url로 변경
    //ticker -> %s 배당 날짜 -> %d 로 변경
    // "https://finance.yahoo.com/quote/KO/history?period1=1525478400&period2=1683244800&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true";

    @Override
    public ScrapedResult scrap(Company company) {

        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        //*스크래핑 과정
        try {

            long start = 0;
            long end = System.currentTimeMillis() / 1000;
            String url = String.format(STATISTICS_URL, company.getTicker(), start, end);
            //1. http connection을 맺는다.

            Connection connection = Jsoup.connect(url);
            //2. 그 커넥션으로 부터 html문서를 받아서 파싱된 형태로 도큐먼트 인스턴스로 만드는 과정을 jsoup라이브러리가 해준다.
            Document document = connection.get();

            //데이터 테이블 중 원하는 데이터의 속성을 가져온다
            //Elemets 복수형 (여러개를 가져올 수도 있어서)
            //3. 파싱된 결과가 들어있는 도큐먼트에서 원하는 데이터를 추출한다.
            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");

            //몇번째 인덱스에 있는 엘리먼트를 가져올 것인가
            Element tableEle = parsingDivs.get(0);

            //tbody에 있는 정보를 가져오도록 하는 명령
            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            //for 문이 돌 때 마다 리스트에 diviend 아이템이 하나씩 추가가 되고
            // 포문이 종료가 되고나면 모든 아이템이 추가된 리스트가 result에 추가된다.
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) { //배당금 정보가 아니면 출력하지 않겠다.
                    continue;
                }

                //일,년,월,일을 지정한 형태로 출력한다.

                String[] splits = txt.split(" ");
                int month = Month.strToNUmber(splits[0]);
                //반점 제거 코드
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }
                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
                // System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }
            scrapResult.setDividends(dividends);

        } catch (IOException e) {

            //TODO
            e.printStackTrace();
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL,ticker,ticker);
        try{

            //url에 해당하는 도큐먼트 반환
            Document document = Jsoup.connect(url).get();
            //회사명을 가져오기 위해 h1태그를 사용하는 것을 가져온다 , 그리고 0번째 해당하는 엘리먼트를 가져오면 타이틀이 들어있게 된다
            Element titleEle = document.getElementsByTag("h1").get(0);

            //회사명을 깔끔하게 가져오기 위한 처리
            String title = titleEle.text().split(" - ")[1].trim();
            //abc - def 를 -를 기준으로 abc 와 def 로 쪼개준다. 그리고 쪼개서 각 배열에 넣은 후 1번째 값을 가져온다 , trim은 앞뒤 공백을 제거해준다.


            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        }
        catch (IOException e){
            e.printStackTrace();
        }


        return null;
    }
}

package com.dayone.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//allargsconstructor :
//모든 필드를 초기화하는 생성자 코드를 쓸 수 있게하는 어노테이션
@Data
@AllArgsConstructor
public class ScrapedResult {
    //스크래핑한 회사의 정보
    private Company company;
    //한 회사는 여러개의 배당금 정보를 가지고 있으므로 그것을 담을 리스트
    private List<Dividend> dividends;

    public ScrapedResult(){ this.dividends = new ArrayList<>();}
}

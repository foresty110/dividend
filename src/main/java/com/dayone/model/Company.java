package com.dayone.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Company {
    //엔티티는 데이터베이스와 직접 매핑되는 용도
    //자신의 역할을 벗어난 범위까지 담당하게 될 수 있음
    //그래서 모델 클래스를 따로 정의
    private  String ticker;
    private String name;

}

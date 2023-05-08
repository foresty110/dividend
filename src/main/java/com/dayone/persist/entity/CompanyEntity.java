package com.dayone.persist.entity;


import com.dayone.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@ToString
@NoArgsConstructor
public class CompanyEntity {

    @Id
    //id가 생성되는 규칙을 오토 인크리먼트로 하기로 했으므로
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //티커는 중복이 되면 안되기 때문에
    @Column(unique = true)
    private String ticker;

    private String name;

    public CompanyEntity(Company company){
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}

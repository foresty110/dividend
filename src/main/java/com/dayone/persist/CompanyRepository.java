package com.dayone.persist;

import com.dayone.persist.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity,Long> {

    //껍데기밖에 없는데도 사용이 되는 이유는
    // 스프링부트에서 repositiory에 정해준 규칙대로 함수를 만들었을 경우
    // 스프링부트에서 내부를 자동으로 코드를 생성해서 실행해 준다.
    // (룰베이스 코드 생성)
    boolean existsByTicker(String ticker);
}

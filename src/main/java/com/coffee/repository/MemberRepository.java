package com.coffee.repository;


import com.coffee.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<엔터티 이름, 엔터티의 기본키(primary key)의 변수 타입>
// 회원 정보들을 이용하여 데이터베이스와 교신하는 인터페이스 입니다.
// 이전의 Dao 역할
public interface MemberRepository extends JpaRepository<Member, Long> {
    // findbyEmail를 JPA에서는 '쿼리 메소드'라고 부릅니다.
    // 이메일 정보를 이용하여 해당 회원이 존재하는 지 체크합니다.
    Member findByEmail(String email);
}



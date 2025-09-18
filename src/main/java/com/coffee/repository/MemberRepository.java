package com.coffee.repository;


import com.coffee.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// 회원 정보들을 이용하여 데이터베이스와 교신하는 인터페이스 입니다.
// 이전의 Dao 역할
// JpaRepository<엔터티 이름, 엔터티의 기본키(primary key)의 변수 타입>
public interface MemberRepository extends JpaRepository<Member, Long> {
}

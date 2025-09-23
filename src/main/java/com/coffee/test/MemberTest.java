package com.coffee.test;

import com.coffee.constant.Role;
import com.coffee.entity.Member;
import com.coffee.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest // 이 클래스는 간단한 테스트를 위한 용도로 사용합니다.
public class MemberTest {
    @Autowired // 의존하고 있는 객체를 외부에서 자동 주입합니다.
    private MemberRepository memberRepository; // 기본 값 null (인터페이스와 클래스는 참조자료형이기 때문에 값이 들어가지 않으면 기본값이 null이다.)


    @Test
    @DisplayName("회원 몇 명 추가하기")
    public void insertMemberList(){
    // 회원 몇명을 추가해 봅니다.
        Member mem01 = new Member();
        mem01.setName("관리자");
        mem01.setEmail("admin@naver.com");
        mem01.setPassword("Admin@123");
        mem01.setAddress("마포구 공덕동");
        mem01.setRole(Role.ADMIN);
        mem01.setRegdate(LocalDate.now());

        Member mem02 = new Member();
        mem02.setName("유영석");
        mem02.setEmail("bluesky@naver.com");
        mem02.setPassword("Bluesky@456");
        mem02.setAddress("용산구 이태원동");
        mem02.setRole(Role.USER);
        mem02.setRegdate(LocalDate.now());

        System.out.println("----------------------");

        Member mem03 = new Member();
        mem03.setName("곰돌이");
        mem03.setEmail("gomdori@naver.com");
        mem03.setPassword("Gomdori@789");
        mem03.setAddress("동대문구 휘경동");
        mem03.setRole(Role.USER);
        mem03.setRegdate(LocalDate.now());

        System.out.println("----------------------");

        memberRepository.save(mem01); // 데이터 베이스에 insert
        memberRepository.save(mem02); // 데이터 베이스에 insert
        memberRepository.save(mem03); // 데이터 베이스에 insert
        System.out.println("------------------");
    }




}

package com.coffee.entity;

import com.coffee.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
@Getter
@Setter
@ToString
// 회원 1명에 대한 정보를 저장하고 있는 자바 클래스
@Entity
@Table(name = "members") // 테이블 이름은 "members"로 설정하는 어노테이션
public class Member {
    @Id // 이 컬럼은 primary key입니다.
    @GeneratedValue(strategy = GenerationType.AUTO) // 기본키 생성 전략, 오라클에서의 시퀀스
    @Column(name = "member_id") // 컬럼 이름을 변경합니다.
    private Long id; // int보다 범위가 큰 정수의 타입을 Long이라고 한다.

    private String name;

    // 유니크 속성과, not null 속성을 설정합니다.
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING) // 컬럼에 문자열 형식으로 데이터가 들어감
    private Role role; // 일반인 또는 관리자

    private LocalDate regdate; // 등록일자

    public Member() {
    }

    public Member(Long id, String name, String email, String password, String address, Role role, LocalDate regdate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
        this.regdate = regdate;
    }

    }





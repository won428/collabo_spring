package com.coffee.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/*
테스트 시나리오
 최초 고객이 장바구니에 품목을 담으면
1. 카트 객체 생성
2. 카트 상품 객체 생성
 그 고객이 다른 상품을 장바구니에 담으면
 1. 카트 상품 객체 생성
 이전 카트 상품을 추가로 더 구매하면
 1. 이전 카트 상품을 갱신
 */

// 고객(Member)이 사용하는 카트 엔터티 클래스입니다.
@Getter @Setter @ToString
@Entity
@Table(name = "carts")
public class Cart {
    @Id // 이 컬럼은 primary key입니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_id") // 테이블에 생성되는 컬럼 이름
    private Long id; // 카트 아이디

    // 고객 1명이 한개의 카트를 사용합니다.
    @OneToOne(fetch = FetchType.LAZY) // 일대일 연관 관계 매핑, 지연 로딩
    @JoinColumn(name = "member_id")
    private Member member;

    // Cart 1개에는 여러개의 카트 상품들이 담겨질 수 있으므로, 컬렉션 형태로 작성해야 합니다.
    // CascadeType.ALL : 카트 엔터티에 변경/수정/삭제 등의 변동 사항이 발생하면, 카트상품 엔터티에 변동 사항을 똑같이 적용시킨다.
    // CascadeType과 관련하여 on delete set null(부모키가 삭제되면 자식에서 외래키를 null값으로 바꾸는것), on delete cascade(부모키가 삭제되면 자식에서 부모키 기반으로 작성된 데이터들을 전부 삭제하는것)
    // mappedBy 구문이 있는 곳은 '연관 관계'의 주인이 아니고 단지 읽기 전용, 매핑 정보만 가지고 있습니다.
    // 주의사항) "cart" : 연관 관계의 주인 엔터티에 들어 있는 변수명과 반드시 동일해야 합니다.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartProduct> cartProducts;

}

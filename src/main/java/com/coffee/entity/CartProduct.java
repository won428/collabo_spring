package com.coffee.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 장바구니에 담을 상품의 대한 정보를 가지고 있는 엔터티 클래스입니다.
@Getter @Setter @ToString
@Entity
@Table(name = "cart_products")
public class CartProduct {

    @Id // 이 컬럼은 primary key입니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_product_id") // 테이블에 생성되는 컬럼 이름
    private Long id; // 카트 상품 아이디, primary key

    // 카트 한개에게는 여러개의 '카트 상품'을 담을 수 있습니다.
    // 조인 컬럼에 명시한 "cart_id"는 외래키 입니다.
    // 이 컬럼은 primary key 이름을 그대로 복사해서 사용하면 됩니다.
    // mappedBy 구문이 없는 곳이 '연관 관계'의 주인이 되며 외래키를 관리해주는 주체입니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 동일 품목의 상품은 여러개의 '카트 상품'에 담겨질수 있습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity ; // 구매 수량
}

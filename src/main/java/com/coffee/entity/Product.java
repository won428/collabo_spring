package com.coffee.entity;

import com.coffee.constant.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "products") // 테이블 이름은 "members"로 설정하는 어노테이션

public class Product {
    // 엔터티 코딩 작성시 database의 제약 조건도 같이 고려해야 합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Min(message = "가격은 0원 이상이어야 합니다.", value = 100) // cf) @Max
    private int price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    @Min(message = "재고 수량은 10개 이상이어야 합니다.", value = 10)
    @Max(message = "재고 수량은 1000개 이하이어야 합니다.", value = 1000)
    private int stock;

    @Column(nullable = false)
    @NotBlank(message = "이미지는 필수 입력 사항입니다.")
    private String image;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "상품 설명은 필수 입력 사항입니다.")
    @Size(max = 1000, message = "상품에 대한 설명은 1,000자리 이하로만 입력할 수 있습니다. ")
    private String description;

    private LocalDate inputdate; // 입고일자
}

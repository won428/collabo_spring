package com.coffee.constant;

// 상품의 카테고리 정보를 나타내기 위한 열거형 상수입니다.
// 한글 이름도 같이 명시합니다.
public enum Category {
    BREAD("빵"), BEVERAGE("음료"), CAKE("케이크");

    private String description;

    public String getDescription() {
        return description;
    }

    Category(String description) {
        this.description = description;
    }
}



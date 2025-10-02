package com.coffee.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OrderListDto {
    private Long orderNumber;
    private String productName;
    private int stock;
    private int totalPrice;
}

package com.coffee.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class Element {
    private int id;
    private String name;
    private int price;
    private int stock;
    private String image;
    private String category;
    private String description;

    public Element(int id, String name, int price, int stock, String image, String category, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.category = category;
        this.description = description;
    }

    public Element(){

    }


}

package com.teamvoy.task.dto.goodsDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsRequest {
    private String name;
    private int amount;
    private double price;

    public GoodsRequest() {
    }

    public GoodsRequest(String name, int amount, double price) {
        this.name = name;
        this.amount = amount;
        this.price = price;
    }
}

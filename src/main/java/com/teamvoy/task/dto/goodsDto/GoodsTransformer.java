package com.teamvoy.task.dto.goodsDto;

import com.teamvoy.task.model.Product;

import java.util.Objects;

public class GoodsTransformer {
    public static Product convertToEntity(GoodsRequest goodsRequest) {
        Product product = new Product();
        product.setName(goodsRequest.getName());
        product.setAmount(goodsRequest.getAmount());
        product.setPrice(goodsRequest.getPrice());

        return product;
    }

    public static Product convertToEntityForUpdate(GoodsRequest goodsRequest, Product product) {
        if (goodsRequest.getName() != null && !goodsRequest.getName().isBlank() || !Objects.equals(goodsRequest.getName(), product.getName())) {
            product.setName(goodsRequest.getName());
        }
        if (!Objects.equals(goodsRequest.getAmount(), product.getAmount())) {
            product.setAmount(goodsRequest.getAmount());
        }
        if (!Objects.equals(goodsRequest.getPrice(), product.getPrice())) {
            product.setPrice(goodsRequest.getPrice());
        }

        return product;
    }
}

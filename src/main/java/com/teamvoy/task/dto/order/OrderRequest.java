package com.teamvoy.task.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    private long productId;
    private long amount;

    public OrderRequest() {
    }

    public OrderRequest(long productId, long amount) {
        this.productId = productId;
        this.amount = amount;
    }
}

package com.teamvoy.task.dto.order;

import com.teamvoy.task.dto.userDto.UserResponseForOrder;
import com.teamvoy.task.model.Order;
import com.teamvoy.task.model.OrderedProduct;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private long id;
    private LocalDateTime localDateTime;
    private UserResponseForOrder user;
    private List<OrderedProduct> goods;
    private double sum;
    private String status;

    public OrderResponse() {
    }

    public OrderResponse(Order order) {
        if (order != null) {
            this.id = order.getId();
            this.localDateTime = order.getLocalDateTime();
            this.user = new UserResponseForOrder(order.getUser());
            this.goods = order.getOrderedProducts();
            this.sum = order.getSum();
            this.status = order.getStatus().toString();
        }
    }
}

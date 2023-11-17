package com.teamvoy.task.dto.order;

import com.teamvoy.task.model.Order;
import com.teamvoy.task.model.OrderedProduct;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class OrderResponseForUser {
    private long id;
    private LocalDateTime localDateTime;
    private List<OrderedProduct> goods;
    private double sum;
    private String status;

    public OrderResponseForUser(Order order) {
        this.id = order.getId();
        this.localDateTime = order.getLocalDateTime();
        this.goods = order.getOrderedProducts();
        this.sum = order.getSum();
        this.status = order.getStatus().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderResponseForUser that = (OrderResponseForUser) o;
        return id == that.id && Double.compare(sum, that.sum) == 0 && Objects.equals(localDateTime, that.localDateTime) && Objects.equals(goods, that.goods) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, localDateTime, goods, sum, status);
    }
}

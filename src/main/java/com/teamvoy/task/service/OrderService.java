package com.teamvoy.task.service;

import com.teamvoy.task.dto.order.OrderRequest;
import com.teamvoy.task.model.Order;
import com.teamvoy.task.model.User;

import java.util.List;

public interface OrderService {
    Order create(Order order);

    Order readById(long id);

    Order update(Order order);

    void delete(long id);

    List<Order> getAll();

    void removeIfOrderNotPaid();

    void paidTheOrder(long id);

    List<Order> findByUserId(long id);

    Order prepareOrder(List<OrderRequest> orderRequests, User user);

    Order prepareOrderForUpdate(long orderId, List<OrderRequest> orderRequests);
}

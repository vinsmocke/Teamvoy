package com.teamvoy.task.repository;

import com.teamvoy.task.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.status = com.teamvoy.task.model.Status.NOT_PAID")
    List<Order> findUnpaidOrders();

    @Query(value = "SELECT * FROM orders where user_id = ?", nativeQuery = true)
    List<Order> findByUserId(long userId);
}

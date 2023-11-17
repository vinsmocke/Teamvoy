package com.teamvoy.task.controller;

import com.teamvoy.task.dto.order.OrderRequest;
import com.teamvoy.task.dto.order.OrderResponse;
import com.teamvoy.task.model.Order;
import com.teamvoy.task.model.User;
import com.teamvoy.task.service.OrderService;
import com.teamvoy.task.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private OrderService orderService;
    private UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("@check.isManager()")
    public List<OrderResponse> getAll() {
        return orderService.getAll().stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping()
    @PreAuthorize("@check.isManager() or @check.accessForOrder(#id)")
    public OrderResponse getById(@RequestParam long id) {
        return new OrderResponse(orderService.readById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@check.isManager() or @check.isOwner(#userId)")
    public OrderResponse create(@RequestBody @Valid List<OrderRequest> orderDto,
                                @RequestParam long userId) {
        User user = userService.readById(userId);
        Order order = orderService.prepareOrder(orderDto, user);

        return new OrderResponse(orderService.create(order));
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.confirmAccessOwnerOrManager(#userId)")
    public List<OrderResponse> findByUserId(@RequestParam long userId) {
        return orderService.findByUserId(userId).stream().map(OrderResponse::new).collect(Collectors.toList());
    }

    @PostMapping("/paying")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.accessForOrder(#orderId)")
    public ResponseEntity<String> paying(@RequestParam long orderId) {
        orderService.paidTheOrder(orderId);
        return ResponseEntity.accepted().body("Your order with id " + orderId + " has been paid");
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.accessForOrder(#orderId)")
    public OrderResponse update(@RequestParam long orderId, @RequestBody @Valid List<OrderRequest> orderDto) {
        return new OrderResponse(orderService.prepareOrderForUpdate(orderId, orderDto));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.accessForOrder(#orderId)")
    public ResponseEntity<String> removePaidOrder(@RequestParam long orderId) {
        orderService.delete(orderId);
        return ResponseEntity.accepted().body("Order with id " + orderId + " has been removed");
    }
}

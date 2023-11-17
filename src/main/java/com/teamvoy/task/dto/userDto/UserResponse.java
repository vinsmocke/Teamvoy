package com.teamvoy.task.dto.userDto;

import com.teamvoy.task.dto.order.OrderResponseForUser;
import com.teamvoy.task.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private double balance;
    private String role;
    private List<OrderResponseForUser> userOrders;

    public UserResponse(){

    }
    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.balance = user.getBalance();
        this.role = user.getRole().getName();
        if (user.getOrders() != null) {
            this.userOrders = user.getOrders().stream().map(OrderResponseForUser::new).distinct().collect(Collectors.toList());
        } else
            userOrders = new ArrayList<>();
    }
}
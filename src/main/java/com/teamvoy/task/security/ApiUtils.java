package com.teamvoy.task.security;

import com.teamvoy.task.model.Order;
import com.teamvoy.task.model.User;
import com.teamvoy.task.service.OrderService;
import com.teamvoy.task.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("check")
public class ApiUtils {
    private OrderService orderService;
    private UserService userService;

    @Autowired
    public ApiUtils(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    public boolean isManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().
                stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equalsIgnoreCase("ROLE_MANAGER"));
    }

    public boolean isClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().
                stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equalsIgnoreCase("ROLE_CLIENT"));
    }

    public boolean isOwner(long userId) {
        User currentUser = userService.readById(userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName().equals(currentUser.getEmail());
    }

    public boolean confirmAccessOwnerOrManager(long userId) {
        return isManager() || isOwner(userId);
    }

    public boolean accessForOrder(long orderId) {
        Order order = orderService.readById(orderId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName().equals(order.getUser().getEmail());
    }
}

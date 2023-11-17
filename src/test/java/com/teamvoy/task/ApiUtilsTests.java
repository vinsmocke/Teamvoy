package com.teamvoy.task;

import com.teamvoy.task.model.Order;
import com.teamvoy.task.model.User;
import com.teamvoy.task.security.ApiUtils;
import com.teamvoy.task.service.OrderService;
import com.teamvoy.task.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ApiUtilsTests {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ApiUtils apiUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsManager() {
        setAuthenticationWithRole("ROLE_MANAGER");

        assertTrue(apiUtils.isManager());
    }

    @Test
    public void testIsClient() {
        setAuthenticationWithRole("ROLE_CLIENT");

        assertTrue(apiUtils.isClient());
    }

    @Test
    public void testIsOwner() {
        long userId = 1L;
        User currentUser = new User();
        currentUser.setEmail("test@example.com");
        when(userService.readById(userId)).thenReturn(currentUser);
        setAuthenticationWithUsername("test@example.com");

        assertTrue(apiUtils.isOwner(userId));
    }

    @Test
    public void testConfirmAccessOwnerOrManager_Manager() {
        setAuthenticationWithRole("ROLE_MANAGER");

        assertTrue(apiUtils.confirmAccessOwnerOrManager(1L));
    }

    @Test
    public void testConfirmAccessOwnerOrManager_Owner() {
        long userId = 1L;
        User currentUser = new User();
        currentUser.setEmail("test@example.com");
        when(userService.readById(userId)).thenReturn(currentUser);
        setAuthenticationWithUsername("test@example.com");

        assertTrue(apiUtils.confirmAccessOwnerOrManager(userId));
    }

    @Test
    public void testAccessForOrder() {
        long orderId = 1L;
        Order order = new Order();
        User user = new User();
        user.setEmail("test@example.com");
        order.setUser(user);
        when(orderService.readById(orderId)).thenReturn(order);
        setAuthenticationWithUsername("test@example.com");

        assertTrue(apiUtils.accessForOrder(orderId));
    }

    private void setAuthenticationWithRole(String role) {
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password",
                Collections.singletonList(() -> role));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setAuthenticationWithUsername(String username) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, "password",
                Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

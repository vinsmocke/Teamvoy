package com.teamvoy.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamvoy.task.dto.order.OrderRequest;
import com.teamvoy.task.model.Order;
import com.teamvoy.task.model.Role;
import com.teamvoy.task.model.Status;
import com.teamvoy.task.model.User;
import com.teamvoy.task.service.OrderService;
import com.teamvoy.task.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderControllerTests {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testGetAll() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(role);

        long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(Status.NOT_PAID);

        Order order1 = new Order();
        order1.setId(2L);
        order1.setUser(user);
        order1.setStatus(Status.NOT_PAID);

        List<Order> orderList = List.of(order, order1);
        when(orderService.getAll()).thenReturn(orderList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(orderList.size()));

        verify(orderService, times(1)).getAll();
    }

    @Test
    public void testGetById() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(role);

        long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(Status.NOT_PAID);
        when(orderService.readById(orderId)).thenReturn(order);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                        .param("id", String.valueOf(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));

        verify(orderService, times(1)).readById(orderId);
    }

    @Test
    public void testCreate() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(role);
        List<OrderRequest> orderRequests = Arrays.asList(new OrderRequest(), new OrderRequest());
        Order createdOrder = new Order();
        createdOrder.setId(1L);
        createdOrder.setUser(user);
        createdOrder.setStatus(Status.NOT_PAID);
        user.setOrders(List.of(createdOrder));
        when(userService.readById(userId)).thenReturn(user);
        when(orderService.prepareOrder(anyList(), any(User.class))).thenReturn(createdOrder);
        when(orderService.create(any(Order.class))).thenReturn(createdOrder);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .param("userId", String.valueOf(userId))
                        .content(asJsonString(orderRequests))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        verify(userService, times(1)).readById(userId);
        verify(orderService, times(1)).prepareOrder(anyList(), any(User.class));
        verify(orderService, times(1)).create(any(Order.class));
    }

    @Test
    public void testFindByUserId() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(role);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(Status.NOT_PAID);

        Order order2 = new Order();
        order2.setUser(user);
        order2.setStatus(Status.PAID);
        order2.setId(2L);

        List<Order> orderList = Arrays.asList(order, order2);
        when(orderService.findByUserId(userId)).thenReturn(orderList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/users")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.length()").value(orderList.size()));

        verify(orderService, times(1)).findByUserId(userId);
    }

    @Test
    public void testPaying() throws Exception {
        long orderId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/paying")
                        .param("orderId", String.valueOf(orderId)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Your order with id " + orderId + " has been paid"));

        verify(orderService, times(1)).paidTheOrder(orderId);
    }

    @Test
    void testUpdateOrder() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(role);

        long orderId = 1L;
        List<OrderRequest> orderDto = Collections.singletonList(new OrderRequest());
        Order updatedOrder = new Order();
        updatedOrder.setLocalDateTime(LocalDateTime.now());
        updatedOrder.setId(1L);
        updatedOrder.setStatus(Status.NOT_PAID);
        updatedOrder.setUser(user);
        when(orderService.prepareOrderForUpdate(any(Long.class), any(List.class))).thenReturn(updatedOrder);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/orders")
                        .param("orderId", String.valueOf(orderId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedOrder.getId()))
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.sum").value(updatedOrder.getSum()))
                .andExpect(jsonPath("$.status").value(updatedOrder.getStatus().toString()));
    }

    @Test
    public void testRemovePaidOrder() throws Exception {
        long orderId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders")
                        .param("orderId", String.valueOf(orderId)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Order with id " + orderId + " has been removed"));

        verify(orderService, times(1)).delete(orderId);
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

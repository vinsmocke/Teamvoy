package com.teamvoy.task.service;

import com.teamvoy.task.dto.order.OrderRequest;
import com.teamvoy.task.exception.EntityNotFoundException;
import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.*;
import com.teamvoy.task.repository.OrderRepository;
import com.teamvoy.task.repository.OrderedProductRepository;
import com.teamvoy.task.repository.ProductRepository;
import com.teamvoy.task.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderedProductRepository orderedProductRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrderWithValidOrder() {
        Order order = new Order();
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.create(order);

        assertNotNull(createdOrder);
        verify(orderRepository).save(order);
    }

    @Test
    public void testCreateOrderWithNull() {
        assertThrows(NullEntityReferenceException.class, () -> orderService.create(null));
    }

    @Test
    public void testReadByIdWithValidId() {
        long id = 1L;
        Order order = new Order();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.readById(id);

        assertNotNull(foundOrder);
        assertEquals(order, foundOrder);
    }

    @Test
    public void testReadByIdWithInvalidId() {
        long id = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.readById(id));
    }

    @Test
    public void testPrepareOrderWithValidRequest() {
        User user = new User();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setAmount(10);
        product.setPrice(100.0);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId(1L);
        orderRequest.setAmount(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Order resultOrder = orderService.prepareOrder(Arrays.asList(orderRequest), user);

        assertNotNull(resultOrder);
        assertEquals(Status.NOT_PAID, resultOrder.getStatus());
        assertFalse(resultOrder.getOrderedProducts().isEmpty());
        assertEquals(500.0, resultOrder.getSum());

        verify(productRepository).findById(1L);
        verify(orderedProductRepository).saveAll(any());
        verify(productRepository).saveAll(any());
    }

    @Test
    public void testPrepareOrderForUpdateWithValidData() {
        long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(Status.NOT_PAID);
        existingOrder.setSum(250);
        List<OrderedProduct> orderedProducts = new ArrayList<>();
        orderedProducts.add(new OrderedProduct(1l, "Product", 10, 25.0));
        existingOrder.setOrderedProducts(orderedProducts);

        Product product = new Product();
        product.setId(1L);
        product.setAmount(10);
        product.setPrice(40.0);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId(1L);
        orderRequest.setAmount(5);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertEquals(250, existingOrder.getSum());
        assertEquals(10, existingOrder.getOrderedProducts().get(0).getAmount());
        orderService.prepareOrderForUpdate(orderId, Arrays.asList(orderRequest));

        assertEquals(200, existingOrder.getSum());
        assertEquals(5, existingOrder.getOrderedProducts().get(0).getAmount());

        assertNotNull(existingOrder);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void testPrepareOrderForUpdateWithInvalidOrderId() {
        long invalidOrderId = 99L;
        when(orderRepository.findById(invalidOrderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            orderService.prepareOrderForUpdate(invalidOrderId, Arrays.asList(new OrderRequest()));
        });

        verify(orderRepository).findById(invalidOrderId);
        verify(productRepository, never()).findById(anyLong());
        verify(orderedProductRepository, never()).save(any());
    }

}

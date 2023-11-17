package com.teamvoy.task.service.impl;

import com.teamvoy.task.dto.order.OrderRequest;
import com.teamvoy.task.exception.EntityNotFoundException;
import com.teamvoy.task.exception.NotEnoughAmountException;
import com.teamvoy.task.exception.NotEnoughBalanceException;
import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.*;
import com.teamvoy.task.repository.OrderRepository;
import com.teamvoy.task.repository.OrderedProductRepository;
import com.teamvoy.task.repository.ProductRepository;
import com.teamvoy.task.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, OrderedProductRepository orderedProductRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderedProductRepository = orderedProductRepository;
    }

    @Override
    public Order create(Order order) {
        if (order != null) {
            return orderRepository.save(order);
        }
        throw new NullEntityReferenceException("Order cannot be 'null'");
    }

    @Override
    public Order readById(long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order with id " + id + " not found!"));
    }

    @Override
    public Order update(Order order) {
        if (order != null) {
            readById(order.getId());
            return orderRepository.save(order);
        }
        throw new NullEntityReferenceException("Order cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        orderRepository.delete(readById(id));
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    @Scheduled(fixedDelay = 300000)
    public void removeIfOrderNotPaid() {
        List<Order> unpaidOrders = orderRepository.findUnpaidOrders();

        LocalDateTime currentTime = LocalDateTime.now();
        unpaidOrders.stream()
                .filter(order -> Duration.between(order.getLocalDateTime(), currentTime).toMinutes() >= 10)
                .forEach(order -> {
                    if (order.getStatus() == Status.NOT_PAID) {
                        List<OrderedProduct> orderedProducts = order.getOrderedProducts();
                        for (OrderedProduct orderedProduct : orderedProducts) {
                            Product product = productRepository.findById(orderedProduct.getId())
                                    .orElseThrow(() -> new EntityNotFoundException("Product with id " + orderedProduct.getId() + " not found!"));
                            product.setAmount(product.getAmount() + orderedProduct.getAmount());
                            productRepository.save(product);
                        }
                    }
                    orderRepository.delete(order);
                });
    }

    @Override
    public void paidTheOrder(long id) {
        Order order = readById(id);
        User user = order.getUser();
        if (user.getBalance() >= order.getSum()) {
            user.setBalance(user.getBalance() - order.getSum());
            order.setStatus(Status.PAID);
            orderRepository.save(order);
        } else
            throw new NotEnoughBalanceException("You do not have enough balance to pay for the order");
    }

    @Override
    public List<Order> findByUserId(long id) {
        return orderRepository.findByUserId(id);
    }

    @Override
    public Order prepareOrder(List<OrderRequest> orderRequests, User user) {
        List<OrderedProduct> productsFromRequest = new ArrayList<>();
        List<Product> productsFromDB = new ArrayList<>();

        for (OrderRequest orderRequest : orderRequests) {
            Product product = productRepository.findById(orderRequest.getProductId()).orElseThrow(
                    () -> new EntityNotFoundException("Product with id " + orderRequest.getProductId() + " not found!"));
            long requestedAmount = orderRequest.getAmount();

            if (checkIfEnoughAmount(product, requestedAmount)) {
                product.setAmount(product.getAmount() - requestedAmount);

                OrderedProduct orderedProduct = new OrderedProduct();
                orderedProduct.setId(product.getId());
                orderedProduct.setName(product.getName());
                orderedProduct.setAmount(requestedAmount);
                orderedProduct.setPrice(product.getPrice());

                productsFromDB.add(product);
                productsFromRequest.add(orderedProduct);
            } else {
                productsFromRequest.clear();
                productsFromDB.clear();
                throw new NotEnoughAmountException("Insufficient stock for item: " + product.getName());
            }
        }
        orderedProductRepository.saveAll(productsFromRequest);
        productRepository.saveAll(productsFromDB);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.NOT_PAID);
        order.setOrderedProducts(productsFromRequest);
        order.setLocalDateTime(LocalDateTime.now());
        order.setSum(productsFromRequest.stream().mapToDouble(value -> value.getPrice() * value.getAmount()).sum());

        return order;
    }

    @Override
    public Order prepareOrderForUpdate(long orderId, List<OrderRequest> orderRequests) {
        Order existingOrder = readById(orderId);

        if (existingOrder.getStatus() == Status.NOT_PAID) {
            double newSum = orderRequests.stream()
                    .mapToDouble(orderRequest -> {
                        Product product = productRepository.findById(orderRequest.getProductId())
                                .orElseThrow(() -> new EntityNotFoundException("Product with id " + orderRequest.getProductId() + " not found!"));
                        return product.getPrice() * orderRequest.getAmount();
                    })
                    .sum();

            existingOrder.setSum(newSum);
            List<OrderedProduct> updatedOrderedProducts = new ArrayList<>();

            for (OrderRequest orderRequest : orderRequests) {
                Product product = productRepository.findById(orderRequest.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Product with id " + orderRequest.getProductId() + " not found!"));

                OrderedProduct existingOrderedProduct = existingOrder.getOrderedProducts().stream()
                        .filter(orderedProduct -> orderedProduct.getId().equals(product.getId()))
                        .findFirst()
                        .orElse(new OrderedProduct());

                if (checkIfEnoughAmount(product, orderRequest.getAmount())) {
                    if (orderRequest.getAmount() > 1) {
                        product.setAmount(product.getAmount() - (orderRequest.getAmount() - existingOrderedProduct.getAmount()));
                    } else
                        product.setAmount(product.getAmount() - orderRequest.getAmount());

                    existingOrderedProduct.setId(product.getId());
                    existingOrderedProduct.setName(product.getName());
                    existingOrderedProduct.setAmount(orderRequest.getAmount());
                    existingOrderedProduct.setPrice(product.getPrice());

                    updatedOrderedProducts.add(existingOrderedProduct);
                    orderedProductRepository.save(existingOrderedProduct);
                } else
                    throw new NotEnoughAmountException("Insufficient stock for item: " + product.getName());

            }
            existingOrder.setOrderedProducts(updatedOrderedProducts);

            return orderRepository.save(existingOrder);
        } else {
            throw new UnsupportedOperationException("Updating orders with status " + existingOrder.getStatus() + " is not allowed.");
        }
    }

    private boolean checkIfEnoughAmount(Product product, long requestAmount) {
        return product.getAmount() >= requestAmount;
    }
}

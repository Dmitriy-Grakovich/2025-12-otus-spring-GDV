package ru.diasoft.tasklesson23.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderItem;
import ru.diasoft.tasklesson23.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// OrderController.java

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final MessageChannel orderInputChannel;

    @PostMapping
    public Order submitOrder(@RequestBody OrderRequest request) {
        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(request.getCustomerId())
                .items(request.getItems())
                .totalAmount(request.getTotalAmount())
                .status(OrderStatus.NEW)
                .shippingAddress(request.getShippingAddress())
                .createdDate(LocalDateTime.now())
                .build();

        Message<Order> message = MessageBuilder.withPayload(order).build();
        orderInputChannel.send(message);

        return order;
    }

    @GetMapping("/test")
    public Order testOrder() {
        OrderItem item = new OrderItem("prod-123", "Test Product", 2, 50.0);

        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId("cust-001")
                .items(List.of(item))
                .totalAmount(100.0)
                .status(OrderStatus.NEW)
                .shippingAddress("123 Main St, City, Country")
                .createdDate(LocalDateTime.now())
                .build();

        Message<Order> message = MessageBuilder.withPayload(order).build();
        orderInputChannel.send(message);

        return order;
    }

    // DTO для запроса
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OrderRequest {
        private String customerId;
        private List<OrderItem> items;
        private Double totalAmount;
        private String shippingAddress;
    }
}
package ru.diasoft.tasklesson23.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final MessageChannel orderInputChannel;
    private final Counter orderSubmittedCounter;

    public OrderController(MessageChannel orderInputChannel, MeterRegistry meterRegistry) {
        this.orderInputChannel = orderInputChannel;
        this.orderSubmittedCounter = Counter.builder("orders.submitted")
                .description("Number of orders submitted")
                .tag("controller", "order")
                .register(meterRegistry);
    }

    @PostMapping
    public Order submitOrder(@RequestBody OrderRequest request) {
        orderSubmittedCounter.increment();
        log.info("Submitting order for customer: {}", request.getCustomerId());

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
        orderSubmittedCounter.increment();
        log.info("Submitting test order");

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
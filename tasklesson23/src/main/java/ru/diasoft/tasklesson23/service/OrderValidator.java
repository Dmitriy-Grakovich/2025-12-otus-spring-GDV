package ru.diasoft.tasklesson23.service;

import org.springframework.stereotype.Service;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderStatus;
@Service
public class OrderValidator {

    public Order validate(Order order) {
        System.out.println("Validating order: " + order.getOrderId());

        // Простая валидация
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order items cannot be empty");
        }

        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new RuntimeException("Total amount must be positive");
        }

        order.setStatus(OrderStatus.VALIDATED);
        return order;
    }
}
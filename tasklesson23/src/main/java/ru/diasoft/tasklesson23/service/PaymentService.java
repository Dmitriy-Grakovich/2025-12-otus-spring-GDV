package ru.diasoft.tasklesson23.service;

import org.springframework.stereotype.Service;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderStatus;

@Service
public class PaymentService {

    public Order processPayment(Order order) {
        System.out.println("💳 Processing payment for order: " + order.getOrderId());

        try {
            Thread.sleep(1000); // Имитация обработки

            // 80% успешных платежей
            boolean success = Math.random() > 0.2;

            if (success) {
                order.setStatus(OrderStatus.PAYMENT_PROCESSED);
                System.out.println("✅ Payment successful for order: " + order.getOrderId());
            } else {
                order.setStatus(OrderStatus.PAYMENT_FAILED);
                System.out.println("❌ Payment failed for order: " + order.getOrderId());
            }

            return order;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted", e);
        }
    }
}
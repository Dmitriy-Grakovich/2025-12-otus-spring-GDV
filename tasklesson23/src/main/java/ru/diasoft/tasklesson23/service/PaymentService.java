package ru.diasoft.tasklesson23.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderStatus;

@Slf4j
@Service
public class PaymentService {

    private final Counter paymentSuccessCounter;
    private final Counter paymentFailureCounter;
    private final Timer paymentProcessingTimer;

    public PaymentService(MeterRegistry meterRegistry) {
        this.paymentSuccessCounter = Counter.builder("payment.success")
                .description("Number of successful payments")
                .tag("service", "payment")
                .register(meterRegistry);

        this.paymentFailureCounter = Counter.builder("payment.failure")
                .description("Number of failed payments")
                .tag("service", "payment")
                .register(meterRegistry);

        this.paymentProcessingTimer = Timer.builder("payment.processing.time")
                .description("Payment processing time")
                .tag("service", "payment")
                .register(meterRegistry);
    }

    public Order processPayment(Order order) {
        log.info("💳 Processing payment for order: {}", order.getOrderId());

        return paymentProcessingTimer.record(() -> {
            try {
                Thread.sleep(1000); // Имитация обработки

                // 80% успешных платежей
                boolean success = Math.random() > 0.2;

                if (success) {
                    order.setStatus(OrderStatus.PAYMENT_PROCESSED);
                    paymentSuccessCounter.increment();
                    log.info("✅ Payment successful for order: {}", order.getOrderId());
                } else {
                    order.setStatus(OrderStatus.PAYMENT_FAILED);
                    paymentFailureCounter.increment();
                    log.warn("❌ Payment failed for order: {}", order.getOrderId());
                }

                return order;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                paymentFailureCounter.increment();
                throw new RuntimeException("Payment processing interrupted", e);
            }
        });
    }
}
package ru.diasoft.tasklesson23.service;

import org.springframework.stereotype.Service;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderStatus;

@Service
public class ShippingService {

    public Order scheduleShipping(Order order) {
        System.out.println("🚚 Scheduling shipping for order: " + order.getOrderId());

        if (order.getStatus() == OrderStatus.SHIPPING_SCHEDULED) {
            order.setStatus(OrderStatus.COMPLETED);
            System.out.println("🎉 Order marked as completed: " + order.getOrderId());
        }

        return order;
    }
}
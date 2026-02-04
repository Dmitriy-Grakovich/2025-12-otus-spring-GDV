package ru.diasoft.tasklesson23.service;

import org.springframework.stereotype.Service;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderStatus;

@Service
public class InventoryService {

    public Order reserveInventory(Order order) {
        System.out.println("📦 Reserving inventory for order: " + order.getOrderId());

        try {
            Thread.sleep(800); // Имитация обработки

            // 90% успешного резервирования
            boolean success = Math.random() > 0.1;

            if (success) {
                order.setStatus(OrderStatus.INVENTORY_RESERVED);
                System.out.println("✅ Inventory reserved for order: " + order.getOrderId());
            } else {
                order.setStatus(OrderStatus.INVENTORY_FAILED);
                System.out.println("❌ Inventory reservation failed for order: " + order.getOrderId());
            }

            return order;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Inventory processing interrupted", e);
        }
    }
}
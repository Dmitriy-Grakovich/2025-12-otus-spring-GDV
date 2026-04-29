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
public class InventoryService {

    private final Counter inventorySuccessCounter;
    private final Counter inventoryFailureCounter;
    private final Timer inventoryProcessingTimer;

    public InventoryService(MeterRegistry meterRegistry) {
        this.inventorySuccessCounter = Counter.builder("inventory.success")
                .description("Number of successful inventory reservations")
                .tag("service", "inventory")
                .register(meterRegistry);

        this.inventoryFailureCounter = Counter.builder("inventory.failure")
                .description("Number of failed inventory reservations")
                .tag("service", "inventory")
                .register(meterRegistry);

        this.inventoryProcessingTimer = Timer.builder("inventory.processing.time")
                .description("Inventory processing time")
                .tag("service", "inventory")
                .register(meterRegistry);
    }

    public Order reserveInventory(Order order) {
        log.info("📦 Reserving inventory for order: {}", order.getOrderId());

        return inventoryProcessingTimer.record(() -> {
            try {
                Thread.sleep(800); // Имитация обработки

                // 90% успешного резервирования
                boolean success = Math.random() > 0.1;

                if (success) {
                    order.setStatus(OrderStatus.INVENTORY_RESERVED);
                    inventorySuccessCounter.increment();
                    log.info("✅ Inventory reserved for order: {}", order.getOrderId());
                } else {
                    order.setStatus(OrderStatus.INVENTORY_FAILED);
                    inventoryFailureCounter.increment();
                    log.warn("❌ Inventory reservation failed for order: {}", order.getOrderId());
                }

                return order;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                inventoryFailureCounter.increment();
                throw new RuntimeException("Inventory processing interrupted", e);
            }
        });
    }
}
package ru.diasoft.tasklesson23.health;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Кастомный Health Indicator для проверки состояния внешних сервисов
 * (Payment Gateway, Inventory System)
 */
@Component
@RequiredArgsConstructor
public class ExternalServicesHealthIndicator implements HealthIndicator {

    private final MeterRegistry meterRegistry;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", LocalDateTime.now().toString());

        // Получаем метрики успешности операций
        double paymentSuccessCount = getCounterValue("payment.success");
        double paymentFailureCount = getCounterValue("payment.failure");
        double inventorySuccessCount = getCounterValue("inventory.success");
        double inventoryFailureCount = getCounterValue("inventory.failure");

        // Вычисляем процент успешности
        double paymentSuccessRate = calculateSuccessRate(paymentSuccessCount, paymentFailureCount);
        double inventorySuccessRate = calculateSuccessRate(inventorySuccessCount, inventoryFailureCount);

        details.put("paymentGateway", Map.of(
                "successCount", paymentSuccessCount,
                "failureCount", paymentFailureCount,
                "successRate", String.format("%.2f%%", paymentSuccessRate),
                "status", getServiceStatus(paymentSuccessRate)
        ));

        details.put("inventorySystem", Map.of(
                "successCount", inventorySuccessCount,
                "failureCount", inventoryFailureCount,
                "successRate", String.format("%.2f%%", inventorySuccessRate),
                "status", getServiceStatus(inventorySuccessRate)
        ));

        // Определяем общий статус
        String overallStatus = determineOverallStatus(paymentSuccessRate, inventorySuccessRate);
        details.put("overallStatus", overallStatus);

        // Возвращаем health status
        if ("HEALTHY".equals(overallStatus)) {
            return Health.up().withDetails(details).build();
        } else if ("DEGRADED".equals(overallStatus)) {
            return Health.status("DEGRADED").withDetails(details).build();
        } else {
            return Health.down().withDetails(details).build();
        }
    }

    private double getCounterValue(String counterName) {
        try {
            var counter = meterRegistry.find(counterName).counter();
            return counter != null ? counter.count() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double calculateSuccessRate(double successCount, double failureCount) {
        double total = successCount + failureCount;
        if (total == 0) {
            return 100.0; // Если операций не было, считаем систему здоровой
        }
        return (successCount / total) * 100.0;
    }

    private String getServiceStatus(double successRate) {
        if (successRate >= 90.0) {
            return "HEALTHY";
        } else if (successRate >= 70.0) {
            return "DEGRADED";
        } else {
            return "UNHEALTHY";
        }
    }

    private String determineOverallStatus(double paymentSuccessRate, double inventorySuccessRate) {
        double minRate = Math.min(paymentSuccessRate, inventorySuccessRate);

        if (minRate >= 90.0) {
            return "HEALTHY";
        } else if (minRate >= 70.0) {
            return "DEGRADED";
        } else {
            return "UNHEALTHY";
        }
    }
}

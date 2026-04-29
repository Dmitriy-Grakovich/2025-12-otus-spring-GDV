package ru.diasoft.tasklesson23.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Кастомный Health Indicator для проверки состояния системы обработки заказов
 */
@Component
@RequiredArgsConstructor
public class OrderProcessingHealthIndicator implements HealthIndicator {

    private final MessageChannel orderInputChannel;
    private final MessageChannel validatedOrderChannel;
    private final MessageChannel paymentResultChannel;
    private final MessageChannel inventoryResultChannel;

    private static final long MAX_PROCESSING_TIME_MS = 5000;
    private long lastCheckTime = System.currentTimeMillis();
    private int healthCheckCounter = 0;

    @Override
    public Health health() {
        healthCheckCounter++;
        long currentTime = System.currentTimeMillis();
        long timeSinceLastCheck = currentTime - lastCheckTime;
        lastCheckTime = currentTime;

        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", LocalDateTime.now().toString());
        details.put("healthCheckCount", healthCheckCounter);
        details.put("timeSinceLastCheck", timeSinceLastCheck + "ms");

        // Проверяем доступность каналов
        boolean channelsAvailable = checkChannelsAvailability(details);

        // Проверяем производительность
        boolean performanceOk = timeSinceLastCheck < MAX_PROCESSING_TIME_MS;
        details.put("performanceStatus", performanceOk ? "OK" : "DEGRADED");
        details.put("maxProcessingTime", MAX_PROCESSING_TIME_MS + "ms");

        // Добавляем информацию о каналах
        details.put("channels", Map.of(
                "orderInputChannel", getChannelStatus(orderInputChannel),
                "validatedOrderChannel", getChannelStatus(validatedOrderChannel),
                "paymentResultChannel", getChannelStatus(paymentResultChannel),
                "inventoryResultChannel", getChannelStatus(inventoryResultChannel)
        ));

        if (channelsAvailable && performanceOk) {
            return Health.up()
                    .withDetails(details)
                    .build();
        } else if (channelsAvailable) {
            return Health.status("DEGRADED")
                    .withDetails(details)
                    .build();
        } else {
            return Health.down()
                    .withDetails(details)
                    .build();
        }
    }

    private boolean checkChannelsAvailability(Map<String, Object> details) {
        try {
            boolean allChannelsAvailable = orderInputChannel != null
                    && validatedOrderChannel != null
                    && paymentResultChannel != null
                    && inventoryResultChannel != null;

            details.put("channelsAvailable", allChannelsAvailable);
            return allChannelsAvailable;
        } catch (Exception e) {
            details.put("channelsAvailable", false);
            details.put("error", e.getMessage());
            return false;
        }
    }

    private String getChannelStatus(MessageChannel channel) {
        if (channel == null) {
            return "NOT_AVAILABLE";
        }
        return "AVAILABLE";
    }
}

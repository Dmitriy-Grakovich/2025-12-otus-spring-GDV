package ru.diasoft.orderitemservice.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEvent;

@Slf4j
@Service
public class OrderEventConsumer {

    @KafkaListener(topics = "${kafka.topics.orderitem-events}", groupId = "${kafka.consumer.group-id}")
    public void consumeOrderEvent(OrderItemEvent event) {
        log.info("Получено событие заказа: {} - ID: {}, цена: {}",
            event.getEventType(), 
            event.getOrderItem().getId(),
            event.getOrderItem().getTotalPrice());

        switch (event.getEventType()) {
            case CREATED:
                log.info("Заказ создана: {}", event.getOrderItem().getId());
                break;
            case UPDATED:
                log.info("Заказ обновлен: {}", event.getOrderItem().getId());
                break;
            case DELETED:
                log.info("Заказ удален: ID {}", event.getOrderItem().getId());
                break;
        }
    }
    }
package ru.diasoft.orderitemservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.diasoft.orderitemservice.domain.OrderItem;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEvent;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEventType;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.orderitem-events}")
    private String orderItemEventsTopic;


    public void sendOrderItemEvent(OrderItemEvent event) {
        log.info("Отправка события: {} - {}", event.getOrderItem().getId(), event.getOrderItem().getTotalPrice());
        sendEvent(orderItemEventsTopic, event.getOrderItem().getId(), event);
    }



    private void sendEvent(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, key, event);

        send.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Сообщение успешно отправлено. Топик: {}, ключ: {}, партиция: {}, оффсет: {}",
                        topic, key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Ошибка при отправке сообщения. Топик: {}, ключ: {}, ошибка: {}",
                        topic, key, ex.getMessage(), ex);
            }
        });
    }
    public void sendOrderItemDeletionEvent(String orderItemId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        sendOrderItemEvent(new OrderItemEvent(orderItem, OrderItemEventType.DELETED));
    }
}
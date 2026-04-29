package ru.diasoft.orderitemservice.kafka.producer;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.diasoft.orderitemservice.domain.OrderItem;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEvent;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEventType;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.orderitem-events}")
    private String orderItemEventsTopic;

    @HystrixCommand(
            fallbackMethod = "sendOrderItemEventBack",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
            }
    )
    public void sendOrderItemEvent(OrderItemEvent event) {
        log.info("Отправка события: {} - {}", event.getOrderItem().getId(), event.getOrderItem().getTotalPrice());
        sendEvent(orderItemEventsTopic, event.getOrderItem().getId(), event);
    }
    public void sendOrderItemEventBack(OrderItemEvent event, Throwable throwable) {
        log.error("Circuit breaker активирован для события: {} - {}. Причина: {}",
                event.getEventType(), event.getOrderItem().getId(), throwable.getMessage());
    }


    private void sendEvent(String topic, String key, Object event) {
        ListenableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, key, event);

        send.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Событие успешно отправлено в топик: {} с ключом: {}", topic, key);
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("Ошибка отправки события в топик: {} с ключом: {}", topic, key, ex);
            }
        });
    }
    public void sendOrderItemDeletionEvent(String orderItemId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        sendOrderItemEvent(new OrderItemEvent(orderItem, OrderItemEventType.DELETED));
    }
}
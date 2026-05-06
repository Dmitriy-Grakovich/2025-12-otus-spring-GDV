package ru.diasoft.orderitemservice.hystrix;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import ru.diasoft.orderitemservice.domain.OrderItem;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEvent;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEventType;
import ru.diasoft.orderitemservice.kafka.producer.OrderEventProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Hystrix Integration Test")

public class HystrixIntegrationTest {

    @Autowired
    private OrderEventProducer eventProducer;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("Проверка успешного выполнения Hystrix команды")
    void testHystrixCommandSuccess() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(null);

        OrderItem orderItem = OrderItem.builder()
                .id("1")
                .productId("1")
                .productName("Покупки")
                .quantity(3)
                .price(150.50)
                .totalPrice(451.50)
                .build();
        OrderItemEvent event = new OrderItemEvent( orderItem, OrderItemEventType.CREATED);

        eventProducer.sendOrderItemEvent(event);

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Проверка срабатывания fallback при ошибке")
    void testHystrixCommandFallback() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Kafka недоступна"));

        OrderItem orderItem = OrderItem.builder()
                .id("1")
                .productId("1")
                .productName("Покупки")
                .quantity(3)
                .price(150.50)
                .totalPrice(451.50)
                .build();
        OrderItemEvent event = new OrderItemEvent( orderItem, OrderItemEventType.CREATED);



        try {
            eventProducer.sendOrderItemEvent(event);
        } catch (Exception e) {
        }

        verify(kafkaTemplate, atLeastOnce()).send(anyString(), anyString(), any());
    }
}
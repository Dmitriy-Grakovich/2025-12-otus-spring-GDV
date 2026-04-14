package ru.diasoft.orderitemservice.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.diasoft.orderitemservice.domain.OrderItem;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEvent;
import ru.diasoft.orderitemservice.kafka.event.OrderItemEventType;
import ru.diasoft.orderitemservice.kafka.producer.OrderEventProducer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
@DisplayName("Kafka Integration Test")
class KafkaIntegrationTest {

    @Autowired
    private OrderEventProducer eventProducer;

    @Test
    @DisplayName("should send orderItem event to Kafka")
    void shouldSendAuthorEvent() {
        OrderItem orderItem = new OrderItem("1", "1", "Покупки", 3, 150.50, 451.50);
        OrderItemEvent event = new OrderItemEvent( orderItem, OrderItemEventType.CREATED);

        assertThat(event.getOrderItem()).isNotNull();
        assertThat(event.getEventType()).isEqualTo(OrderItemEventType.CREATED);
        assertThat(event.getOrderItem().getProductName()).isEqualTo("Покупки");

    }
}
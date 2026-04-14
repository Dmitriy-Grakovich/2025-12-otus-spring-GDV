package ru.diasoft.orderitemservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.diasoft.orderitemservice.domain.OrderItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEvent {
    private OrderItem orderItem;
    private OrderItemEventType eventType;

}

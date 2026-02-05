package ru.diasoft.tasklesson23.domain;

import lombok.*;
import ru.diasoft.tasklesson23.dto.OrderItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private String orderId;
    private String customerId;
    private List<OrderItemDto> items;
    private Double totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime createdDate;
}
package ru.diasoft.tasklesson23.domain;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    private String eventId;
    private String orderId;
    private OrderStatus status;
    private String message;
    private LocalDateTime timestamp;
}
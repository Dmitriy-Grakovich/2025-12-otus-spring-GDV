package ru.diasoft.orderitemservice.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}

package ru.diasoft.orderitemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
}

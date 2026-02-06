package ru.diasoft.orderitemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}

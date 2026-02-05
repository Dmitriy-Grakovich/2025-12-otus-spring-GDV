package ru.diasoft.tasklesson23.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemSimpleDto {
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;
}

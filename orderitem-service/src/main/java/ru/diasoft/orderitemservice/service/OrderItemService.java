package ru.diasoft.orderitemservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.diasoft.orderitemservice.domain.OrderItem;
import ru.diasoft.orderitemservice.dto.OrderItemRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OrderItemService {
    
    private final Map<String, OrderItem> orderItemStorage = new ConcurrentHashMap<>();
    
    public OrderItem createOrderItem(OrderItemRequest request) {
        String id = UUID.randomUUID().toString();
        Double totalPrice = request.getQuantity() * request.getPrice();
        
        OrderItem orderItem = OrderItem.builder()
                .id(id)
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .totalPrice(totalPrice)
                .build();
        
        orderItemStorage.put(id, orderItem);
        log.info("Created OrderItem with id: {}", id);
        
        return orderItem;
    }
    
    public Optional<OrderItem> getOrderItem(String id) {
        return Optional.ofNullable(orderItemStorage.get(id));
    }
    
    public List<OrderItem> getAllOrderItems() {
        return new ArrayList<>(orderItemStorage.values());
    }
    
    public OrderItem updateOrderItem(String id, OrderItemRequest request) {
        OrderItem existingItem = orderItemStorage.get(id);
        if (existingItem == null) {
            throw new RuntimeException("OrderItem not found with id: " + id);
        }
        
        Double totalPrice = request.getQuantity() * request.getPrice();
        
        OrderItem updatedItem = OrderItem.builder()
                .id(id)
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .totalPrice(totalPrice)
                .build();
        
        orderItemStorage.put(id, updatedItem);
        log.info("Updated OrderItem with id: {}", id);
        
        return updatedItem;
    }
    
    public void deleteOrderItem(String id) {
        orderItemStorage.remove(id);
        log.info("Deleted OrderItem with id: {}", id);
    }
    
    public Double calculateTotalPrice(String id) {
        OrderItem item = orderItemStorage.get(id);
        if (item == null) {
            throw new RuntimeException("OrderItem not found with id: " + id);
        }
        return item.getTotalPrice();
    }
}

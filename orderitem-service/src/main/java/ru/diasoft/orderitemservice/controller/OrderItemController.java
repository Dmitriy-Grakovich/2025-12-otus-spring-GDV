package ru.diasoft.orderitemservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.orderitemservice.domain.OrderItem;
import ru.diasoft.orderitemservice.dto.OrderItemRequest;
import ru.diasoft.orderitemservice.dto.OrderItemResponse;
import ru.diasoft.orderitemservice.service.OrderItemService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    
    private final OrderItemService orderItemService;
    
    @PostMapping
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody OrderItemRequest request) {
        log.info("Creating OrderItem for product: {}", request.getProductId());
        OrderItem orderItem = orderItemService.createOrderItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(orderItem));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItem(@PathVariable String id) {
        log.info("Getting OrderItem with id: {}", id);
        return orderItemService.getOrderItem(id)
                .map(item -> ResponseEntity.ok(toResponse(item)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        log.info("Getting all OrderItems");
        List<OrderItemResponse> items = orderItemService.getAllOrderItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> updateOrderItem(
            @PathVariable String id,
            @RequestBody OrderItemRequest request) {
        log.info("Updating OrderItem with id: {}", id);
        try {
            OrderItem orderItem = orderItemService.updateOrderItem(id, request);
            return ResponseEntity.ok(toResponse(orderItem));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable String id) {
        log.info("Deleting OrderItem with id: {}", id);
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/total-price")
    public ResponseEntity<Double> getTotalPrice(@PathVariable String id) {
        log.info("Calculating total price for OrderItem with id: {}", id);
        try {
            Double totalPrice = orderItemService.calculateTotalPrice(id);
            return ResponseEntity.ok(totalPrice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<OrderItemResponse>> searchByProductName(@RequestParam String name) {
        log.info("Searching OrderItems by product name: {}", name);
        List<OrderItemResponse> items = orderItemService.getAllOrderItems().stream()
                .filter(item -> item.getProductName().toLowerCase().contains(name.toLowerCase()))
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<OrderItemStats> getStats() {
        log.info("Getting OrderItem statistics");
        List<OrderItem> allItems = orderItemService.getAllOrderItems();
        
        int totalItems = allItems.size();
        int totalQuantity = allItems.stream().mapToInt(OrderItem::getQuantity).sum();
        double totalValue = allItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        double averagePrice = allItems.stream().mapToDouble(OrderItem::getPrice).average().orElse(0.0);
        
        OrderItemStats stats = new OrderItemStats(totalItems, totalQuantity, totalValue, averagePrice);
        return ResponseEntity.ok(stats);
    }
    
    private OrderItemResponse toResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
    
    // DTO для статистики
    public record OrderItemStats(
            int totalItems,
            int totalQuantity,
            double totalValue,
            double averagePrice
    ) {}
}

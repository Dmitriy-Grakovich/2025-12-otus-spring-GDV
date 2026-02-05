package ru.diasoft.tasklesson23.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.diasoft.tasklesson23.dto.OrderItemDto;
import ru.diasoft.tasklesson23.dto.OrderItemRequest;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OrderItemClient {
    
    private final RestTemplate restTemplate;
    private final String orderItemServiceUrl;
    
    public OrderItemClient(RestTemplate restTemplate,
                          @Value("${orderitem.service.url}") String orderItemServiceUrl) {
        this.restTemplate = restTemplate;
        this.orderItemServiceUrl = orderItemServiceUrl;
    }
    
    public OrderItemDto createOrderItem(OrderItemRequest request) {
        log.info("Creating OrderItem via REST: {}", request);
        String url = orderItemServiceUrl + "/api/order-items";
        return restTemplate.postForObject(url, request, OrderItemDto.class);
    }
    
    public OrderItemDto getOrderItem(String id) {
        log.info("Getting OrderItem by id: {}", id);
        String url = orderItemServiceUrl + "/api/order-items/" + id;
        return restTemplate.getForObject(url, OrderItemDto.class);
    }
    
    public List<OrderItemDto> getAllOrderItems() {
        log.info("Getting all OrderItems");
        String url = orderItemServiceUrl + "/api/order-items";
        OrderItemDto[] items = restTemplate.getForObject(url, OrderItemDto[].class);
        return items != null ? Arrays.asList(items) : List.of();
    }
    
    public OrderItemDto updateOrderItem(String id, OrderItemRequest request) {
        log.info("Updating OrderItem with id: {}", id);
        String url = orderItemServiceUrl + "/api/order-items/" + id;
        restTemplate.put(url, request);
        return getOrderItem(id);
    }
    
    public void deleteOrderItem(String id) {
        log.info("Deleting OrderItem with id: {}", id);
        String url = orderItemServiceUrl + "/api/order-items/" + id;
        restTemplate.delete(url);
    }
    
    public Double getTotalPrice(String id) {
        log.info("Getting total price for OrderItem with id: {}", id);
        String url = orderItemServiceUrl + "/api/order-items/" + id + "/total-price";
        return restTemplate.getForObject(url, Double.class);
    }
}

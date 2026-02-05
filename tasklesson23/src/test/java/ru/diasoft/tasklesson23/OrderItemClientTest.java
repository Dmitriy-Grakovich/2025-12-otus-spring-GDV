package ru.diasoft.tasklesson23;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import ru.diasoft.tasklesson23.client.OrderItemClient;
import ru.diasoft.tasklesson23.dto.OrderItemDto;
import ru.diasoft.tasklesson23.dto.OrderItemRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit тесты для OrderItemClient
 */
@SpringBootTest
class OrderItemClientTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private OrderItemClient orderItemClient;

    private OrderItemDto mockOrderItemDto;
    private OrderItemRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = new OrderItemRequest("prod-123", "Test Product", 2, 50.0);
        
        mockOrderItemDto = OrderItemDto.builder()
                .id(UUID.randomUUID().toString())
                .productId("prod-123")
                .productName("Test Product")
                .quantity(2)
                .price(50.0)
                .totalPrice(100.0)
                .build();
    }

    @Test
    void createOrderItemShouldCallRestTemplatePost() {
        when(restTemplate.postForObject(anyString(), any(), eq(OrderItemDto.class)))
                .thenReturn(mockOrderItemDto);

        OrderItemDto result = orderItemClient.createOrderItem(mockRequest);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo("prod-123");
        assertThat(result.getTotalPrice()).isEqualTo(100.0);
        
        verify(restTemplate).postForObject(
                contains("/api/order-items"),
                eq(mockRequest),
                eq(OrderItemDto.class)
        );
    }

    @Test
    void getOrderItemShouldCallRestTemplateGet() {
        String itemId = UUID.randomUUID().toString();
        when(restTemplate.getForObject(anyString(), eq(OrderItemDto.class)))
                .thenReturn(mockOrderItemDto);

        OrderItemDto result = orderItemClient.getOrderItem(itemId);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo("prod-123");
        
        verify(restTemplate).getForObject(
                contains("/api/order-items/" + itemId),
                eq(OrderItemDto.class)
        );
    }

    @Test
    void getAllOrderItemsShouldCallRestTemplateGet() {
        OrderItemDto[] mockArray = {mockOrderItemDto};
        when(restTemplate.getForObject(anyString(), eq(OrderItemDto[].class)))
                .thenReturn(mockArray);

        var result = orderItemClient.getAllOrderItems();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo("prod-123");
        
        verify(restTemplate).getForObject(
                contains("/api/order-items"),
                eq(OrderItemDto[].class)
        );
    }

    @Test
    void deleteOrderItemShouldCallRestTemplateDelete() {
        String itemId = UUID.randomUUID().toString();

        orderItemClient.deleteOrderItem(itemId);

        verify(restTemplate).delete(contains("/api/order-items/" + itemId));
    }

    @Test
    void getTotalPriceShouldCallRestTemplateGet() {
        String itemId = UUID.randomUUID().toString();
        Double expectedPrice = 100.0;
        
        when(restTemplate.getForObject(anyString(), eq(Double.class)))
                .thenReturn(expectedPrice);

        Double result = orderItemClient.getTotalPrice(itemId);

        assertThat(result).isEqualTo(expectedPrice);
        
        verify(restTemplate).getForObject(
                contains("/api/order-items/" + itemId + "/total-price"),
                eq(Double.class)
        );
    }
}

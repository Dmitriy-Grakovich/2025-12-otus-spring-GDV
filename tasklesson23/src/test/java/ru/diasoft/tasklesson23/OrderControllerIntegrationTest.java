package ru.diasoft.tasklesson23;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.diasoft.tasklesson23.client.OrderItemClient;
import ru.diasoft.tasklesson23.dto.OrderItemDto;
import ru.diasoft.tasklesson23.dto.OrderItemRequest;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для OrderController с мокированием OrderItem Service
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderItemClient orderItemClient;

    @Test
    void testOrderEndpointShouldCreateOrderWithMockedOrderItemService() throws Exception {
        // Мокируем ответ от OrderItem Service
        OrderItemDto mockedItem = OrderItemDto.builder()
                .id(UUID.randomUUID().toString())
                .productId("prod-123")
                .productName("Test Product")
                .quantity(2)
                .price(50.0)
                .totalPrice(100.0)
                .build();

        when(orderItemClient.createOrderItem(any(OrderItemRequest.class)))
                .thenReturn(mockedItem);

        // Вызываем тестовый endpoint
        mockMvc.perform(get("/api/orders/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.customerId").value("cust-001"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").exists())
                .andExpect(jsonPath("$.items[0].productId").value("prod-123"))
                .andExpect(jsonPath("$.items[0].totalPrice").value(100.0))
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.totalAmount").value(100.0));
    }

    @Test
    void submitOrderShouldCreateOrderWithMockedOrderItemService() throws Exception {
        // Мокируем ответ от OrderItem Service
        OrderItemDto mockedItem = OrderItemDto.builder()
                .id(UUID.randomUUID().toString())
                .productId("prod-456")
                .productName("Another Product")
                .quantity(3)
                .price(75.0)
                .totalPrice(225.0)
                .build();

        when(orderItemClient.createOrderItem(any(OrderItemRequest.class)))
                .thenReturn(mockedItem);

        // Создаем запрос на создание заказа
        String orderRequest = """
                {
                    "customerId": "cust-002",
                    "items": [
                        {
                            "productId": "prod-456",
                            "productName": "Another Product",
                            "quantity": 3,
                            "price": 75.0
                        }
                    ],
                    "totalAmount": 225.0,
                    "shippingAddress": "456 Oak St, City, Country"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.customerId").value("cust-002"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").exists())
                .andExpect(jsonPath("$.items[0].productId").value("prod-456"))
                .andExpect(jsonPath("$.items[0].totalPrice").value(225.0))
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.totalAmount").value(225.0))
                .andExpect(jsonPath("$.shippingAddress").value("456 Oak St, City, Country"));
    }

    @Test
    void submitOrderWithMultipleItemsShouldWork() throws Exception {
        // Мокируем ответы для нескольких элементов
        OrderItemDto mockedItem1 = OrderItemDto.builder()
                .id(UUID.randomUUID().toString())
                .productId("prod-001")
                .productName("Product 1")
                .quantity(1)
                .price(100.0)
                .totalPrice(100.0)
                .build();

        OrderItemDto mockedItem2 = OrderItemDto.builder()
                .id(UUID.randomUUID().toString())
                .productId("prod-002")
                .productName("Product 2")
                .quantity(2)
                .price(50.0)
                .totalPrice(100.0)
                .build();

        when(orderItemClient.createOrderItem(any(OrderItemRequest.class)))
                .thenReturn(mockedItem1)
                .thenReturn(mockedItem2);

        String orderRequest = """
                {
                    "customerId": "cust-003",
                    "items": [
                        {
                            "productId": "prod-001",
                            "productName": "Product 1",
                            "quantity": 1,
                            "price": 100.0
                        },
                        {
                            "productId": "prod-002",
                            "productName": "Product 2",
                            "quantity": 2,
                            "price": 50.0
                        }
                    ],
                    "totalAmount": 200.0,
                    "shippingAddress": "789 Pine St, City, Country"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.customerId").value("cust-003"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.totalAmount").value(200.0));
    }
}

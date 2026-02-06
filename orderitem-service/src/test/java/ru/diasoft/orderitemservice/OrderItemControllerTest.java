package ru.diasoft.orderitemservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.diasoft.orderitemservice.dto.OrderItemRequest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для OrderItemController
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createdItemId;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем тестовый элемент для использования в тестах
        OrderItemRequest request = new OrderItemRequest("prod-test", "Test Product", 1, 10.0);
        
        String response = mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        createdItemId = objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void createOrderItemShouldReturnCreatedStatus() throws Exception {
        OrderItemRequest request = new OrderItemRequest("prod-123", "Test Product", 2, 50.0);

        mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.productId").value("prod-123"))
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.price").value(50.0))
                .andExpect(jsonPath("$.totalPrice").value(100.0));
    }

    @Test
    void getOrderItemByIdShouldReturnItem() throws Exception {
        mockMvc.perform(get("/api/order-items/{id}", createdItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdItemId))
                .andExpect(jsonPath("$.productId").value("prod-test"))
                .andExpect(jsonPath("$.totalPrice").value(10.0));
    }

    @Test
    void getOrderItemByIdWithInvalidIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/order-items/{id}", "invalid-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOrderItemsShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void updateOrderItemShouldReturnUpdatedItem() throws Exception {
        OrderItemRequest updateRequest = new OrderItemRequest("prod-updated", "Updated Product", 5, 20.0);

        mockMvc.perform(put("/api/order-items/{id}", createdItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdItemId))
                .andExpect(jsonPath("$.productId").value("prod-updated"))
                .andExpect(jsonPath("$.productName").value("Updated Product"))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.price").value(20.0))
                .andExpect(jsonPath("$.totalPrice").value(100.0));
    }

    @Test
    void updateOrderItemWithInvalidIdShouldReturnNotFound() throws Exception {
        OrderItemRequest updateRequest = new OrderItemRequest("prod-updated", "Updated Product", 5, 20.0);

        mockMvc.perform(put("/api/order-items/{id}", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrderItemShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/order-items/{id}", createdItemId))
                .andExpect(status().isNoContent());

        // Проверяем, что элемент действительно удален
        mockMvc.perform(get("/api/order-items/{id}", createdItemId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTotalPriceShouldReturnPrice() throws Exception {
        mockMvc.perform(get("/api/order-items/{id}/total-price", createdItemId))
                .andExpect(status().isOk())
                .andExpect(content().string("10.0"));
    }

    @Test
    void getTotalPriceWithInvalidIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/order-items/{id}/total-price", "invalid-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrderItemWithZeroQuantityShouldCalculateCorrectly() throws Exception {
        OrderItemRequest request = new OrderItemRequest("prod-zero", "Zero Quantity", 0, 50.0);

        mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(0.0));
    }

    @Test
    void createOrderItemWithLargeQuantityShouldCalculateCorrectly() throws Exception {
        OrderItemRequest request = new OrderItemRequest("prod-large", "Large Quantity", 1000, 10.0);

        mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(10000.0));
    }
}

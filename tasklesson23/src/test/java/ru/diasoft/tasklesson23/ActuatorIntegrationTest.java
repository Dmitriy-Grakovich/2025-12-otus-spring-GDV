package ru.diasoft.tasklesson23;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для проверки работы Spring Boot Actuator
 */
@SpringBootTest
@AutoConfigureMockMvc
class ActuatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointShouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists());
    }

    @Test
    void orderProcessingHealthIndicatorShouldBePresent() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.orderProcessing").exists())
                .andExpect(jsonPath("$.components.orderProcessing.status").exists());
    }

    @Test
    void externalServicesHealthIndicatorShouldBePresent() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.externalServices").exists())
                .andExpect(jsonPath("$.components.externalServices.details.paymentGateway").exists())
                .andExpect(jsonPath("$.components.externalServices.details.inventorySystem").exists());
    }

    @Test
    void metricsEndpointShouldReturnMetricsList() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }

    @Test
    void customMetricsShouldBeAvailable() throws Exception {
        // Проверяем наличие кастомных метрик
        mockMvc.perform(get("/actuator/metrics/orders.submitted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("orders.submitted"));

        mockMvc.perform(get("/actuator/metrics/payment.success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("payment.success"));

        mockMvc.perform(get("/actuator/metrics/inventory.success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("inventory.success"));
    }

    
    @Test
    void infoEndpointShouldReturnAppInfo() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("tasklesson23"))
                .andExpect(jsonPath("$.app.description").exists());
    }

    @Test
    void integrationGraphEndpointShouldBeAvailable() throws Exception {
        mockMvc.perform(get("/actuator/integrationgraph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes").exists());
    }
}

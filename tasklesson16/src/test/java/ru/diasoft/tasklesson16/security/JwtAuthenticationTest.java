package ru.diasoft.tasklesson16.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.diasoft.tasklesson16.controller.dto.JwtRequest;
import ru.diasoft.tasklesson16.controller.dto.JwtResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Тесты JWT аутентификации")
class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Успешная аутентификация с валидными credentials")
    void whenValidCredentials_thenReturnJwtToken() throws Exception {
        JwtRequest request = new JwtRequest("user", "user");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JwtResponse response = objectMapper.readValue(responseBody, JwtResponse.class);
        
        assertThat(response.getToken()).isNotEmpty();
    }

    @Test
    @DisplayName("Аутентификация с неверным паролем")
    void whenInvalidPassword_thenReturnBadRequest() throws Exception {
        JwtRequest request = new JwtRequest("user", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Аутентификация с несуществующим пользователем")
    void whenUserNotFound_thenReturnBadRequest() throws Exception {
        JwtRequest request = new JwtRequest("nonexistent", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Доступ к защищенному ресурсу без токена")
    void whenNoToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isForbidden()); // JWT возвращает 403 Forbidden без токена
    }

    @Test
    @DisplayName("Доступ к защищенному ресурсу с валидным токеном")
    void whenValidToken_thenAccessGranted() throws Exception {
        // Сначала получаем токен
        JwtRequest request = new JwtRequest("user", "user");
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        JwtResponse response = objectMapper.readValue(responseBody, JwtResponse.class);
        String token = response.getToken();

        // Используем токен для доступа к защищенному ресурсу
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Доступ к защищенному ресурсу с невалидным токеном")
    void whenInvalidToken_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }
}

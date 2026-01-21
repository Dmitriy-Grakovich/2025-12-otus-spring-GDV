package ru.diasoft.tasklesson16.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Тесты безопасности приложения")
@org.springframework.test.context.ActiveProfiles("test")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Неаутентифицированный пользователь должен быть перенаправлен на страницу входа при доступе к /api/books")
    void whenUnauthenticatedAccessBooks_thenRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь должен быть перенаправлен на страницу входа при доступе к /api/authors")
    void whenUnauthenticatedAccessAuthors_thenRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь должен быть перенаправлен на страницу входа при доступе к /api/genres")
    void whenUnauthenticatedAccessGenres_thenRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/genres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Страница входа должна быть доступна для всех")
    void whenAccessLoginPage_thenOk() throws Exception {
        mockMvc.perform(get("/login.html"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Аутентифицированный пользователь с ролью USER должен иметь доступ к /api/books")
    void whenAuthenticatedUserAccessBooks_thenOk() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Аутентифицированный пользователь с ролью ADMIN должен иметь доступ к /api/books")
    void whenAuthenticatedAdminAccessBooks_thenOk() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Аутентифицированный пользователь с ролью USER должен иметь доступ к /api/authors")
    void whenAuthenticatedUserAccessAuthors_thenOk() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Аутентифицированный пользователь с ролью USER должен иметь доступ к /api/genres")
    void whenAuthenticatedUserAccessGenres_thenOk() throws Exception {
        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен иметь доступ к POST /api/books")
    void whenUnauthenticatedPostBook_thenRedirectToLogin() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен иметь доступ к PUT /api/books/{id}")
    void whenUnauthenticatedPutBook_thenRedirectToLogin() throws Exception {
        mockMvc.perform(put("/api/books/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен иметь доступ к DELETE /api/books/{id}")
    void whenUnauthenticatedDeleteBook_thenRedirectToLogin() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен иметь доступ к POST /api/authors")
    void whenUnauthenticatedPostAuthor_thenRedirectToLogin() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен иметь доступ к DELETE /api/authors/{id}")
    void whenUnauthenticatedDeleteAuthor_thenRedirectToLogin() throws Exception {
        mockMvc.perform(delete("/api/authors/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен иметь доступ к POST /api/genres")
    void whenUnauthenticatedPostGenre_thenRedirectToLogin() throws Exception {
        mockMvc.perform(post("/api/genres")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен иметь доступ к DELETE /api/genres/{id}")
    void whenUnauthenticatedDeleteGenre_thenRedirectToLogin() throws Exception {
        mockMvc.perform(delete("/api/genres/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }




    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Аутентифицированный пользователь должен иметь доступ к удалению книги")
    void whenAuthenticatedUserDeleteBook_thenAllowed() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

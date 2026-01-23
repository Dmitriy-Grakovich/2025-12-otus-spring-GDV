package ru.diasoft.tasklesson16.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.diasoft.tasklesson16.controller.dto.JwtRequest;
import ru.diasoft.tasklesson16.controller.dto.JwtResponse;
import ru.diasoft.tasklesson16.security.JwtUtil;

/**
 * Контроллер для аутентификации и получения JWT токена
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Аутентификация пользователя и выдача JWT токена
     * 
     * @param request запрос с username и password
     * @return JWT токен
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request) {
        try {
            log.info("Attempting to authenticate user: {}", request.getUsername());
            // Аутентифицируем пользователя
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            log.info("User authenticated successfully: {}", request.getUsername());
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body("Неверное имя пользователя или пароль");
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body("Ошибка аутентификации: " + e.getMessage());
        }

        // Загружаем данные пользователя
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // Генерируем токен
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));
    }
}

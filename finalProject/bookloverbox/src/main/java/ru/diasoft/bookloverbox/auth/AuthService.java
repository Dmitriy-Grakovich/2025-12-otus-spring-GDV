package ru.diasoft.bookloverbox.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.repository.UserRepository;
import ru.diasoft.bookloverbox.services.IAuthService;
import ru.diasoft.bookloverbox.security.JwtService;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            user.getEmail(),
            user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet())
        );
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Невалидный refresh token");
        }
        
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            user.getEmail(),
            user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet())
        );
    }
}

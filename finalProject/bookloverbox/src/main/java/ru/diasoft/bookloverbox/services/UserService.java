package ru.diasoft.bookloverbox.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.bookloverbox.auth.AuthResponse;
import ru.diasoft.bookloverbox.domain.Role;
import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.dto.RegisterRequest;
import ru.diasoft.bookloverbox.repository.RoleRepository;
import ru.diasoft.bookloverbox.repository.UserRepository;
import ru.diasoft.bookloverbox.security.JwtService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        // Проверка существования пользователя
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        
        // Создание пользователя
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setActive(true);
        
        // Назначение ролей (по умолчанию - READER)
        Set<Role> roles = new HashSet<>();
        Role readerRole = roleRepository.findByName(Role.READER)
            .orElseThrow(() -> new RuntimeException("Роль READER не найдена"));
        roles.add(readerRole);
        
        // Если пользователь хочет стать автором
        if (request.isWantsToBeAuthor()) {
            Role authorRole = roleRepository.findByName(Role.AUTHOR)
                .orElseThrow(() -> new RuntimeException("Роль AUTHOR не найдена"));
            roles.add(authorRole);
        }
        
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        
        // Генерация токенов
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            savedUser.getEmail(),
            savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }
    
    @Transactional
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Роль не найдена"));
        
        user.addRole(role);
        userRepository.save(user);
    }
    
    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Роль не найдена"));
        
        user.removeRole(role);
        userRepository.save(user);
    }
    
    public boolean userHasRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.hasRole(roleName);
    }
}

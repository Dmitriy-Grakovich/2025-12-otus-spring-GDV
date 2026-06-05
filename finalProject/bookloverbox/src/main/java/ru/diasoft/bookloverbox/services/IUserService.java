package ru.diasoft.bookloverbox.services;

import ru.diasoft.bookloverbox.auth.AuthResponse;
import ru.diasoft.bookloverbox.dto.RegisterRequest;

public interface IUserService {
    
    AuthResponse registerUser(RegisterRequest request);
    
    void addRoleToUser(Long userId, String roleName);
    
    void removeRoleFromUser(Long userId, String roleName);
    
    boolean userHasRole(Long userId, String roleName);
}

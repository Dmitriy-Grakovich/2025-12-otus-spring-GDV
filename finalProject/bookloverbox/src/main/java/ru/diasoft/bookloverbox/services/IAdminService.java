package ru.diasoft.bookloverbox.services;

import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.dto.UserStatsDto;

import java.util.List;
import java.util.Map;

public interface IAdminService {
    
    List<User> getAllUsers();
    
    User getUserById(Long userId);
    
    void setUserActive(Long userId, boolean active);
    
    UserStatsDto getSystemStats();
    
    Map<String, Long> getBooksCountByStatus();
    
    List<User> getTopAuthors(int limit);
    
    List<UserStatsDto> getUsersStatistics();
    
    Map<String, Object> getOverviewStatistics();
}

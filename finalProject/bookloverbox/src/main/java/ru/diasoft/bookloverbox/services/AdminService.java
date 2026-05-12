package ru.diasoft.bookloverbox.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.diasoft.bookloverbox.domain.BookStatus;
import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.dto.UserStatsDto;
import ru.diasoft.bookloverbox.repository.BookRepository;
import ru.diasoft.bookloverbox.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    
    public void setUserActive(Long userId, boolean active) {
        User user = getUserById(userId);
        user.setActive(active);
        userRepository.save(user);
    }
    
    public UserStatsDto getSystemStats() {
        UserStatsDto stats = new UserStatsDto();
        
        long totalUsers = userRepository.count();
        long totalBooks = bookRepository.count();
        
        stats.setTotalUsers(totalUsers);
        stats.setTotalBooks(totalBooks);
        stats.setActiveUsers(userRepository.findAll().stream().filter(User::isActive).count());
        stats.setPublishedBooks(bookRepository.findByStatus(BookStatus.PUBLISHED).size());
        stats.setBooksOnModeration(bookRepository.findByStatus(BookStatus.MODERATION).size());
        
        return stats;
    }
    
    public Map<String, Long> getBooksCountByStatus() {
        Map<String, Long> stats = new HashMap<>();
        for (BookStatus status : BookStatus.values()) {
            stats.put(status.name(), (long) bookRepository.findByStatus(status).size());
        }
        return stats;
    }
    
    public List<User> getTopAuthors(int limit) {
        return userRepository.findAllAuthors().stream()
            .sorted((u1, u2) -> Long.compare(
                bookRepository.countPublishedBooksByAuthor(u2),
                bookRepository.countPublishedBooksByAuthor(u1)
            ))
            .limit(limit)
            .collect(Collectors.toList());
    }
}
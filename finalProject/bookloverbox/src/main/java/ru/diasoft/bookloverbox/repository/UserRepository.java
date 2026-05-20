package ru.diasoft.bookloverbox.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.diasoft.bookloverbox.domain.User;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    // Найти всех авторов
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_AUTHOR'")
    Set<User> findAllAuthors();
    
    // Найти всех модераторов
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_MODERATOR'")
    Set<User> findAllModerators();
}

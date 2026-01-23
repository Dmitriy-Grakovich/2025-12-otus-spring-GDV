package ru.diasoft.tasklesson16.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.diasoft.tasklesson16.domain.Consumer;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Consumer, Long> {
    Optional<Consumer> findByUsername(String username);
}
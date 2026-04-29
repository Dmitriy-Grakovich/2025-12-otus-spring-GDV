package ru.diasoft.tasklesson16.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.diasoft.tasklesson16.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    @Override
    Optional<Genre> findById(Long id);
    
    @Override
    List<Genre> findAll();
    
    Optional<Genre> findByName(String name);
    
    List<Genre> findByNameContainingIgnoreCase(String name);
}

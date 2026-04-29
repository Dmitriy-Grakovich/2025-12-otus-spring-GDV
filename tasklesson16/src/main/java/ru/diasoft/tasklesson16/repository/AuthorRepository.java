package ru.diasoft.tasklesson16.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.diasoft.tasklesson16.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @Override
    Optional<Author> findById(Long id);
    
    @Override
    List<Author> findAll();
    
    List<Author> findByLastNameContainingIgnoreCase(String lastName);
    
    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);
}

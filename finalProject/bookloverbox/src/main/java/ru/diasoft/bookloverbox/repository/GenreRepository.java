package ru.diasoft.bookloverbox.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.diasoft.bookloverbox.domain.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    Optional<Genre> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT g FROM Genre g LEFT JOIN FETCH g.books")
    List<Genre> findAllWithBooks();
    
    @Query("SELECT g FROM Genre g WHERE SIZE(g.books) > 0")
    List<Genre> findGenresWithBooks();
}

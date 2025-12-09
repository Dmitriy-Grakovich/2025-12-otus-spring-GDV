package ru.diasoft.spring.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.service.impl.AuthorServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты для AuthorServiceImpl")
class AuthorServiceImplIntegrationTest {
    
    @Autowired
    private AuthorServiceImpl authorService;
    
    @Test
    @DisplayName("Должен найти всех авторов из тестовых данных")
    void shouldFindAllAuthorsFromTestData() {
        // When
        List<Author> authors = authorService.getAllAuthors();
        
        // Then
        assertThat(authors).hasSize(3);
        
        // Проверяем первого автора
        Author firstAuthor = authors.get(0);
        assertThat(firstAuthor.getId()).isEqualTo(100L);
        assertThat(firstAuthor.getFirstName()).isEqualTo("Test");
        assertThat(firstAuthor.getLastName()).isEqualTo("Author1");
        assertThat(firstAuthor.getAge()).isEqualTo(30);
    }
    
    @Test
    @DisplayName("Должен найти автора по ID")
    void shouldFindAuthorById() {
        // When
        Optional<Author> author = authorService.getAuthorById(101L);
        
        // Then
        assertThat(author).isPresent();
        assertThat(author.get().getFirstName()).isEqualTo("Test");
        assertThat(author.get().getLastName()).isEqualTo("Author2");
        assertThat(author.get().getAge()).isEqualTo(40);
    }
    
    @Test
    @DisplayName("Должен создать нового автора")
    void shouldCreateNewAuthor() {
        // Given
        String firstName = "Integration";
        String lastName = "Test";
        Integer age = 25;
        
        // When
        Author createdAuthor = authorService.createAuthor(firstName, lastName, age);
        
        // Then
        assertThat(createdAuthor.getId()).isNotNull();
        assertThat(createdAuthor.getFirstName()).isEqualTo(firstName);
        assertThat(createdAuthor.getLastName()).isEqualTo(lastName);
        assertThat(createdAuthor.getAge()).isEqualTo(age);
        
        // Проверяем, что автор сохранен в БД
        Optional<Author> foundAuthor = authorService.getAuthorById(createdAuthor.getId());
        assertThat(foundAuthor).isPresent();
        assertThat(foundAuthor.get().getFirstName()).isEqualTo(firstName);
    }
    
    @Test
    @DisplayName("Должен обновить автора")
    void shouldUpdateAuthor() {
        // Given - сначала создаем автора
        Author author = authorService.createAuthor("Original", "Author", 30);
        
        // When
        Author updatedAuthor = authorService.updateAuthor(
            author.getId(),
            "Updated",
            "Author",
            35
        );
        
        // Then
        assertThat(updatedAuthor.getId()).isEqualTo(author.getId());
        assertThat(updatedAuthor.getFirstName()).isEqualTo("Updated");
        assertThat(updatedAuthor.getLastName()).isEqualTo("Author");
        assertThat(updatedAuthor.getAge()).isEqualTo(35);
    }
    
    @Test
    @DisplayName("Должен удалить автора")
    void shouldDeleteAuthor() {
        // Given - создаем автора для удаления
        Author author = authorService.createAuthor("Delete", "Author", 30);
        Long authorId = author.getId();
        
        // When
        authorService.deleteAuthor(authorId);
        
        // Then
        assertThat(authorService.getAuthorById(authorId)).isEmpty();
    }
    
    @Test
    @DisplayName("Должен найти автора по имени и фамилии")
    void shouldFindAuthorByFullName() {
        // Given - сначала создаем автора
        authorService.createAuthor("Find", "Me", 30);
        
        // When
        Optional<Author> author = authorService.findAuthorByFullName("Find", "Me");
        
        // Then
        assertThat(author).isPresent();
        assertThat(author.get().getFirstName()).isEqualTo("Find");
        assertThat(author.get().getLastName()).isEqualTo("Me");
    }
}
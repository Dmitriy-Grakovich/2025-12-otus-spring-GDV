package ru.diasoft.spring.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.service.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Интеграционные тесты для AuthorServiceImpl")
class AuthorServiceImplIntegrationTest {

    @Autowired
    private AuthorService authorService;



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
    @DisplayName("Должен обновить существующего автора")
    void shouldUpdateExistingAuthor() {
        // Given - используем существующего автора из тестовых данных
        Long authorId = 100L;
        Optional<Author> existingAuthor = authorService.getAuthorById(authorId);
        assertThat(existingAuthor).isPresent();

        // When
        Author updatedAuthor = authorService.updateAuthor(
                authorId,
                "Updated",
                "Author",
                35
        );

        // Then
        assertThat(updatedAuthor.getId()).isEqualTo(authorId);
        assertThat(updatedAuthor.getFirstName()).isEqualTo("Updated");
        assertThat(updatedAuthor.getLastName()).isEqualTo("Author");
        assertThat(updatedAuthor.getAge()).isEqualTo(35);

        // Проверяем в БД
        Optional<Author> foundAuthor = authorService.getAuthorById(authorId);
        assertThat(foundAuthor).isPresent();
        assertThat(foundAuthor.get().getFirstName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("Должен обновить несуществующего автора (создать нового)")
    void shouldUpdateNonExistingAuthor() {
        // Given
        Long nonExistingId = 999L;

        // When
        Author updatedAuthor = authorService.updateAuthor(
                nonExistingId,
                "New",
                "Author",
                40
        );

        // Then - должен быть создан новый автор
        assertThat(updatedAuthor.getId()).isNotNull();
        assertThat(updatedAuthor.getFirstName()).isEqualTo("New");
        assertThat(updatedAuthor.getLastName()).isEqualTo("Author");
        assertThat(updatedAuthor.getAge()).isEqualTo(40);
    }

    @Test
    @DisplayName("Должен удалить автора")
    void shouldDeleteAuthor() {
        // Given - сначала создаем автора для удаления
        Author author = authorService.createAuthor("Delete", "Author", 30);
        Long authorId = author.getId();

        // Убедимся, что автор создан
        assertThat(authorService.getAuthorById(authorId)).isPresent();

        // When
        authorService.deleteAuthor(authorId);

        // Then - проверяем, что автор действительно удален
        // Можно попробовать получить автора заново
        Optional<Author> deletedAuthor = authorService.getAuthorById(authorId);
        assertThat(deletedAuthor).isEmpty();
    }

    @Test
    @DisplayName("Должен найти автора по имени и фамилии")
    void shouldFindAuthorByFullName() {
        // Given - используем существующего автора
        String firstName = "Test";
        String lastName = "Author1";

        // When
        Optional<Author> author = authorService.findAuthorByFullName(firstName, lastName);

        // Then
        assertThat(author).isPresent();
        assertThat(author.get().getFirstName()).isEqualTo(firstName);
        assertThat(author.get().getLastName()).isEqualTo(lastName);
        assertThat(author.get().getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске несуществующего автора по имени")
    void shouldReturnEmptyForNonExistingAuthorByFullName() {
        // When
        Optional<Author> author = authorService.findAuthorByFullName("Non", "Existing");

        // Then
        assertThat(author).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске автора по несуществующему ID")
    void shouldReturnEmptyForNonExistingAuthorById() {
        // When
        Optional<Author> author = authorService.getAuthorById(999L);

        // Then
        assertThat(author).isEmpty();
    }

    @Test
    @DisplayName("Должен корректно работать с автором без возраста (null)")
    void shouldHandleAuthorWithNullAge() {
        // Given
        String firstName = "NoAge";
        String lastName = "Author";

        // When
        Author author = authorService.createAuthor(firstName, lastName, null);

        // Then
        assertThat(author.getId()).isNotNull();
        assertThat(author.getFirstName()).isEqualTo(firstName);
        assertThat(author.getLastName()).isEqualTo(lastName);
        assertThat(author.getAge()).isNull();
    }
}
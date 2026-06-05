package ru.diasoft.bookloverbox.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.dto.BookDto;
import ru.diasoft.bookloverbox.dto.CreateBookRequest;
import ru.diasoft.bookloverbox.services.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookController Unit Tests")
class BookControllerTest {

    @Mock
    private BookServiceImpl bookServiceImpl;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private BookController bookController;

    private CreateBookRequest createBookRequest;
    private Book testBook;
    private BookDto testBookDto;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        createBookRequest = new CreateBookRequest();
        createBookRequest.setTitle("Мастер и Маргарита");
        createBookRequest.setAuthorName("Булгаков, Михаил Афанасьевич");
        createBookRequest.setDescription("Роман о дьяволе, посетившем Москву");
        createBookRequest.setGenreId(1L);
        createBookRequest.setContent("Текст книги...");
        createBookRequest.setPageCount(384);
        createBookRequest.setAgeRating("16+");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Мастер и Маргарита");
        testBook.setDescription("Роман о дьяволе, посетившем Москву");
        testBook.setPageCount(384);
        testBook.setAgeRating("16+");

        testBookDto = new BookDto();
        testBookDto.setId(1L);
        testBookDto.setTitle("Мастер и Маргарита");
        testBookDto.setDescription("Роман о дьяволе, посетившем Москву");
        testBookDto.setCoverUrl("https://example.com/cover.jpg");
        testBookDto.setPrice(new BigDecimal("299.00"));
        testBookDto.setStatus("PUBLISHED");
        testBookDto.setGenreId(1L);
        testBookDto.setGenreName("Фантастика");
        testBookDto.setAuthorName("Булгаков, Михаил Афанасьевич");
        testBookDto.setAverageRating(4.5);
        testBookDto.setReviewsCount(42);
        testBookDto.setViewsCount(1000L);
        testBookDto.setDownloadsCount(150L);
        testBookDto.setPublishedAt(LocalDateTime.now());
        testBookDto.setPageCount(384);
        testBookDto.setAgeRating("16+");

        lenient().when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Nested
    @DisplayName("createBook tests")
    class CreateBookTests {

        @Test
        @DisplayName("Должно вернуть созданную книгу при успешном создании")
        void createBook_Success() {
            when(bookServiceImpl.createBookFromRequest(any(CreateBookRequest.class), anyString()))
                    .thenReturn(testBook);
            when(bookServiceImpl.convertToDto(any(Book.class))).thenReturn(testBookDto);

            ResponseEntity<BookDto> response = bookController.createBook(createBookRequest, userDetails);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(testBookDto);
            verify(bookServiceImpl).createBookFromRequest(createBookRequest, "test@example.com");
            verify(bookServiceImpl).convertToDto(testBook);
        }

        @Test
        @DisplayName("Должно выбросить исключение при ошибке сервиса")
        void createBook_ServiceThrowsException() {
            when(bookServiceImpl.createBookFromRequest(any(CreateBookRequest.class), anyString()))
                    .thenThrow(new RuntimeException("Ошибка создания книги"));

            assertThatThrownBy(() -> bookController.createBook(createBookRequest, userDetails))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Ошибка создания книги");
        }
    }

    @Nested
    @DisplayName("submitToModeration tests")
    class SubmitToModerationTests {

        @Test
        @DisplayName("Должно отправить книгу на модерацию")
        void submitToModeration_Success() {
            when(bookServiceImpl.submitToModeration(1L, "test@example.com")).thenReturn(testBook);
            when(bookServiceImpl.convertToDto(any(Book.class))).thenReturn(testBookDto);

            ResponseEntity<BookDto> response = bookController.submitToModeration(1L, userDetails);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(testBookDto);
            verify(bookServiceImpl).submitToModeration(1L, "test@example.com");
        }

        @Test
        @DisplayName("Должно выбросить исключение при отсутствии книги")
        void submitToModeration_BookNotFound() {
            when(bookServiceImpl.submitToModeration(999L, "test@example.com"))
                    .thenThrow(new RuntimeException("Книга не найдена"));

            assertThatThrownBy(() -> bookController.submitToModeration(999L, userDetails))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Книга не найдена");
        }
    }

    @Nested
    @DisplayName("getPublishedBooks tests")
    class GetPublishedBooksTests {

        @Test
        @DisplayName("Должно вернуть страницу опубликованных книг")
        void getPublishedBooks_Success() {
            Page<BookDto> bookPage = new PageImpl<>(List.of(testBookDto));
            when(bookServiceImpl.getPublishedBooks(0, 10)).thenReturn(bookPage);

            ResponseEntity<Page<BookDto>> response = bookController.getPublishedBooks(0, 10);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(1);
            assertThat(response.getBody().getContent().get(0).getTitle()).isEqualTo("Мастер и Маргарита");
        }

        @Test
        @DisplayName("Должно вернуть пустую страницу при отсутствии книг")
        void getPublishedBooks_EmptyPage() {
            Page<BookDto> emptyPage = new PageImpl<>(List.of());
            when(bookServiceImpl.getPublishedBooks(0, 10)).thenReturn(emptyPage);

            ResponseEntity<Page<BookDto>> response = bookController.getPublishedBooks(0, 10);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).isEmpty();
        }

        @Test
        @DisplayName("Должно использовать значения по умолчанию для пагинации")
        void getPublishedBooks_DefaultPagination() {
            Page<BookDto> bookPage = new PageImpl<>(List.of(testBookDto));
            when(bookServiceImpl.getPublishedBooks(0, 10)).thenReturn(bookPage);

            ResponseEntity<Page<BookDto>> response = bookController.getPublishedBooks(0, 10);

            assertThat(response.getBody()).isNotNull();
            verify(bookServiceImpl).getPublishedBooks(0, 10);
        }
    }

    @Nested
    @DisplayName("getBookById tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Должно вернуть книгу по ID")
        void getBookById_Success() {
            when(bookServiceImpl.getBookById(1L)).thenReturn(testBookDto);

            ResponseEntity<BookDto> response = bookController.getBookById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(testBookDto);
            assertThat(response.getBody().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должно выбросить исключение при отсутствии книги")
        void getBookById_NotFound() {
            when(bookServiceImpl.getBookById(999L))
                    .thenThrow(new RuntimeException("Книга не найдена"));

            assertThatThrownBy(() -> bookController.getBookById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Книга не найдена");
        }
    }

    @Nested
    @DisplayName("getMyBooks tests")
    class GetMyBooksTests {

        @Test
        @DisplayName("Должно вернуть книги автора")
        void getMyBooks_Success() {
            Page<BookDto> bookPage = new PageImpl<>(List.of(testBookDto));
            when(bookServiceImpl.getBooksByAuthor("test@example.com", 0, 10)).thenReturn(bookPage);

            ResponseEntity<Page<BookDto>> response = bookController.getMyBooks(userDetails, 0, 10);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).hasSize(1);
            verify(bookServiceImpl).getBooksByAuthor("test@example.com", 0, 10);
        }

        @Test
        @DisplayName("Должно вернуть пустую страницу если у автора нет книг")
        void getMyBooks_EmptyPage() {
            Page<BookDto> emptyPage = new PageImpl<>(List.of());
            when(bookServiceImpl.getBooksByAuthor("test@example.com", 0, 10)).thenReturn(emptyPage);

            ResponseEntity<Page<BookDto>> response = bookController.getMyBooks(userDetails, 0, 10);

            assertThat(response.getBody().getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchBooks tests")
    class SearchBooksTests {

        @Test
        @DisplayName("Должно вернуть найденные книги")
        void searchBooks_Success() {
            Page<BookDto> bookPage = new PageImpl<>(List.of(testBookDto));
            when(bookServiceImpl.searchBooks("Мастер", 0, 10)).thenReturn(bookPage);

            ResponseEntity<Page<BookDto>> response = bookController.searchBooks("Мастер", 0, 10);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).hasSize(1);
            assertThat(response.getBody().getContent().get(0).getTitle()).contains("Мастер");
        }

        @Test
        @DisplayName("Должно вернуть пустую страницу при отсутствии результатов")
        void searchBooks_NoResults() {
            Page<BookDto> emptyPage = new PageImpl<>(List.of());
            when(bookServiceImpl.searchBooks("Неизвестная книга", 0, 10)).thenReturn(emptyPage);

            ResponseEntity<Page<BookDto>> response = bookController.searchBooks("Неизвестная книга", 0, 10);

            assertThat(response.getBody().getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateBook tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Должно обновить книгу")
        void updateBook_Success() {
            createBookRequest.setTitle("Обновленное название");
            when(bookServiceImpl.updateBook(eq(1L), any(CreateBookRequest.class), eq("test@example.com")))
                    .thenReturn(testBook);
            when(bookServiceImpl.convertToDto(any(Book.class))).thenReturn(testBookDto);

            ResponseEntity<BookDto> response = bookController.updateBook(1L, createBookRequest, userDetails);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(testBookDto);
            verify(bookServiceImpl).updateBook(1L, createBookRequest, "test@example.com");
        }

        @Test
        @DisplayName("Должно выбросить исключение при отсутствии книги")
        void updateBook_BookNotFound() {
            when(bookServiceImpl.updateBook(eq(999L), any(CreateBookRequest.class), anyString()))
                    .thenThrow(new RuntimeException("Книга не найдена"));

            assertThatThrownBy(() -> bookController.updateBook(999L, createBookRequest, userDetails))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Книга не найдена");
        }
    }

    @Nested
    @DisplayName("deleteBook tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Должно удалить книгу и вернуть OK")
        void deleteBook_Success() {
            doNothing().when(bookServiceImpl).deleteBook(1L, "test@example.com");

            ResponseEntity<Void> response = bookController.deleteBook(1L, userDetails);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(bookServiceImpl).deleteBook(1L, "test@example.com");
        }


    }
}

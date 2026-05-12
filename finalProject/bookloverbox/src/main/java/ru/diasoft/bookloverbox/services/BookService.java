package ru.diasoft.bookloverbox.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.domain.BookStatus;
import ru.diasoft.bookloverbox.domain.Genre;
import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.dto.BookDto;
import ru.diasoft.bookloverbox.repository.BookRepository;
import ru.diasoft.bookloverbox.repository.GenreRepository;
import ru.diasoft.bookloverbox.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    @Qualifier("errorChannel")
    private final MessageChannel bookPublishedChannel;
    
    @Transactional
    public Book createBook(BookDto dto, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
            .orElseThrow(() -> new RuntimeException("Автор не найден"));
        
        if (!author.isAuthor()) {
            throw new RuntimeException("Пользователь не является автором");
        }
        
        Genre genre = null;
        if (dto.getGenreId() != null) {
            genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        }
        
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setCoverUrl(dto.getCoverUrl());
        book.setPrice(dto.getPrice());
        book.setAuthor(author);
        book.setGenre(genre);
        book.setStatus(BookStatus.DRAFT);
        
        return bookRepository.save(book);
    }
    
    @Transactional
    public Book submitToModeration(Long bookId, String authorEmail) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        
        if (!book.getAuthor().getEmail().equals(authorEmail)) {
            throw new RuntimeException("Вы не являетесь автором этой книги");
        }
        
        if (book.getStatus() != BookStatus.DRAFT && book.getStatus() != BookStatus.REJECTED) {
            throw new RuntimeException("Книгу можно отправить на модерацию только из черновика или после отклонения");
        }
        
        book.setStatus(BookStatus.MODERATION);
        return bookRepository.save(book);
    }
    
    @Transactional
    public Book moderateBook(Long bookId, boolean approved, String moderatorComment) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        
        if (book.getStatus() != BookStatus.MODERATION) {
            throw new RuntimeException("Книга не на модерации");
        }
        
        if (approved) {
            book.setStatus(BookStatus.PUBLISHED);
            book.setPublishedAt(LocalDateTime.now());
            
            // Отправка асинхронного события о публикации
            bookPublishedChannel.send(MessageBuilder.withPayload(book).build());
        } else {
            book.setStatus(BookStatus.REJECTED);
        }
        
        return bookRepository.save(book);
    }
    
    public Page<BookDto> getPublishedBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByStatusOrderByPublishedAtDesc(BookStatus.PUBLISHED, pageable)
            .map(this::convertToDto);
    }
    
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        
        if (book.getStatus() == BookStatus.PUBLISHED) {
            book.incrementViews();
            bookRepository.save(book);
        }
        
        return convertToDto(book);
    }
    
    public Page<BookDto> getBooksByAuthor(String authorEmail, int page, int size) {
        User author = userRepository.findByEmail(authorEmail)
            .orElseThrow(() -> new RuntimeException("Автор не найден"));
        
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByAuthor(author, pageable)
            .map(this::convertToDto);
    }
    
    public Page<BookDto> searchBooks(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.searchByTitle(title, pageable)
            .map(this::convertToDto);
    }
    
    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setCoverUrl(book.getCoverUrl());
        dto.setPrice(book.getPrice());
        dto.setStatus(book.getStatus().toString());
        dto.setAuthorName(book.getAuthor().getFullName());
        dto.setGenreName(book.getGenre() != null ? book.getGenre().getName() : null);
        dto.setAverageRating(book.getAverageRating());
        dto.setReviewsCount(book.getReviewsCount());
        dto.setViewsCount(book.getViewsCount());
        dto.setDownloadsCount(book.getDownloadsCount());
        dto.setPublishedAt(book.getPublishedAt());
        return dto;
    }
}
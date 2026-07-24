package ru.diasoft.bookloverbox.services;

import org.springframework.data.domain.Page;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.dto.BookDto;
import ru.diasoft.bookloverbox.dto.CreateBookRequest;

public interface IBookService {
    
    Book createBookFromRequest(CreateBookRequest request, String authorEmail);
    
    Book createBook(BookDto dto, String authorEmail);
    
    Book submitToModeration(Long bookId, String authorEmail);
    
    Book moderateBook(Long bookId, boolean approved, String moderatorComment);
    
    Book moderateBookWithEdit(Long bookId, String newDescription, boolean approved, String rejectionReason);
    
    Page<BookDto> getPublishedBooks(int page, int size);
    
    BookDto getBookById(Long id);
    
    Page<BookDto> getBooksByAuthor(String authorEmail, int page, int size);
    
    Page<BookDto> searchBooks(String title, int page, int size);
    
    Page<BookDto> getPendingBooks(int page, int size);
    
    Book updateBook(Long id, CreateBookRequest request, String authorEmail);
    
    void deleteBook(Long id, String authorEmail);
    
    BookDto convertToDto(Book book);
}

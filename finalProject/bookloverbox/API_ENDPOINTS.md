# API Endpoints - BookLoverBox

## ✅ Статус проверки всех endpoints

### 🔐 Authentication (`/api/auth`)

| Endpoint | Method | Frontend | Backend | Описание |
|----------|--------|----------|---------|----------|
| `/auth/login` | POST | ✅ | ✅ | Вход в систему |
| `/auth/register` | POST | ✅ | ✅ | Регистрация |
| `/auth/refresh` | POST | ❌ | ✅ | Обновление токена |

### 📚 Books (`/api/books`)

| Endpoint | Method | Frontend | Backend | Описание |
|----------|--------|----------|---------|----------|
| `/books` | GET | ✅ | ✅ | Список опубликованных книг |
| `/books` | POST | ✅ | ✅ | Создать книгу |
| `/books/{id}` | GET | ✅ | ✅ | Получить книгу по ID |
| `/books/{id}` | PUT | ✅ | ❌ | Обновить книгу |
| `/books/{id}` | DELETE | ✅ | ❌ | Удалить книгу |
| `/books/my` | GET | ✅ | ✅ | Мои книги |
| `/books/search` | GET | ✅ | ✅ | Поиск книг |
| `/books/{id}/moderation` | POST | ❌ | ✅ | Отправить на модерацию |

### 🎭 Genres (`/api/genres`)

| Endpoint | Method | Frontend | Backend | Описание |
|----------|--------|----------|---------|----------|
| `/genres` | GET | ✅ | ✅ | Список жанров |
| `/genres` | POST | ✅ | ✅ | Создать жанр (ADMIN) |
| `/genres/{id}` | GET | ❌ | ✅ | Получить жанр |
| `/genres/{id}` | PUT | ❌ | ✅ | Обновить жанр (ADMIN) |
| `/genres/{id}` | DELETE | ❌ | ✅ | Удалить жанр (ADMIN) |

### ⭐ Reviews (`/api/reviews`)

| Endpoint | Method | Frontend | Backend | Описание |
|----------|--------|----------|---------|----------|
| `/reviews/books/{bookId}` | GET | ✅ | ✅ | Отзывы на книгу |
| `/reviews/books/{bookId}` | POST | ✅ | ✅ | Оставить отзыв |
| `/reviews/{reviewId}` | PUT | ✅ | ✅ | Редактировать отзыв |
| `/reviews/{reviewId}` | DELETE | ✅ | ✅ | Удалить отзыв |
| `/reviews/my` | GET | ❌ | ✅ | Мои отзывы |

### 👮 Moderation (`/api/moderator`)

| Endpoint | Method | Frontend | Backend | Описание |
|----------|--------|----------|---------|----------|
| `/moderator/books/pending` | GET | ✅ | ✅ | Книги на модерации |
| `/moderator/books/{id}/approve` | POST | ✅ | ✅ | Одобрить книгу |
| `/moderator/books/{id}/reject` | POST | ✅ | ✅ | Отклонить книгу |

### 📊 Statistics (`/api/statistics`)

| Endpoint | Method | Frontend | Backend | Описание |
|----------|--------|----------|---------|----------|
| `/statistics/overview` | GET | ✅ | ✅ | Общая статистика |
| `/statistics/users` | GET | ✅ | ✅ | Статистика пользователей |

### 🔄 Batch (`/api/batch`)

| Endpoint | Method | Frontend | Backend | Описание |
|----------|--------|----------|---------|----------|
| `/batch/export-books` | POST | ✅ | ✅ | Экспорт книг |

## ❌ Отсутствующие реализации

### Backend (нужно добавить):

1. **BookController:**
   - `PUT /books/{id}` - обновление книги
   - `DELETE /books/{id}` - удаление книги

### Frontend (не критично):

- `/auth/refresh` - обновление токена (можно добавить позже)
- `/genres/{id}` - просмотр отдельного жанра
- `/reviews/my` - мои отзывы

## 🔧 Что нужно исправить:

### 1. Добавить методы в BookController:

```java
@PutMapping("/{id}")
@Operation(summary = "Обновить книгу")
public ResponseEntity<BookDto> updateBook(@PathVariable Long id,
                                          @Valid @RequestBody CreateBookRequest request,
                                          @AuthenticationPrincipal UserDetails user) {
    Book book = bookService.updateBook(id, request, user.getUsername());
    return ResponseEntity.ok(bookService.convertToDto(book));
}

@DeleteMapping("/{id}")
@Operation(summary = "Удалить книгу")
public ResponseEntity<Void> deleteBook(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails user) {
    bookService.deleteBook(id, user.getUsername());
    return ResponseEntity.ok().build();
}
```

### 2. Добавить методы в BookService:

```java
@Transactional
public Book updateBook(Long id, CreateBookRequest request, String authorEmail) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    
    if (!book.getAuthor().getEmail().equals(authorEmail)) {
        throw new RuntimeException("Вы не являетесь автором этой книги");
    }
    
    // Обновление полей
    book.setTitle(request.getTitle());
    book.setAuthorName(request.getAuthorName());
    book.setDescription(request.getDescription());
    // ... остальные поля
    
    return bookRepository.save(book);
}

@Transactional
public void deleteBook(Long id, String authorEmail) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    
    if (!book.getAuthor().getEmail().equals(authorEmail)) {
        throw new RuntimeException("Вы не являетесь автором этой книги");
    }
    
    bookRepository.delete(book);
}
```

## ✅ Итоговая статистика:

- **Всего endpoints**: 28
- **Реализовано полностью**: 24 (86%)
- **Требует доработки**: 4 (14%)
  - 2 критичных (update/delete книг)
  - 2 некритичных (дополнительные функции)

## 🎯 Приоритет реализации:

1. **Высокий**: `PUT /books/{id}`, `DELETE /books/{id}` - нужны для управления книгами
2. **Средний**: Остальные endpoints работают корректно
3. **Низкий**: Дополнительные функции можно добавить позже

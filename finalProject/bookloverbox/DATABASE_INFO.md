# Работа с базой данных PostgreSQL

## ✅ ДА, ВСЕ ДАННЫЕ ХРАНЯТСЯ В POSTGRESQL!

### 🗄️ Конфигурация базы данных

**Файл:** `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookloverbox
    username: booklover
    password: secret
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: update  # Автоматическое создание/обновление таблиц
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### 📊 Структура таблиц в БД

#### **1. Таблица `books`**

```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    author_name VARCHAR(200),
    cover_url VARCHAR(500),
    price DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    
    -- Расширенные поля
    file_format VARCHAR(50),
    language VARCHAR(50),
    tags TEXT,
    publication_year INTEGER,
    publisher VARCHAR(200),
    page_count INTEGER,
    age_rating VARCHAR(10),
    copyright_holder VARCHAR(200),
    isbn VARCHAR(20),
    
    -- Связи
    author_id BIGINT NOT NULL REFERENCES users(id),
    genre_id BIGINT REFERENCES genres(id),
    
    -- Метрики
    views_count BIGINT DEFAULT 0,
    downloads_count BIGINT DEFAULT 0,
    average_rating DECIMAL(3,2),
    
    -- Временные метки
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    published_at TIMESTAMP
);
```

#### **2. Таблица `users`**

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(200),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP
);
```

#### **3. Таблица `roles`**

```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);
```

#### **4. Таблица `user_roles` (связь многие-ко-многим)**

```sql
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

#### **5. Таблица `genres`**

```sql
CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(500)
);
```

#### **6. Таблица `reviews`**

```sql
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 🔄 Операции с базой данных

#### **Создание книги (CREATE)**

**Код:** `BookService.createBookFromRequest()`

```java
@Transactional
@CacheEvict(value = CacheConfig.BOOKS_CACHE, allEntries = true)
public Book createBookFromRequest(CreateBookRequest request, String authorEmail) {
    // ... создание объекта Book
    return bookRepository.save(book); // ← СОХРАНЕНИЕ В БД
}
```

**SQL запрос:**
```sql
INSERT INTO books (
    title, author_name, description, genre_id, 
    author_id, status, file_format, language, 
    publication_year, page_count, created_at
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
```

#### **Обновление книги (UPDATE)**

**Код:** `BookService.updateBook()`

```java
@Transactional
@CacheEvict(value = CacheConfig.BOOKS_CACHE, allEntries = true)
public Book updateBook(Long id, CreateBookRequest request, String authorEmail) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    
    // ... обновление полей
    
    return bookRepository.save(book); // ← ОБНОВЛЕНИЕ В БД
}
```

**SQL запрос:**
```sql
UPDATE books SET 
    title = ?, 
    description = ?, 
    genre_id = ?,
    publication_year = ?,
    page_count = ?,
    updated_at = ?
WHERE id = ?;
```

#### **Удаление книги (DELETE)**

**Код:** `BookService.deleteBook()`

```java
@Transactional
@CacheEvict(value = CacheConfig.BOOKS_CACHE, allEntries = true)
public void deleteBook(Long id, String authorEmail) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    
    bookRepository.delete(book); // ← УДАЛЕНИЕ ИЗ БД
}
```

**SQL запрос:**
```sql
DELETE FROM books WHERE id = ?;
```

#### **Чтение книг (READ)**

**Код:** `BookRepository` методы

```java
// Все опубликованные книги
Page<Book> findByStatusOrderByPublishedAtDesc(BookStatus status, Pageable pageable);

// Книги автора
Page<Book> findByAuthor(User author, Pageable pageable);

// Поиск по названию
@Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
Page<Book> searchByTitle(@Param("title") String title, Pageable pageable);
```

**SQL запросы:**
```sql
-- Все опубликованные
SELECT * FROM books 
WHERE status = 'PUBLISHED' 
ORDER BY published_at DESC 
LIMIT 10 OFFSET 0;

-- Книги автора
SELECT * FROM books 
WHERE author_id = ? 
ORDER BY created_at DESC;

-- Поиск
SELECT * FROM books 
WHERE LOWER(title) LIKE LOWER('%война%') 
AND status = 'PUBLISHED';
```

### 🔍 Проверка данных в БД

#### **Способ 1: Через psql**

```bash
# Подключение к БД
psql -h localhost -U booklover -d bookloverbox

# Просмотр всех книг
SELECT id, title, author_name, status, created_at FROM books;

# Подсчет книг
SELECT COUNT(*) FROM books;

# Книги по статусам
SELECT status, COUNT(*) FROM books GROUP BY status;
```

#### **Способ 2: Через pgAdmin**

1. Подключитесь к серверу PostgreSQL
2. Откройте базу данных `bookloverbox`
3. Выполните запросы в Query Tool

#### **Способ 3: Через DBeaver**

1. Создайте подключение к PostgreSQL
2. Откройте базу `bookloverbox`
3. Просмотрите таблицы и данные

### 📈 Примеры данных в БД

#### **Книги:**
```
id | title           | author_name    | status      | created_at
---+-----------------+----------------+-------------+-------------------
1  | Война и мир     | Лев Толстой    | PUBLISHED   | 2026-05-12 14:00:00
2  | Преступление... | Достоевский    | MODERATION  | 2026-05-12 14:30:00
3  | Мастер и Марг.  | Булгаков       | DRAFT       | 2026-05-12 15:00:00
```

#### **Пользователи:**
```
id | email                      | full_name        | is_active
---+----------------------------+------------------+-----------
1  | admin@bookloverbox.ru      | Администратор    | true
2  | moderator@bookloverbox.ru  | Модератор        | true
3  | author@bookloverbox.ru     | Автор            | true
```

#### **Жанры:**
```
id | name                    | description
---+-------------------------+----------------------------------
1  | Классическая литература | Произведения классиков...
2  | Фантастика              | Научная фантастика...
3  | Детектив                | Детективы и криминальные романы
```

### 🔐 Транзакции и целостность данных

#### **@Transactional аннотации**

Все операции изменения данных выполняются в транзакциях:

```java
@Transactional  // ← Гарантирует ACID
public Book createBookFromRequest(...) {
    // Если произойдет ошибка - все откатится
    User author = userRepository.findByEmail(authorEmail)
        .orElseThrow(...);
    
    Genre genre = genreRepository.findById(request.getGenreId())
        .orElseThrow(...);
    
    Book book = new Book();
    // ... настройка
    
    return bookRepository.save(book);
}
```

#### **Каскадные операции**

```java
@Entity
public class Book {
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Review> reviews;
    // При удалении книги - удаляются все отзывы
}
```

### 📊 Статистика и аналитика

#### **Запросы для статистики:**

```java
// Количество книг по автору
long countByAuthor(User author);

// Количество опубликованных книг
@Query("SELECT COUNT(b) FROM Book b WHERE b.status = 'PUBLISHED'")
long countPublishedBooks();

// Топ книг по просмотрам
@Query("SELECT b FROM Book b ORDER BY b.viewsCount DESC")
Page<Book> findTopBooksByViews(Pageable pageable);
```

### 🔄 Миграции и версионирование

**Hibernate DDL Auto:** `update`
- Автоматически создает таблицы при первом запуске
- Обновляет схему при изменении Entity
- Не удаляет существующие данные

**Альтернатива:** Liquibase (отключен, но настроен)
```yaml
liquibase:
  enabled: false
  change-log: classpath:db/changelog/db.changelog-master.yml
```

### ✅ Подтверждение работы с БД

#### **1. Entity классы с JPA аннотациями:**
- ✅ `@Entity` - маркирует класс как сущность БД
- ✅ `@Table(name = "books")` - указывает имя таблицы
- ✅ `@Id` - первичный ключ
- ✅ `@GeneratedValue` - автогенерация ID
- ✅ `@Column` - настройка колонок
- ✅ `@ManyToOne`, `@OneToMany` - связи между таблицами

#### **2. Repository с JpaRepository:**
- ✅ Наследует `JpaRepository<Book, Long>`
- ✅ Автоматические CRUD операции
- ✅ Кастомные запросы с `@Query`
- ✅ Методы поиска по соглашению имен

#### **3. Транзакционность:**
- ✅ `@Transactional` на всех методах изменения
- ✅ Откат при ошибках
- ✅ ACID гарантии

#### **4. Логирование SQL:**
```yaml
jpa:
  show-sql: true  # Показывает SQL запросы в логах
  properties:
    hibernate:
      format_sql: true  # Форматирует SQL
```

### 🎯 Итог

**ВСЕ ДАННЫЕ ХРАНЯТСЯ В POSTGRESQL!**

- ✅ **Создание** - `INSERT INTO books ...`
- ✅ **Чтение** - `SELECT * FROM books ...`
- ✅ **Обновление** - `UPDATE books SET ...`
- ✅ **Удаление** - `DELETE FROM books ...`

**Никакие данные не хранятся в памяти!**
Все операции выполняются через JPA/Hibernate с PostgreSQL.

### 📝 Как проверить самостоятельно:

1. Подключитесь к БД:
   ```bash
   psql -h localhost -U booklover -d bookloverbox
   ```

2. Посмотрите книги:
   ```sql
   SELECT * FROM books;
   ```

3. Создайте книгу через UI

4. Снова посмотрите книги:
   ```sql
   SELECT * FROM books ORDER BY id DESC LIMIT 1;
   ```

5. Увидите новую запись в БД! 🎉

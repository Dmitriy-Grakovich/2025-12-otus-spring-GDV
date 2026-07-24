# Руководство по использованию Swagger API

## 🌐 Доступ к Swagger UI

После запуска приложения, Swagger UI доступен по адресу:

**http://localhost:8080/swagger-ui/index.html**

Или короткая ссылка:

**http://localhost:8080/swagger-ui.html**

## 📖 OpenAPI документация (JSON)

Полная спецификация API в формате OpenAPI 3.0:

**http://localhost:8080/v3/api-docs**

## 🔐 Аутентификация в Swagger

### Шаг 1: Получите JWT токен

1. Откройте Swagger UI
2. Найдите раздел **Authentication**
3. Разверните endpoint `POST /api/auth/login`
4. Нажмите **Try it out**
5. Введите тестовые данные:

```json
{
  "email": "admin@bookloverbox.ru",
  "password": "admin123"
}
```

6. Нажмите **Execute**
7. Скопируйте значение `accessToken` из ответа

### Шаг 2: Авторизуйтесь в Swagger

1. Нажмите кнопку **Authorize** (🔓) вверху страницы
2. В поле **Value** введите: `Bearer <ваш_токен>`
   
   Например:
   ```
   Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBib29rbG92ZXJib3gucnUi...
   ```

3. Нажмите **Authorize**
4. Нажмите **Close**

Теперь все защищенные endpoints будут автоматически использовать ваш токен!

## 👥 Тестовые учетные записи

### Администратор (полный доступ)
```json
{
  "email": "admin@bookloverbox.ru",
  "password": "admin123"
}
```

### Модератор (модерация книг)
```json
{
  "email": "moderator@bookloverbox.ru",
  "password": "moderator123"
}
```

### Автор (создание книг)
```json
{
  "email": "author@bookloverbox.ru",
  "password": "author123"
}
```

### Читатель (просмотр и отзывы)
```json
{
  "email": "reader@bookloverbox.ru",
  "password": "reader123"
}
```

## 📚 Основные группы endpoints

### 🔐 Authentication
- `POST /api/auth/register` - Регистрация нового пользователя
- `POST /api/auth/login` - Вход в систему
- `POST /api/auth/refresh` - Обновление токена

### 📖 Books
- `GET /api/books` - Список опубликованных книг
- `POST /api/books` - Создать книгу (AUTHOR)
- `GET /api/books/{id}` - Получить книгу по ID
- `PUT /api/books/{id}` - Обновить книгу (AUTHOR)
- `DELETE /api/books/{id}` - Удалить книгу (AUTHOR)
- `GET /api/books/my` - Мои книги (AUTHOR)
- `GET /api/books/search` - Поиск книг
- `POST /api/books/{id}/moderation` - Отправить на модерацию

### 🎭 Genres
- `GET /api/genres` - Список всех жанров
- `POST /api/genres` - Создать жанр (ADMIN)
- `GET /api/genres/{id}` - Получить жанр
- `PUT /api/genres/{id}` - Обновить жанр (ADMIN)
- `DELETE /api/genres/{id}` - Удалить жанр (ADMIN)

### ⭐ Reviews
- `GET /api/reviews/books/{bookId}` - Отзывы на книгу
- `POST /api/reviews/books/{bookId}` - Оставить отзыв
- `PUT /api/reviews/{reviewId}` - Редактировать отзыв
- `DELETE /api/reviews/{reviewId}` - Удалить отзыв
- `GET /api/reviews/my` - Мои отзывы

### 👮 Moderation
- `GET /api/moderator/books/pending` - Книги на модерации (MODERATOR)
- `POST /api/moderator/books/{id}/moderate` - Модерировать книгу (MODERATOR)
- `POST /api/moderator/books/{id}/approve` - Одобрить книгу (MODERATOR)
- `POST /api/moderator/books/{id}/reject` - Отклонить книгу (MODERATOR)

### 📊 Statistics
- `GET /api/statistics/overview` - Общая статистика (ADMIN/MODERATOR)
- `GET /api/statistics/users` - Статистика пользователей (ADMIN)

### 🔄 Batch
- `POST /api/batch/export-books` - Экспорт книг в CSV (ADMIN)

## 💡 Примеры использования

### Создание книги

1. Авторизуйтесь как автор
2. Найдите `POST /api/books`
3. Нажмите **Try it out**
4. Введите данные:

```json
{
  "title": "Война и мир",
  "authorName": "Лев Толстой",
  "description": "Роман-эпопея Льва Николаевича Толстого",
  "genreId": 1,
  "language": "Русский",
  "publicationYear": 1869,
  "pageCount": 1300,
  "ageRating": "12+"
}
```

5. Нажмите **Execute**

### Модерация книги

1. Авторизуйтесь как модератор
2. Получите список книг на модерации: `GET /api/moderator/books/pending`
3. Выберите книгу и используйте `POST /api/moderator/books/{id}/moderate`:

```json
{
  "description": "Отредактированное описание",
  "approved": true,
  "rejectionReason": null
}
```

### Добавление отзыва

1. Авторизуйтесь (любая роль)
2. Найдите `POST /api/reviews/books/{bookId}`
3. Введите данные:

```json
{
  "rating": 5,
  "comment": "Отличная книга! Рекомендую всем."
}
```

## 🎨 Особенности Swagger UI

### Фильтрация по тегам
Используйте теги для навигации:
- **Authentication** - Аутентификация
- **Books** - Книги
- **Genres** - Жанры
- **Reviews** - Отзывы
- **Moderation** - Модерация
- **Statistics** - Статистика

### Схемы данных
В разделе **Schemas** внизу страницы можно посмотреть структуру всех DTO:
- BookDto
- CreateBookRequest
- ReviewDto
- GenreDto
- UserStatsDto
- и другие

### Коды ответов
Для каждого endpoint указаны возможные коды ответов:
- **200** - Успешно
- **201** - Создано
- **400** - Неверный запрос
- **401** - Не авторизован
- **403** - Доступ запрещен
- **404** - Не найдено
- **500** - Ошибка сервера

## 🔧 Интеграция с другими инструментами

### Postman
1. Экспортируйте OpenAPI спецификацию: http://localhost:8080/v3/api-docs
2. Импортируйте в Postman: File → Import → Paste Raw Text
3. Настройте переменную окружения `bearerToken`

### Curl
```bash
# Получение токена
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@bookloverbox.ru","password":"admin123"}'

# Использование токена
curl -X GET http://localhost:8080/api/books/my \
  -H "Authorization: Bearer <ваш_токен>"
```

### HTTPie
```bash
# Получение токена
http POST http://localhost:8080/api/auth/login \
  email=admin@bookloverbox.ru password=admin123

# Использование токена
http GET http://localhost:8080/api/books/my \
  "Authorization: Bearer <ваш_токен>"
```

## 📝 Полезные ссылки

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml
- **Главная страница**: http://localhost:8080
- **Actuator Health**: http://localhost:8080/actuator/health

## ⚠️ Важные замечания

1. **JWT токены имеют срок действия** - если получаете 401, обновите токен
2. **Используйте правильную роль** - некоторые endpoints требуют определенных прав
3. **Валидация данных** - проверяйте обязательные поля в схемах
4. **Пагинация** - многие списки поддерживают параметры `page` и `size`

## 🎯 Быстрый старт

1. Запустите приложение
2. Откройте http://localhost:8080/swagger-ui.html
3. Авторизуйтесь через `POST /api/auth/login`
4. Нажмите **Authorize** и введите токен
5. Тестируйте любые endpoints!

**Готово! Теперь у вас есть полный доступ к API через Swagger UI.** 🚀

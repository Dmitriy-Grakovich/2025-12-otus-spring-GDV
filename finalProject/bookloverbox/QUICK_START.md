# BookLoverBox - Быстрый старт

## 🚀 Запуск приложения

### Требования
- Java 11
- PostgreSQL (запущен через Docker Compose)
- Maven 3.6.3

### Запуск

1. **Запустить базу данных:**
```bash
docker-compose up -d postgres
```

2. **Собрать проект:**
```bash
C:\mvn\apache-maven-3.6.3\bin\mvn.cmd clean package -DskipTests
```

3. **Запустить приложение:**
```bash
java -jar target\bookloverbox-0.0.1-SNAPSHOT.jar
```

## 🔐 Тестовые пользователи

При первом запуске автоматически создаются следующие пользователи:

### Администратор
- **Email**: `admin@bookloverbox.ru`
- **Пароль**: `admin123`
- **Роли**: ADMIN, MODERATOR, AUTHOR
- **Доступ**: Все разделы

### Модератор
- **Email**: `moderator@bookloverbox.ru`
- **Пароль**: `moderator123`
- **Роли**: MODERATOR, AUTHOR
- **Доступ**: Модерация, Мои книги

### Автор
- **Email**: `author@bookloverbox.ru`
- **Пароль**: `author123`
- **Роли**: AUTHOR
- **Доступ**: Мои книги

### Читатель
- **Email**: `reader@bookloverbox.ru`
- **Пароль**: `reader123`
- **Роли**: READER
- **Доступ**: Просмотр книг, отзывы

## 📱 Доступные разделы

### Для всех пользователей
- **Главная** (`/`) - Приветственная страница
- **Книги** - Каталог опубликованных книг
- **Вход/Регистрация** - Аутентификация

### Для авторизованных пользователей
- **Мои книги** - Управление своими книгами (для авторов)

### Для модераторов
- **Модерация** - Модерация книг на публикацию

### Для администраторов
- **Админ** - Панель администратора со статистикой

## 🌐 Endpoints

- **Frontend**: http://localhost:8080
- **API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator

## 🛠️ Технологии

### Backend
- Spring Boot 2.4.1
- Java 11
- PostgreSQL
- Spring Security + JWT
- Spring Batch
- Spring Integration
- Hibernate

### Frontend
- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

## 📝 API Примеры

### Вход
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@bookloverbox.ru","password":"admin123"}'
```

### Получение списка книг
```bash
curl http://localhost:8080/api/books
```

### Создание книги (требуется токен)
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Моя книга","description":"Описание","genreId":1}'
```

## 🐛 Отладка

Если возникают проблемы:

1. Проверьте, что PostgreSQL запущен:
```bash
docker-compose ps
```

2. Проверьте логи приложения в консоли

3. Проверьте логи PostgreSQL:
```bash
docker-compose logs postgres
```

4. Очистите базу данных и перезапустите:
```bash
docker-compose down -v
docker-compose up -d postgres
java -jar target\bookloverbox-0.0.1-SNAPSHOT.jar
```

## 📚 Структура проекта

```
bookloverbox/
├── src/main/
│   ├── java/ru/diasoft/bookloverbox/
│   │   ├── auth/          # Аутентификация
│   │   ├── batch/         # Spring Batch задачи
│   │   ├── config/        # Конфигурация
│   │   ├── controller/    # REST контроллеры
│   │   ├── domain/        # Entity классы
│   │   ├── dto/           # DTO объекты
│   │   ├── repository/    # JPA репозитории
│   │   ├── security/      # Security компоненты
│   │   └── services/      # Бизнес-логика
│   └── resources/
│       ├── static/        # Frontend файлы
│       │   ├── css/
│       │   ├── js/
│       │   └── index.html
│       └── application.yml
└── pom.xml
```

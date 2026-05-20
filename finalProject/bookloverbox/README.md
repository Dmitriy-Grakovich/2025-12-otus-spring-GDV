# BookLoverBox - Литературный портал

Полнофункциональная платформа для начинающих авторов и читателей, построенная на Spring Boot и React.

## 🚀 Технологии

### Backend
- **Spring Boot 3.5.6** - основной фреймворк
- **Spring Security + JWT** - аутентификация и авторизация
- **Spring Data JPA** - работа с базой данных
- **Spring Cache + Redis** - кэширование
- **Spring Batch** - пакетная обработка данных
- **Spring Integration** - интеграция и обработка событий
- **PostgreSQL** - основная база данных
- **Liquibase** - миграции БД
- **Swagger/OpenAPI** - документация API
- **Spring Boot Actuator + Prometheus** - мониторинг

### Frontend
- **React 18** - UI библиотека
- **TypeScript** - типизация
- **Vite** - сборщик
- **TailwindCSS** - стилизация
- **shadcn/ui** - компоненты UI
- **Lucide React** - иконки
- **Axios** - HTTP клиент
- **React Router** - роутинг

### DevOps
- **Docker** - контейнеризация
- **Docker Compose** - оркестрация
- **Nginx** - веб-сервер для фронтенда

## 📋 Функциональность

### Роли пользователей
- **Читатель** - просмотр и оценка книг, написание отзывов
- **Автор** - публикация книг, управление своими произведениями
- **Модератор** - модерация книг перед публикацией
- **Администратор** - полное управление платформой

### Основные возможности
- ✅ Регистрация и аутентификация пользователей
- ✅ Публикация книг авторами
- ✅ Модерация контента
- ✅ Система оценок и отзывов
- ✅ Поиск книг по названию
- ✅ Фильтрация по жанрам
- ✅ Статистика просмотров и скачиваний
- ✅ Панель администратора
- ✅ Экспорт данных в CSV
- ✅ Кэширование популярных запросов

## 🛠 Установка и запуск

### Предварительные требования
- Docker и Docker Compose
- (Опционально) Java 17+ и Maven для локальной разработки
- (Опционально) Node.js 20+ для разработки фронтенда

### Быстрый старт с Docker Compose

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd finalProject/bookloverbox
```

2. Запустите все сервисы:
```bash
docker-compose up -d
```

3. Дождитесь запуска всех контейнеров (это может занять несколько минут при первом запуске)

4. Откройте приложение:
- **Фронтенд**: http://localhost
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator

### Остановка приложения
```bash
docker-compose down
```

### Остановка с удалением данных
```bash
docker-compose down -v
```

## 🔧 Локальная разработка

### Backend

1. Запустите PostgreSQL и Redis:
```bash
docker-compose up postgres redis -d
```

2. Запустите Spring Boot приложение:
```bash
./mvnw spring-boot:run
```

### Frontend

1. Перейдите в директорию frontend:
```bash
cd frontend
```

2. Установите зависимости:
```bash
npm install
```

3. Запустите dev сервер:
```bash
npm run dev
```

4. Откройте http://localhost:3000

## 📊 API Endpoints

### Аутентификация
- `POST /api/auth/register` - регистрация
- `POST /api/auth/login` - вход

### Книги
- `GET /api/books` - список опубликованных книг
- `GET /api/books/{id}` - детали книги
- `POST /api/books` - создать книгу (автор)
- `GET /api/books/my` - мои книги (автор)
- `GET /api/books/search?title={title}` - поиск книг

### Отзывы
- `POST /api/reviews/books/{bookId}` - оставить отзыв
- `GET /api/reviews/books/{bookId}` - отзывы на книгу
- `PUT /api/reviews/{reviewId}` - редактировать отзыв
- `DELETE /api/reviews/{reviewId}` - удалить отзыв

### Модерация
- `POST /api/moderator/books/{id}/approve` - одобрить книгу
- `POST /api/moderator/books/{id}/reject` - отклонить книгу

### Администрирование
- `GET /api/statistics/overview` - общая статистика
- `GET /api/statistics/users` - статистика пользователей
- `POST /api/batch/export-books` - экспорт книг в CSV

### Жанры
- `GET /api/genres` - список жанров
- `POST /api/genres` - создать жанр (админ)

Полная документация API доступна в Swagger UI: http://localhost:8080/swagger-ui.html

## 🗄 Структура базы данных

### Основные таблицы
- `users` - пользователи
- `roles` - роли
- `user_roles` - связь пользователей и ролей
- `books` - книги
- `genres` - жанры
- `reviews` - отзывы

## 🔐 Безопасность

- JWT токены для аутентификации
- Хеширование паролей с BCrypt
- CORS настроен для работы с фронтендом
- Ролевая модель доступа
- Валидация входных данных

## 📈 Мониторинг

Приложение предоставляет метрики через Spring Boot Actuator:
- Health check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

## 🧪 Тестирование

Запуск тестов:
```bash
./mvnw test
```

## 📝 Переменные окружения

### Backend
- `DB_HOST` - хост PostgreSQL (по умолчанию: localhost)
- `DB_PORT` - порт PostgreSQL (по умолчанию: 5432)
- `DB_NAME` - имя базы данных (по умолчанию: bookloverbox)
- `DB_USER` - пользователь БД (по умолчанию: booklover)
- `DB_PASSWORD` - пароль БД (по умолчанию: secret)
- `REDIS_HOST` - хост Redis (по умолчанию: localhost)
- `REDIS_PORT` - порт Redis (по умолчанию: 6379)
- `JWT_SECRET` - секретный ключ для JWT
- `PORT` - порт приложения (по умолчанию: 8080)

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit изменения (`git commit -m 'Add some AmazingFeature'`)
4. Push в branch (`git push origin feature/AmazingFeature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект создан в образовательных целях.

## 👨‍💻 Автор

Гракович Дмитрий Владимирович

## 🎓 Проект

Финальный проект курса OTUS "Разработчик на Spring Framework"

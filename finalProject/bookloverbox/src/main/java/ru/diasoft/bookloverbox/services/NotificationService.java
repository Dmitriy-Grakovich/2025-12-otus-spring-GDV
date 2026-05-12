package ru.diasoft.bookloverbox.services;



import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.domain.Review;

@Slf4j
@Service
public class NotificationService {

    public static void notifySubscribers(Book book) {
        log.info("📚 Книга '{}' опубликована! Уведомляем подписчиков жанра...", book.getTitle());
        // Здесь реальная отправка email/уведомлений
    }

    public static void sendReviewNotification(Review review) {
        log.info("⭐ Новый отзыв на книгу '{}' от пользователя {}: оценка {}",
                review.getBook().getTitle(),
                review.getUser().getFullName(),
                review.getRating()
        );
    }

    public static void sendWelcomeEmail(String email) {
        log.info("✉️ Отправлено приветственное письмо на {}", email);
    }
}
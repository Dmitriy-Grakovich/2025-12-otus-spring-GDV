package ru.diasoft.bookloverbox.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.domain.Review;
import ru.diasoft.bookloverbox.services.NotificationService;

@Configuration
@EnableIntegration
public class IntegrationConfig {
    
    @Bean
    public MessageChannel bookPublishedChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public MessageChannel newReviewChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public MessageChannel userRegisteredChannel() {
        return new DirectChannel();
    }
    
    @ServiceActivator(inputChannel = "bookPublishedChannel")
    public void handleBookPublished(Book book) {
        NotificationService.notifySubscribers(book);
    }
    
    @ServiceActivator(inputChannel = "newReviewChannel")
    public void handleNewReview(Review review) {
        NotificationService.sendReviewNotification(review);
    }
    
    @ServiceActivator(inputChannel = "userRegisteredChannel")
    public void handleUserRegistration(String email) {
        NotificationService.sendWelcomeEmail(email);
    }
}
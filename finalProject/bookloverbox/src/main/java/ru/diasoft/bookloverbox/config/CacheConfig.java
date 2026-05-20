package ru.diasoft.bookloverbox.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String BOOKS_CACHE = "books";
    public static final String BOOK_BY_ID_CACHE = "bookById";
    public static final String GENRES_CACHE = "genres";
    public static final String REVIEWS_CACHE = "reviews";
    public static final String USER_STATS_CACHE = "userStats";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                BOOKS_CACHE,
                BOOK_BY_ID_CACHE,
                GENRES_CACHE,
                REVIEWS_CACHE,
                USER_STATS_CACHE
        );
    }
}

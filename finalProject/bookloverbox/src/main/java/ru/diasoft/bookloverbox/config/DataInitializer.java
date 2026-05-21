package ru.diasoft.bookloverbox.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.bookloverbox.domain.Genre;
import ru.diasoft.bookloverbox.domain.Role;
import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.repository.GenreRepository;
import ru.diasoft.bookloverbox.repository.RoleRepository;
import ru.diasoft.bookloverbox.repository.UserRepository;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GenreRepository genreRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Создание ролей
        initRoles();
        
        // Создание жанров
        initGenres();
        
        // Создание пользователей
        if (userRepository.count() == 0) {
            log.info("Инициализация тестовых пользователей...");
            
            Role adminRole = roleRepository.findByName(Role.ADMIN).orElseThrow();
            Role moderatorRole = roleRepository.findByName(Role.MODERATOR).orElseThrow();
            Role authorRole = roleRepository.findByName(Role.AUTHOR).orElseThrow();
            Role readerRole = roleRepository.findByName(Role.READER).orElseThrow();
            
            // Создание администратора
            User admin = new User();
            admin.setEmail("admin@bookloverbox.ru");
            admin.setFullName("Администратор");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);
            HashSet<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(moderatorRole);
            adminRoles.add(authorRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            log.info("Создан администратор: admin@bookloverbox.ru / admin123");
            
            // Создание модератора
            User moderator = new User();
            moderator.setEmail("moderator@bookloverbox.ru");
            moderator.setFullName("Модератор");
            moderator.setPassword(passwordEncoder.encode("moderator123"));
            moderator.setActive(true);
            HashSet<Role> moderatorRoles = new HashSet<>();
            moderatorRoles.add(moderatorRole);
            moderatorRoles.add(authorRole);
            moderator.setRoles(moderatorRoles);
            userRepository.save(moderator);
            log.info("Создан модератор: moderator@bookloverbox.ru / moderator123");
            
            // Создание автора
            User author = new User();
            author.setEmail("author@bookloverbox.ru");
            author.setFullName("Автор");
            author.setPassword(passwordEncoder.encode("author123"));
            author.setActive(true);
            HashSet<Role> authorRoles = new HashSet<>();
            authorRoles.add(authorRole);
            author.setRoles(authorRoles);
            userRepository.save(author);
            log.info("Создан автор: author@bookloverbox.ru / author123");
            
            // Создание читателя
            User reader = new User();
            reader.setEmail("reader@bookloverbox.ru");
            reader.setFullName("Читатель");
            reader.setPassword(passwordEncoder.encode("reader123"));
            reader.setActive(true);
            HashSet<Role> readerRoles = new HashSet<>();
            readerRoles.add(readerRole);
            reader.setRoles(readerRoles);
            userRepository.save(reader);
            log.info("Создан читатель: reader@bookloverbox.ru / reader123");
            
            log.info("Инициализация завершена!");
        } else {
            log.info("База данных уже содержит пользователей, инициализация пропущена");
        }
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            log.info("Создание ролей...");
            
            roleRepository.save(new Role(Role.ADMIN, "Администратор системы"));
            roleRepository.save(new Role(Role.MODERATOR, "Модератор контента"));
            roleRepository.save(new Role(Role.AUTHOR, "Автор книг"));
            roleRepository.save(new Role(Role.READER, "Читатель"));
            
            log.info("Роли созданы");
        }
    }
    
    private void initGenres() {
        if (genreRepository.count() == 0) {
            log.info("Создание жанров...");
            
            // Художественная литература
            genreRepository.save(new Genre("Классическая литература", "Произведения классиков мировой литературы"));
            genreRepository.save(new Genre("Современная проза", "Современная художественная литература"));
            genreRepository.save(new Genre("Фантастика", "Научная фантастика и космические приключения"));
            genreRepository.save(new Genre("Фэнтези", "Магия, драконы и волшебные миры"));
            genreRepository.save(new Genre("Детектив", "Детективы и криминальные романы"));
            genreRepository.save(new Genre("Триллер", "Напряженные и захватывающие истории"));
            genreRepository.save(new Genre("Роман", "Любовные и семейные романы"));
            genreRepository.save(new Genre("Приключения", "Приключенческие романы"));
            genreRepository.save(new Genre("Ужасы", "Мистика и ужасы"));
            genreRepository.save(new Genre("Историческая проза", "Исторические романы"));
            
            // Поэзия и драматургия
            genreRepository.save(new Genre("Поэзия", "Стихи и поэмы"));
            genreRepository.save(new Genre("Драматургия", "Пьесы и сценарии"));
            
            // Детская литература
            genreRepository.save(new Genre("Детская литература", "Книги для детей"));
            genreRepository.save(new Genre("Подростковая литература", "Книги для подростков"));
            genreRepository.save(new Genre("Сказки", "Народные и авторские сказки"));
            
            // Нехудожественная литература
            genreRepository.save(new Genre("Научная литература", "Научные и научно-популярные книги"));
            genreRepository.save(new Genre("Бизнес", "Книги о бизнесе и предпринимательстве"));
            genreRepository.save(new Genre("Психология", "Книги по психологии"));
            genreRepository.save(new Genre("Саморазвитие", "Книги по личностному росту"));
            genreRepository.save(new Genre("История", "Исторические исследования"));
            genreRepository.save(new Genre("Биография", "Биографии и мемуары"));
            genreRepository.save(new Genre("Философия", "Философские труды"));
            genreRepository.save(new Genre("Религия", "Религиозная литература"));
            genreRepository.save(new Genre("Кулинария", "Кулинарные книги и рецепты"));
            genreRepository.save(new Genre("Путешествия", "Путеводители и книги о путешествиях"));
            
            // Специальная литература
            genreRepository.save(new Genre("Программирование", "Книги по программированию и IT"));
            genreRepository.save(new Genre("Учебная литература", "Учебники и учебные пособия"));
            genreRepository.save(new Genre("Справочники", "Справочная литература"));
            
            log.info("Создано {} жанров", genreRepository.count());
        }
    }
}

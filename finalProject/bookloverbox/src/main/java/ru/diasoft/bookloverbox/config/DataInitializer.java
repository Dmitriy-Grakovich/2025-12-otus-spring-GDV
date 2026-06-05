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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // Создание ролей одним запросом
        initRoles();
        
        // Создание жанров одним запросом
        initGenres();
        
        // Создание пользователей
        if (userRepository.count() == 0) {
            log.info("Инициализация тестовых пользователей...");
            
            // Загружаем все роли одним запросом и создаем карту для быстрого доступа
            Map<String, Role> rolesMap = roleRepository.findAll().stream()
                    .collect(Collectors.toMap(Role::getName, role -> role));
            
            Role adminRole = rolesMap.get(Role.ADMIN);
            Role moderatorRole = rolesMap.get(Role.MODERATOR);
            Role authorRole = rolesMap.get(Role.AUTHOR);
            Role readerRole = rolesMap.get(Role.READER);
            
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
            
            // Создаем все роли одним запросом saveAll()
            roleRepository.saveAll(List.of(
                    new Role(Role.ADMIN, "Администратор системы"),
                    new Role(Role.MODERATOR, "Модератор контента"),
                    new Role(Role.AUTHOR, "Автор книг"),
                    new Role(Role.READER, "Читатель")
            ));
            
            log.info("Роли созданы");
        }
    }
    
    private void initGenres() {
        if (genreRepository.count() == 0) {
            log.info("Создание жанров...");
            
            // Создаем все жанры одним запросом saveAll()
            genreRepository.saveAll(List.of(
                    // Художественная литература
                    new Genre("Классическая литература", "Произведения классиков мировой литературы"),
                    new Genre("Современная проза", "Современная художественная литература"),
                    new Genre("Фантастика", "Научная фантастика и космические приключения"),
                    new Genre("Фэнтези", "Магия, драконы и волшебные миры"),
                    new Genre("Детектив", "Детективы и криминальные романы"),
                    new Genre("Триллер", "Напряженные и захватывающие истории"),
                    new Genre("Роман", "Любовные и семейные романы"),
                    new Genre("Приключения", "Приключенческие романы"),
                    new Genre("Ужасы", "Мистика и ужасы"),
                    new Genre("Историческая проза", "Исторические романы"),
                    // Поэзия и драматургия
                    new Genre("Поэзия", "Стихи и поэмы"),
                    new Genre("Драматургия", "Пьесы и сценарии"),
                    // Детская литература
                    new Genre("Детская литература", "Книги для детей"),
                    new Genre("Подростковая литература", "Книги для подростков"),
                    new Genre("Сказки", "Народные и авторские сказки"),
                    // Нехудожественная литература
                    new Genre("Научная литература", "Научные и научно-популярные книги"),
                    new Genre("Бизнес", "Книги о бизнесе и предпринимательстве"),
                    new Genre("Психология", "Книги по психологии"),
                    new Genre("Саморазвитие", "Книги по личностному росту"),
                    new Genre("История", "Исторические исследования"),
                    new Genre("Биография", "Биографии и мемуары"),
                    new Genre("Философия", "Философские труды"),
                    new Genre("Религия", "Религиозная литература"),
                    new Genre("Кулинария", "Кулинарные книги и рецепты"),
                    new Genre("Путешествия", "Путеводители и книги о путешествиях"),
                    // Специальная литература
                    new Genre("Программирование", "Книги по программированию и IT"),
                    new Genre("Учебная литература", "Учебники и учебные пособия"),
                    new Genre("Справочники", "Справочная литература")
            ));
            
            log.info("Создано {} жанров", genreRepository.count());
        }
    }
}

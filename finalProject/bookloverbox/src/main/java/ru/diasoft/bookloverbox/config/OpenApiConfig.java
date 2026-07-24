package ru.diasoft.bookloverbox.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("BookLoverBox API")
                        .version("1.0.0")
                        .description("REST API для литературного портала BookLoverBox\n\n" +
                                "## Описание\n" +
                                "Платформа для публикации, модерации и чтения книг с системой отзывов.\n\n" +
                                "## Аутентификация\n" +
                                "Используется JWT токен. Для доступа к защищенным endpoints:\n" +
                                "1. Получите токен через `/api/auth/login`\n" +
                                "2. Нажмите кнопку 'Authorize' вверху страницы\n" +
                                "3. Введите токен в формате: `Bearer <ваш_токен>`\n\n" +
                                "## Роли пользователей\n" +
                                "- **READER** - Читатель (просмотр книг, добавление отзывов)\n" +
                                "- **AUTHOR** - Автор (создание и управление своими книгами)\n" +
                                "- **MODERATOR** - Модератор (модерация книг)\n" +
                                "- **ADMIN** - Администратор (полный доступ)\n\n" +
                                "## Тестовые учетные записи\n" +
                                "- Администратор: `admin@bookloverbox.ru` / `admin123`\n" +
                                "- Модератор: `moderator@bookloverbox.ru` / `moderator123`\n" +
                                "- Автор: `author@bookloverbox.ru` / `author123`\n" +
                                "- Читатель: `reader@bookloverbox.ru` / `reader123`")
                        .contact(new Contact()
                                .name("OTUS Final Project")
                                .email("support@bookloverbox.ru")
                                .url("https://github.com/yourusername/bookloverbox"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Введите JWT токен в формате: Bearer <token>")));
    }
}

-- Вставка пользователей с зашифрованными паролями (BCrypt)
-- Пароль: admin123
INSERT INTO users (email, password, full_name, is_active)
VALUES ('admin@bookloverbox.ru', '$2a$10$YourRealAdminHashHere', 'Администратор', true);

-- Пароль: moderator123
INSERT INTO users (email, password, full_name, is_active)
VALUES ('moderator@bookloverbox.ru', '$2a$10$YourRealModeratorHashHere', 'Модератор', true);

-- Пароль: author123
INSERT INTO users (email, password, full_name, is_active)
VALUES ('author@bookloverbox.ru', '$2a$10$YourRealAuthorHashHere', 'Автор', true);

-- Пароль: reader123
INSERT INTO users (email, password, full_name, is_active)
VALUES ('reader@bookloverbox.ru', '$2a$10$YourRealReaderHashHere', 'Читатель', true);
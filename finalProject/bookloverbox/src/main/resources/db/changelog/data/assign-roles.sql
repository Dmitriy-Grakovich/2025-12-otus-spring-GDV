-- Назначение ролей администратору (все роли)
INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE email='admin@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_ADMIN')),
    ((SELECT id FROM users WHERE email='admin@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_MODERATOR')),
    ((SELECT id FROM users WHERE email='admin@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_AUTHOR')),
    ((SELECT id FROM users WHERE email='admin@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_READER'));

-- Назначение ролей модератору
INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE email='moderator@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_MODERATOR')),
    ((SELECT id FROM users WHERE email='moderator@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_AUTHOR')),
    ((SELECT id FROM users WHERE email='moderator@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_READER'));

-- Назначение ролей автору
INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE email='author@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_AUTHOR')),
    ((SELECT id FROM users WHERE email='author@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_READER'));

-- Назначение ролей читателю
INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE email='reader@bookloverbox.ru'), (SELECT id FROM roles WHERE name='ROLE_READER'));
CREATE TABLE IF NOT EXISTS category (
                                        id INT PRIMARY KEY,
                                        cname VARCHAR(100),
    cdesc VARCHAR(255)
    );

INSERT INTO category (id, cname, cdesc)
SELECT 1, 'Technology', 'Công Nghệ'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 1);

INSERT INTO category (id, cname, cdesc)
SELECT 2, 'Heal', 'Sức Khỏe & Làm Đẹp'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 2);

INSERT INTO category (id, cname, cdesc)
SELECT 3, 'Travel', 'Du Lịch'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 3);

INSERT INTO category (id, cname, cdesc)
SELECT 4, 'Food', 'Ẩm Thực'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 4);

INSERT INTO category (id, cname, cdesc)
SELECT 5, 'Finance', 'Tài Chính'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 5);

INSERT INTO category (id, cname, cdesc)
SELECT 6, 'Education', 'Giáo Dục'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 6);

INSERT INTO category (id, cname, cdesc)
SELECT 7, 'Entertainment', 'Giải Trí'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 7);

INSERT INTO category (id, cname, cdesc)
SELECT 8, 'Science', 'Khoa Học'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 8);



CREATE TABLE IF NOT EXISTS `users`
(
    `id`         varchar(255) NOT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    `active`     bit(1)       DEFAULT NULL,
    `avatar`     varchar(255) DEFAULT NULL,
    `dob`        date         DEFAULT NULL,
    `email`      varchar(255) DEFAULT NULL,
    `gender`     enum('FEMALE','MALE') DEFAULT 'FEMALE',
    `name`       varchar(255) DEFAULT NULL,
    `role`       varchar(255) DEFAULT 'USER',
    `password`   varchar(255) DEFAULT NULL,
    `is_locked`  bit(1)       DEFAULT 0,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO users (id, created_at, created_by, updated_at, updated_by, active, avatar, dob, email, gender, name, role, password, is_locked)
SELECT 'e692cd89-e09e-4651-afb8-8956d349ff6c',
       NOW(),
       'system',
       NOW(),
       'system',
       1,
       NULL,
       '2000-01-01',
       'user@gmail.com',
       'FEMALE',
       'Default User',
       'USER',
       '$2a$10$EJkL.sXN6Tg.NHzrmTk7DeWJf2lO/QYAJk7x7S41T4iHlgfimeUQu',
       0 WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO users (id, created_at, created_by, updated_at, updated_by, active, avatar, dob, email, gender, name, role, password, is_locked)
SELECT 'e692cd89-e09e-4651-afb8-8956d349ftdf',
       NOW(),
       'system',
       NOW(),
       'system',
       1,
       NULL,
       '2000-01-01',
       'thaidangfa@gmail.com',
       'FEMALE',
       'IAmTDF',
       'USER',
       '$2a$10$EJkL.sXN6Tg.NHzrmTk7DeWJf2lO/QYAJk7x7S41T4iHlgfimeUQu',
       0 WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO users (id, created_at, created_by, updated_at, updated_by, active, avatar, dob, email, gender, name, role, password, is_locked)
SELECT
    'a85f7cb2-9fe4-42d3-b821-4f6e95d7f761',
    NOW(),
    'system',
    NOW(),
    'system',
    1,
    NULL,
    '1990-01-01',
    'admin@gmail.com',
    'MALE',
    'System Administrator',
    'ADMIN',
    '$2a$10$EJkL.sXN6Tg.NHzrmTk7DeWJf2lO/QYAJk7x7S41T4iHlgfimeUQu',
    0
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE role = 'ADMIN');

-- SET SESSION sql_mode = (SELECT REPLACE(@@sql_mode, 'ONLY_FULL_GROUP_BY', ''));
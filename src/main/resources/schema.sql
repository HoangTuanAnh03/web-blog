CREATE TABLE IF NOT EXISTS category (
                                        id INT PRIMARY KEY,
                                        cname VARCHAR(100),
    cdesc VARCHAR(255)
    );

INSERT INTO category (id, cname, cdesc)
VALUES (1, 'Technology', 'Công Nghệ'),
       (2, 'Heal', 'Sức Khỏe & Làm Đẹp'),
       (3, 'Travel', 'Du Lịch'),
       (4, 'Food', 'Ẩm Thực'),
       (5, 'Finance', 'Tài Chính'),
       (6, 'Education', 'Giáo Dục'),
       (7, 'Entertainment', 'Giải Trí'),
       (8, 'Science', 'Khoa Học') ON DUPLICATE KEY
UPDATE id = id;

-- SET SESSION sql_mode = (SELECT REPLACE(@@sql_mode, 'ONLY_FULL_GROUP_BY', ''));
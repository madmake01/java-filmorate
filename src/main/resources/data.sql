MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES (6, 'Боевик');

MERGE INTO ratings (name) KEY (name) VALUES ('G');
MERGE INTO ratings (name) KEY (name) VALUES ('PG');
MERGE INTO ratings (name) KEY (name) VALUES ('PG-13');
MERGE INTO ratings (name) KEY (name) VALUES ('R');
MERGE INTO ratings (name) KEY (name) VALUES ('NC-17');

--MERGE INTO directors (id, name) KEY (id) VALUES (1, 'Алексадр');
--MERGE INTO directors (id, name) KEY (id) VALUES (2, 'Иван');
--MERGE INTO directors (id, name) KEY (id) VALUES (3, 'Виктор');
--MERGE INTO directors (id, name) KEY (id) VALUES (4, 'Дмитрий');
--MERGE INTO directors (id, name) KEY (id) VALUES (5, 'Антон');
--MERGE INTO directors (id, name) KEY (id) VALUES (6, 'Юрий');
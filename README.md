# java-filmorate

## Схема базы данных

![Схема БД](docs/images/database-schema.png)

### Таблицы

- **users** — пользователи: `email`, `login`, `name`, `birthday`
- **friendships** — дружба между пользователями с полем `status` (`pending`, `accepted`)
- **films** — фильмы: `name`, `description`, `release_date`, `duration`, `rating`
- **genres** — жанры фильмов
- **film_genres** — связь many-to-many между фильмами и жанрами
- **film_likes** — связь many-to-many между пользователями и фильмами (лайки)
- **ratings** — справочник возрастных рейтингов фильмов

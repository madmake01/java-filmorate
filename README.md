# java-filmorate

## Схема базы данных

![Схема БД](docs/images/database-schema.png)

### Таблицы

- **users** — пользователи: `email`, `login`, `name`, `birthday`
- **friendships** — дружба между пользователями
- **films** — фильмы: `name`, `description`, `release_date`, `duration`, `rating`
- **genres** — жанры фильмов
- **film_genres** — связь many-to-many между фильмами и жанрами
- **film_likes** — связь many-to-many между пользователями и фильмами (лайки)
- **ratings** — справочник возрастных рейтингов фильмов
- **directors** — справочник режиссёров
- **films_directors** — связь many-to-many между фильмами и режиссёрами
- **feed_events** — события активности пользователей: тип (`LIKE`, `REVIEW`, `FRIEND`), операция (`ADD`, `REMOVE`,
  `UPDATE`), связанные сущности (`user_id`, `entity_id`)
- **reviews** — отзывы пользователей на фильмы: `content`, `is_positive`, `user_id`, `film_id`, `useful`
- **review_likes** — лайки/дизлайки к отзывам: `review_id`, `user_id`, `is_like`

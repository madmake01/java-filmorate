CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar NOT NULL,
    login    varchar NOT NULL,
    name     varchar,
    birthday date
);


CREATE TABLE IF NOT EXISTS friendships
(
    requester_user_id bigint NOT NULL,
    addressee_user_id bigint NOT NULL,
    PRIMARY KEY (requester_user_id, addressee_user_id),
    FOREIGN KEY (requester_user_id) REFERENCES users (user_id),
    FOREIGN KEY (addressee_user_id) REFERENCES users (user_id),
    CONSTRAINT chk_no_self_friendship CHECK (requester_user_id <> addressee_user_id)
);

CREATE TABLE IF NOT EXISTS ratings
(
    rating_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name      varchar UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar NOT NULL,
    description  varchar(200),
    release_date date,
    duration     integer,
    rating_id    bigint,
    FOREIGN KEY (rating_id) REFERENCES ratings (rating_id)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    user_id bigint NOT NULL,
    film_id bigint NOT NULL,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  bigint NOT NULL,
    genre_id bigint NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

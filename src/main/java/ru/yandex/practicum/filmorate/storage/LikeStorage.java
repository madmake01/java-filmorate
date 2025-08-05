package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {
    void addLike(Like like);

    void removeLike(Like like);

    List<Film> getPopularFilms(int count,
                               Long genreId,
                               Integer year);
}

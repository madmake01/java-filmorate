package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {
    void addLike(Like like);

    void removeLike(Like like);

    List<Film> findTopFilmsByLikes(int amount);

    List<Film> getPopularFilmsWithCountAndGenreId(int count, long genreId);

    List<Film> getPopularFilmsWithCountAndYear(int count, int year);

    List<Film> getPopularFilmsWithGenreId(long genreId);

    List<Film> getPopularFilmsWithYear(int year);

    List<Film> getPopularFilmsWithGenreIdAndYear(long genreId, int year);
}

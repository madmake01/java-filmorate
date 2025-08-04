package ru.yandex.practicum.filmorate.storage.inmemoryimpl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    private final Set<Like> likes = new HashSet<>();

    @Override
    public void addLike(Like like) {
        likes.add(like);
    }

    @Override
    public void removeLike(Like like) {
        likes.remove(like);
    }

    @Override
    public List<Film> findTopFilmsByLikes(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> getPopularFilmsWithCountAndGenreId(int count, long genreId) {
        return List.of();
    }

    @Override
    public List<Film> getPopularFilmsWithCountAndYear(int count, int year) {
        return List.of();
    }

    @Override
    public List<Film> getPopularFilmsWithGenreId(long genreId) {
        return List.of();
    }

    @Override
    public List<Film> getPopularFilmsWithYear(int year) {
        return List.of();
    }

    @Override
    public List<Film> getPopularFilmsWithGenreIdAndYear(long genreId, int year) {
        return List.of();
    }

    public Map<Long, Long> getLikeCountsByFilmId() {
        return likes.stream()
                .collect(Collectors.groupingBy(Like::filmId, Collectors.counting()));
    }
}

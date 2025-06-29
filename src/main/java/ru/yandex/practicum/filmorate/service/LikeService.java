package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final LikeStorage likeStorage;

    public void addLike(Long userId, Long filmId) {
        likeStorage.addLike(new Like(userId, filmId));
    }

    public void removeLike(Long userId, Long filmId) {
        likeStorage.removeLike(new Like(userId, filmId));
    }

    public List<Film> findMostLikedFilms(int amount) {
        return likeStorage.findTopFilmsByLikes(amount);
    }
}

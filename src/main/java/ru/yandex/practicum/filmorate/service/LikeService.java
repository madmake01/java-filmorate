package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.event.LikeAddedEvent;
import ru.yandex.practicum.filmorate.event.LikeRemovedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final LikeStorage likeStorage;
    private final ApplicationEventPublisher publisher;
    private final UserService userService;
    private final FilmService filmService;

    @Transactional
    public void addLike(Long userId, Long filmId) {
        likeStorage.addLike(new Like(userId, filmId));
        publisher.publishEvent(new LikeAddedEvent(userId, filmId));
    }

    @Transactional
    public void removeLike(Long userId, Long filmId) {
        userService.getUser(userId);
        filmService.getFilm(filmId);
        likeStorage.removeLike(new Like(userId, filmId));
        publisher.publishEvent(new LikeRemovedEvent(userId, filmId));
    }

    public List<Film> getPopularFilmsWithCountAndGenreId(Integer count, Long genreId, Integer year) {
        return likeStorage.getPopularFilms(count, genreId, year);
    }

}

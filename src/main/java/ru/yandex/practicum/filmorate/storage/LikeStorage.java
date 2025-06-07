package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Map;

public interface LikeStorage {
    void addLike(Like like);

    void removeLike(Like like);

    Map<Long, Long> getLikeCountsByFilmId();
}

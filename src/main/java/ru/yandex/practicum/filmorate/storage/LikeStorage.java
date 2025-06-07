package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LikeStorage {
    private final Set<Like> likes = new HashSet<>();

    public void addLike(Like like) {
        likes.add(like);
    }

    public void removeLike(Like like) {
        likes.remove(like);
    }

    public Map<Long, Long> getLikeCountsByFilmId() {
        return likes.stream()
                .collect(Collectors.groupingBy(Like::filmId, Collectors.counting()));
    }
}

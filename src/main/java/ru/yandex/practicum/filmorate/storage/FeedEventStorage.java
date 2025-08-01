package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.feedevent.FeedEvent;

import java.util.List;

public interface FeedEventStorage {
    FeedEvent persist(FeedEvent feedEvent);

    List<FeedEvent> findByUserId(Long userId);
}

package ru.yandex.practicum.filmorate.event;

import ru.yandex.practicum.filmorate.model.feedevent.EventType;
import ru.yandex.practicum.filmorate.model.feedevent.Operation;

public record LikeRemovedEvent(Long userId, Long filmId)
        implements FeedEventSource {

    @Override
    public EventType eventType() {
        return EventType.LIKE;
    }

    @Override
    public Operation operation() {
        return Operation.REMOVE;
    }

    @Override
    public Long entityId() {
        return filmId;
    }
}
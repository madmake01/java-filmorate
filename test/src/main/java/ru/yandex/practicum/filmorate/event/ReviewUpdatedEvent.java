package ru.yandex.practicum.filmorate.event;

import ru.yandex.practicum.filmorate.model.feedevent.EventType;
import ru.yandex.practicum.filmorate.model.feedevent.Operation;

public record ReviewUpdatedEvent(Long userId, Long reviewId)
        implements FeedEventSource {

    @Override
    public EventType eventType() {
        return EventType.REVIEW;
    }

    @Override
    public Operation operation() {
        return Operation.UPDATE;
    }

    @Override
    public Long entityId() {
        return reviewId;
    }
}
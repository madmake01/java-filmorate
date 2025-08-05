package ru.yandex.practicum.filmorate.event;

import ru.yandex.practicum.filmorate.model.feedevent.EventType;
import ru.yandex.practicum.filmorate.model.feedevent.Operation;

public record FriendAddedEvent(Long userId, Long friendId)
        implements FeedEventSource {

    @Override
    public EventType eventType() {
        return EventType.FRIEND;
    }

    @Override
    public Operation operation() {
        return Operation.ADD;
    }

    @Override
    public Long entityId() {
        return friendId;
    }
}

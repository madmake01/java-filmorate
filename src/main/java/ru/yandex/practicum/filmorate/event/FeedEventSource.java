package ru.yandex.practicum.filmorate.event;

import ru.yandex.practicum.filmorate.model.feedevent.EventType;
import ru.yandex.practicum.filmorate.model.feedevent.Operation;

public interface FeedEventSource {
    EventType eventType();

    Operation operation();

    Long userId();

    Long entityId();
}

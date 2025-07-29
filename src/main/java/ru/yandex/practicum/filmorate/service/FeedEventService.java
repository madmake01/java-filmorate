package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.yandex.practicum.filmorate.event.FeedEventSource;
import ru.yandex.practicum.filmorate.model.feedevent.FeedEvent;
import ru.yandex.practicum.filmorate.storage.FeedEventStorage;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FeedEventService {
    private final FeedEventStorage feedEventStorage;
    private final UserService userService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(FeedEventSource feedEventSource) {
        FeedEvent feedEvent = FeedEvent.builder()
                .timestamp(Instant.now())
                .userId(feedEventSource.userId())
                .eventType(feedEventSource.eventType())
                .operation(feedEventSource.operation())
                .entityId(feedEventSource.entityId())
                .build();

        feedEventStorage.persist(feedEvent);
    }

    public List<FeedEvent> getUserFeedEvents(Long userId) {
        userService.getUser(userId);
        return feedEventStorage.findByUserId(userId);
    }
}

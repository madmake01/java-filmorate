package ru.yandex.practicum.filmorate.model.feedevent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FeedEvent {

    @JsonProperty("eventId")
    private Long id;
    private Instant timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
}

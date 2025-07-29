package ru.yandex.practicum.filmorate.model.feedevent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.InstantToEpochMillisSerializer;

import java.time.Instant;

@Data
@Builder
public class FeedEvent {

    @JsonProperty("eventId")
    private Long id;
    @JsonSerialize(using = InstantToEpochMillisSerializer.class)
    private Instant timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
}

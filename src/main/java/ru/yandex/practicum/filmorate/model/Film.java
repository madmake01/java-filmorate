package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;
import ru.yandex.practicum.filmorate.validation.NotBeforeDate;
import ru.yandex.practicum.filmorate.validation.PositiveDuration;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    public static final String EARLIEST_ALLOWED_RELEASE_DATE = "1895-12-28";
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotBeforeDate(EARLIEST_ALLOWED_RELEASE_DATE)
    private LocalDate releaseDate;

    @NotNull
    @PositiveDuration
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;
}

package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.NotBeforeDate;

import java.time.LocalDate;
import java.util.List;

@Data
public class Film {
    public static final String EARLIEST_ALLOWED_RELEASE_DATE = "1895-12-28";
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    @NotBeforeDate(EARLIEST_ALLOWED_RELEASE_DATE)
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Integer duration;
    @JsonProperty("mpa")
    @NotNull
    @Valid
    private Rating rating;
    @Valid
    private List<Genre> genres;

    private List<Director> directors;
}

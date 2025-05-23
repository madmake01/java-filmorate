package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    @NotBlank
    @Size(max = 200)
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

}

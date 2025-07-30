package ru.yandex.practicum.filmorate.storage.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FilmRowMapperWithDetails implements RowMapper<Film> {

    private final ObjectMapper objectMapper;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = FilmMappingUtil.mapFilm(resultSet);

        if (!resultSet.wasNull()) {
            film.setRating(FilmMappingUtil.mapRating(resultSet));
        }

        String genresJson = resultSet.getString("genres");
        if (genresJson != null && !genresJson.isBlank()) {
            try {
                List<Genre> genres = objectMapper.readValue(
                        genresJson,
                        new TypeReference<>() {
                        }
                );
                film.setGenres(genres);
            } catch (IOException e) {
                throw new SQLException("Ошибка при парсинге genres JSON", e);
            }
        }

        return film;
    }
}

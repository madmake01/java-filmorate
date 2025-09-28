package ru.yandex.practicum.filmorate.storage.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FilmRowMapper implements RowMapper<Film> {

    private final ObjectMapper objectMapper;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = FilmMappingUtil.mapFilm(resultSet);

        if (!resultSet.wasNull()) {
            film.setRating(FilmMappingUtil.mapRating(resultSet));
        }

        film.setGenres(parseJsonList(resultSet, "genres", objectMapper, new TypeReference<>() {
        }));
        film.setDirectors(parseJsonList(resultSet, "directors", objectMapper, new TypeReference<>() {
        }));

        return film;
    }

    private static <T> List<T> parseJsonList(ResultSet rs, String columnName, ObjectMapper objectMapper, TypeReference<List<T>> typeRef) throws SQLException {
        String json = rs.getString(columnName);
        if (json != null && !json.isBlank()) {
            try {
                return objectMapper.readValue(json, typeRef);
            } catch (IOException e) {
                throw new SQLException("Ошибка при парсинге JSON из колонки '" + columnName + "'", e);
            }
        }
        return List.of();
    }
}

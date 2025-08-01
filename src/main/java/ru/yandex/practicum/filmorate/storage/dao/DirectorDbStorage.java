package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbc;
    private final DirectorMapper directorMapper;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbc, DirectorMapper directorMapper) {
        this.jdbc = jdbc;
        this.directorMapper = directorMapper;
    }

    @Override
    public Director createDirector(Director director) {
        final String queryToAdd = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(queryToAdd, new String[]{"id"});
            stmt.setString(1, director.getName());

            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());

        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        final String queryToUpdate = "UPDATE directors SET name = ? WHERE id = ?";

        int numberOfEntries = jdbc.update(queryToUpdate,
                director.getName(),
                director.getId());
        if (numberOfEntries == 0) {
            throw new EntityNotFoundException(
                    String.format("Режиссёр с указанным id - %d не найден", director.getId()));
        }

        return director;
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        final String queryToGetDirectorById = "SELECT * FROM directors WHERE id = ?";

        return jdbc.query(queryToGetDirectorById, directorMapper, id).stream()
                .findFirst();

    }

    @Override
    public Collection<Director> getListDirectors() {
        final String queryToGetListDirectors = "SELECT * FROM directors";

        return jdbc.query(queryToGetListDirectors, directorMapper);
    }

    @Override
    public boolean removeDirector(long id) {
        final String queryToRemove = "DELETE FROM directors WHERE id = ?";

        return jdbc.update(queryToRemove, id) > 0;
    }
}

package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorMapper implements RowMapper<Director> {

    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getLong("id"), rs.getString("name"));
    }
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director updateDirector(Director director);

    Optional<Director> getDirectorById(long id);

    Collection<Director> getListDirectors();

    boolean removeDirector(long id);
}
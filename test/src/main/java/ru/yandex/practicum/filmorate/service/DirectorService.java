package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDbStorage;

import java.util.Collection;

@Service
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public Director createDirector(Director director) {
        return directorDbStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDbStorage.updateDirector(director);
    }

    public Director getDirectorById(long id) {
        return directorDbStorage.getDirectorById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Режиссёр с указанным id - %d не найден", id)));
    }

    public Collection<Director> getListDirectors() {
        return directorDbStorage.getListDirectors();
    }

    public boolean removeDirector(long id) {
        return directorDbStorage.removeDirector(id);

    }
}

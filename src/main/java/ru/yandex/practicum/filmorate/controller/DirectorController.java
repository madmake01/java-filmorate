package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorService.getDirectorById(id);
    }

    @GetMapping
    public Collection<Director> getListDirectors() {
        return directorService.getListDirectors();
    }

    @DeleteMapping("/{id}")
    public boolean removeDirector(@PathVariable long id) {
        return directorService.removeDirector(id);
    }
}

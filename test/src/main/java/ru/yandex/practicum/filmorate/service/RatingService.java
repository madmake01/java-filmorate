package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class RatingService {
    private final RatingDbStorage ratingDbStorage;

    public Rating getRating(Long id) {
        return ratingDbStorage.getById(id).orElseThrow(() -> new EntityNotFoundException("Rating with id '%d' not found".formatted(id)));
    }

    public Collection<Rating> findAll() {
        return ratingDbStorage.findAll();
    }
}

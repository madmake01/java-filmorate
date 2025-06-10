package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> find(Long id);

    Collection<User> findAll();

    User persist(User user);

    Optional<User> update(User user);
}

package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;


    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User persist(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> update(User user) {
        return Optional.ofNullable(users.computeIfPresent(user.getId(), (k, v) -> user));
    }

    private long generateId() {
        id++;
        return id;
    }
}

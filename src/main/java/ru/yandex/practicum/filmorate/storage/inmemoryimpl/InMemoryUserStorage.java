package ru.yandex.practicum.filmorate.storage.inmemoryimpl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public Optional<User> find(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User persist(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        return Optional.ofNullable(users.computeIfPresent(user.getId(), (k, v) -> user));
    }

    private long generateId() {
        id++;
        return id;
    }

    @Override
    public void remove(Long id) {
        find(id);
        users.remove(id);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public User getUser(Long id) {
        return userStorage.find(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id '%d' not found".formatted(id)));
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User save(User user) {
        if (user.getName() == null) {
            log.debug("User {} name is null, using login instead ", user);
            user.setName(user.getLogin());
        }
        return userStorage.persist(user);
    }

    public User update(User user) {
        return userStorage.update(user)
                .orElseThrow(() -> new EntityNotFoundException("User with id '%d' not found".formatted(user.getId())));
    }

}

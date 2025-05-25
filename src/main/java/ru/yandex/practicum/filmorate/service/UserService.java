package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        if (user.getName() == null) {
            log.debug("User {} name is null, using login instead ", user);
            user.setName(user.getLogin());
        }
        return userRepository.persist(user);
    }

    public Optional<User> update(User user) {
        return userRepository.update(user);
    }
}

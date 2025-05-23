package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userRepository.persist(user);
    }

    public Optional<User> update(User user) {
        return userRepository.update(user);
    }
}

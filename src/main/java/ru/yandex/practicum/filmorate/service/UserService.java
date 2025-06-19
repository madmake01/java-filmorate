package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

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

    public void addFriend(Long ownerId, Long friendId) {
        validateUserExist(ownerId, friendId);
        friendshipStorage.addFriendShip(new Friendship(ownerId, friendId));
    }

    public void removeFriend(Long ownerId, Long friendId) {
        validateUserExist(ownerId, friendId);
        friendshipStorage.removeFriendShip(new Friendship(ownerId, friendId));
    }

    public Set<User> getFriends(Long ownerId) {
        validateUserExist(ownerId);
        return friendshipStorage.getFriendIds(ownerId)
                .stream()
                .map(this::getUser)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        Set<Long> firstUserFriends = friendshipStorage.getFriendIds(firstUserId);
        Set<Long> secondUserFriends = friendshipStorage.getFriendIds(secondUserId);

        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .map(this::getUser)
                .collect(Collectors.toSet());
    }

    private void validateUserExist(Long... id) {
        for (Long userId : id) {
            getUser(userId);
        }
    }
}

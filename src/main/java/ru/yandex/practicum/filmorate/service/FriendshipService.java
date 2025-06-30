package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FriendshipService {
    private final FriendshipStorage friendshipStorage;
    private final UserService userService;

    @Transactional
    public void addFriend(Long ownerId, Long friendId) {
        userService.getUser(ownerId);
        userService.getUser(friendId);
        friendshipStorage.persist(new Friendship(ownerId, friendId));
    }

    @Transactional
    public void removeFriend(Long ownerId, Long friendId) {
        userService.getUser(ownerId);
        userService.getUser(friendId);
        friendshipStorage.delete(new Friendship(ownerId, friendId));
    }

    @Transactional
    public List<User> getFriends(Long ownerId) {
        userService.getUser(ownerId);
        return friendshipStorage.findFriends(ownerId);
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        userService.getUser(firstUserId);
        userService.getUser(secondUserId);
        return friendshipStorage.findCommonFriends(firstUserId, secondUserId);
    }
}


package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendshipStorage {
    void persist(Friendship friendship);

    void delete(Friendship friendship);

    List<User> findCommonFriends(Long userId1, Long userId2);

    List<User> findFriends(Long userId);

    Set<Long> getFriendIds(Long ownerId);
}

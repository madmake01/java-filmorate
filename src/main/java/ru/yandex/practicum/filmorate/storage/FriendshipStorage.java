package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Set;

public interface FriendshipStorage {
    void addFriendShip(Friendship friendship);

    void removeFriendShip(Friendship friendship);

    Set<Long> getFriendIds(Long ownerId);
}

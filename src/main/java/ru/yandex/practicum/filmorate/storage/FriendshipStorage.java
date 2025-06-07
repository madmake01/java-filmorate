package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FriendshipStorage {

    private final Set<Friendship> friendships = new HashSet<>();

    public void addFriendShip(Friendship friendship) {
        friendships.add(friendship);
    }

    public void removeFriendShip(Friendship friendship) {
        friendships.remove(friendship);
    }

    public Set<Long> getFriendIds(Long ownerId) {
        return friendships.stream()
                .filter(f -> Objects.equals(f.userIdOne(), ownerId) || Objects.equals(f.userIdTwo(), ownerId))
                .map(f -> Objects.equals(f.userIdOne(), ownerId) ? f.userIdTwo() : f.userIdOne())
                .collect(Collectors.toSet());
    }

}

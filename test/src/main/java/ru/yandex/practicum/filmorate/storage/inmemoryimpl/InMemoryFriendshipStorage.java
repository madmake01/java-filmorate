package ru.yandex.practicum.filmorate.storage.inmemoryimpl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {

    private final Set<Friendship> friendships = new HashSet<>();

    @Override
    public void persist(Friendship friendship) {
        friendships.add(friendship);
    }

    @Override
    public void delete(Friendship friendship) {
        friendships.remove(friendship);
    }

    @Override
    public List<User> findCommonFriends(Long userId1, Long userId2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findFriends(Long userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Long> getFriendIds(Long ownerId) {
        return friendships.stream()
                .filter(f -> Objects.equals(f.userIdOne(), ownerId) || Objects.equals(f.userIdTwo(), ownerId))
                .map(f -> Objects.equals(f.userIdOne(), ownerId) ? f.userIdTwo() : f.userIdOne())
                .collect(Collectors.toSet());
    }
}

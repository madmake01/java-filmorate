package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.sql.FriendshipSql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@RequiredArgsConstructor
@Repository
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper;

    @Override
    public void persist(Friendship friendship) {
        jdbcTemplate.update(FriendshipSql.INSERT,
                friendship.userIdOne(), friendship.userIdTwo());
    }

    @Override
    public void delete(Friendship friendship) {
        jdbcTemplate.update(FriendshipSql.DELETE,
                friendship.userIdOne(), friendship.userIdTwo());
    }

    @Override
    public Set<Long> getFriendIds(Long ownerId) {
        List<Long> ids = jdbcTemplate.queryForList(FriendshipSql.SELECT_FRIEND_IDS, Long.class, ownerId);
        return new HashSet<>(ids);
    }

    @Override
    public List<User> findCommonFriends(Long userId1, Long userId2) {
        return jdbcTemplate.query(FriendshipSql.SELECT_COMMON_FRIENDS, userRowMapper, userId1, userId2);
    }

    @Override
    public List<User> findFriends(Long userId) {
        return jdbcTemplate.query(FriendshipSql.SELECT_FRIENDS, userRowMapper, userId);
    }
}

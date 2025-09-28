package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class FriendshipDbStorageTest {

    @Autowired
    private FriendshipDbStorage friendshipDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (1, 'a@mail.com', 'a', 'User A', '1990-01-01')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (2, 'b@mail.com', 'b', 'User B', '1991-01-01')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (3, 'c@mail.com', 'c', 'User C', '1992-01-01')");
    }

    @Test
    void persist_shouldAddFriendship() {
        Friendship friendship = new Friendship(1L, 2L);
        friendshipDbStorage.persist(friendship);

        Set<Long> friendsOf1 = friendshipDbStorage.getFriendIds(1L);
        assertThat(friendsOf1).containsExactly(2L);
    }

    @Test
    void delete_shouldRemoveFriendship() {
        Friendship friendship = new Friendship(1L, 2L);
        friendshipDbStorage.persist(friendship);
        friendshipDbStorage.delete(friendship);

        Set<Long> friendsOf1 = friendshipDbStorage.getFriendIds(1L);
        assertThat(friendsOf1).isEmpty();
    }

    @Test
    void getFriendIds_shouldReturnOnlyOutgoingFriendships() {
        friendshipDbStorage.persist(new Friendship(1L, 2L));
        friendshipDbStorage.persist(new Friendship(1L, 3L));
        friendshipDbStorage.persist(new Friendship(2L, 3L));

        Set<Long> friends = friendshipDbStorage.getFriendIds(1L);
        assertThat(friends).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void findFriends_shouldReturnUserObjects() {
        friendshipDbStorage.persist(new Friendship(1L, 2L));
        friendshipDbStorage.persist(new Friendship(1L, 3L));

        List<User> friends = friendshipDbStorage.findFriends(1L);
        assertThat(friends).extracting(User::getId).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void findCommonFriends_shouldReturnIntersection() {
        friendshipDbStorage.persist(new Friendship(1L, 2L));
        friendshipDbStorage.persist(new Friendship(1L, 3L));
        friendshipDbStorage.persist(new Friendship(2L, 3L));
        friendshipDbStorage.persist(new Friendship(2L, 1L));

        List<User> common = friendshipDbStorage.findCommonFriends(1L, 2L);
        assertThat(common).extracting(User::getId).containsExactly(3L);
    }
}

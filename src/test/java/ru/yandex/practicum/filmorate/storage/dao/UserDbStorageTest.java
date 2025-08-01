package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class UserDbStorageTest {

    private static final LocalDate DATE = LocalDate.of(1990, 1, 1);

    private final UserDbStorage userDbStorage;

    private User makeUser(String email, String login, String name) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(DATE);
        return user;
    }

    @Test
    void persist_shouldSaveUserAndAssignId() {
        User user = makeUser("a@b.com", "login1", "name1");

        User saved = userDbStorage.persist(user);

        assertThat(saved.getId()).isNotNull();

        Optional<User> fromDb = userDbStorage.find(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void find_shouldReturnEmptyWhenNotFound() {
        Optional<User> user = userDbStorage.find(999L);
        assertThat(user).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllPersistedUsers() {
        userDbStorage.persist(makeUser("1@b.com", "login1", "name1"));
        userDbStorage.persist(makeUser("2@b.com", "login2", "name2"));

        Collection<User> users = userDbStorage.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void update_shouldModifyExistingUser() {
        User user = userDbStorage.persist(makeUser("a@b.com", "login1", "name1"));

        user.setName("Updated");
        Optional<User> updated = userDbStorage.update(user);

        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated");

        Optional<User> reloaded = userDbStorage.find(user.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getName()).isEqualTo("Updated");
    }

    @Test
    void update_shouldReturnEmptyWhenUserNotFound() {
        User user = makeUser("x@y.com", "ghost", "Ghost");
        user.setId(999L);

        Optional<User> result = userDbStorage.update(user);
        assertThat(result).isEmpty();
    }

    @Test
    void remove_shouldDeleteUserAndRelatedData() {
        User user = makeUser("del@user.com", "deluser", "Delete Me");
        User saved = userDbStorage.persist(user);

        Long userId = saved.getId();

        userDbStorage.remove(userId);

        Optional<User> deletedUser = userDbStorage.find(userId);
        assertThat(deletedUser).isEmpty();
    }
}

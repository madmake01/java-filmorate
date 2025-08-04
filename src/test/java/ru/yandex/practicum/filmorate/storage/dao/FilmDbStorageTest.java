package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.config.TestJdbcConfig;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.TestEntityFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = TestJdbcConfig.class)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikeDbStorage likeDbStorage;

    @Test
    void persist_shouldInsertFilmAndSetId() {
        Film film = TestEntityFactory.createFilm("Film 1");

        Film saved = filmDbStorage.persist(film);

        assertThat(saved.getId()).isNotNull();
        Optional<Film> loaded = filmDbStorage.find(saved.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("Film 1");
    }

    @Test
    void find_shouldReturnEmptyIfNotExists() {
        Optional<Film> result = filmDbStorage.find(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllFilms() {
        filmDbStorage.persist(TestEntityFactory.createFilm("A"));
        filmDbStorage.persist(TestEntityFactory.createFilm("B"));

        Collection<Film> films = filmDbStorage.findAll();

        assertThat(films).hasSize(2);
    }

    @Test
    void update_shouldModifyFilm() {
        Film film = filmDbStorage.persist(TestEntityFactory.createFilm("Old Name"));
        film.setName("Updated Name");

        Optional<Film> updated = filmDbStorage.update(film);

        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Name");

        Optional<Film> reloaded = filmDbStorage.find(film.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void update_shouldReturnEmptyIfNotFound() {
        Film film = TestEntityFactory.createFilm("Ghost");
        film.setId(999L);

        Optional<Film> result = filmDbStorage.update(film);

        assertThat(result).isEmpty();
    }

    @Nested
    class CommonFilmsTests {

        @Autowired
        private UserDbStorage userDbStorage;
        @Autowired
        private LikeDbStorage likeDbStorage;

        @Test
        void shouldReturnCommonLikedFilmsForTwoUsers() {
            User user1 = userDbStorage.persist(TestEntityFactory.createUser("User1"));
            User user2 = userDbStorage.persist(TestEntityFactory.createUser("User2"));
            Film film = filmDbStorage.persist(TestEntityFactory.createFilm("Common Film"));
            likeDbStorage.addLike(new Like(user1.getId(), film.getId()));
            likeDbStorage.addLike(new Like(user2.getId(), film.getId()));

            List<Film> commonFilms = filmDbStorage.findCommonFilms(user1.getId(), user2.getId());

            assertThat(commonFilms).hasSize(1);
            assertThat(commonFilms.getFirst().getId()).isEqualTo(film.getId());
            assertThat(commonFilms.getFirst().getName()).isEqualTo("Common Film");
        }

        @Test
        void shouldReturnTwoCommonFilmsForUsersOneAndTwoSortedByLikeCountDesc() {
            User user1 = userDbStorage.persist(TestEntityFactory.createUser("User1"));
            User user2 = userDbStorage.persist(TestEntityFactory.createUser("User2"));
            User user3 = userDbStorage.persist(TestEntityFactory.createUser("User3"));
            Film lessPopular = TestEntityFactory.createFilm("Film 2");
            lessPopular.setGenres(List.of());
            Film morePopular = TestEntityFactory.createFilm("Film 2");
            morePopular.setGenres(List.of());
            lessPopular = filmDbStorage.persist(lessPopular);
            morePopular = filmDbStorage.persist(morePopular);
            likeDbStorage.addLike(new Like(user1.getId(), lessPopular.getId()));
            likeDbStorage.addLike(new Like(user2.getId(), lessPopular.getId()));
            likeDbStorage.addLike(new Like(user1.getId(), morePopular.getId()));
            likeDbStorage.addLike(new Like(user2.getId(), morePopular.getId()));
            likeDbStorage.addLike(new Like(user3.getId(), morePopular.getId()));

            List<Film> result = filmDbStorage.findCommonFilms(user1.getId(), user2.getId());

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(morePopular.getId());
            assertThat(result.get(1).getId()).isEqualTo(lessPopular.getId());
        }

    }

    @Test
    void remove_shouldDeleteFilmAndRelatedData() {
        Film film = TestEntityFactory.createFilm("Film to delete");
        Film saved = filmDbStorage.persist(film);
        Long filmId = saved.getId();

        User user = userDbStorage.persist(TestEntityFactory.createUser("User for like"));
        likeDbStorage.addLike(new Like(user.getId(), filmId));

        Optional<Film> beforeDelete = filmDbStorage.find(filmId);
        assertThat(beforeDelete).isPresent();

        filmDbStorage.remove(filmId);

        Optional<Film> afterDelete = filmDbStorage.find(filmId);
        assertThat(afterDelete).isEmpty();
    }
}

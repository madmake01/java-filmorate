package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
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

    // дополнительные бины для вставки пользователей, лайков и прямой доступ к JdbcTemplate
    @Autowired
    private UserDbStorage userDbStorage;
    @Autowired
    private LikeDbStorage likeDbStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            assertThat(commonFilms.get(0).getId()).isEqualTo(film.getId());
            assertThat(commonFilms.get(0).getName()).isEqualTo("Common Film");
        }

        @Test
        void shouldReturnTwoCommonFilmsForUsersOneAndTwoSortedByLikeCountDesc() {
            User user1 = userDbStorage.persist(TestEntityFactory.createUser("User1"));
            User user2 = userDbStorage.persist(TestEntityFactory.createUser("User2"));
            User user3 = userDbStorage.persist(TestEntityFactory.createUser("User3"));
            Film lessPopular = TestEntityFactory.createFilm("Film 2");
            lessPopular = filmDbStorage.persist(lessPopular);
            Film morePopular = TestEntityFactory.createFilm("Film 3");
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
    void searchByTitleSubstring() {
        Film film1 = filmDbStorage.persist(TestEntityFactory.createFilm("Крадущийся тигр"));
        Film film2 = filmDbStorage.persist(TestEntityFactory.createFilm("Ночной тигр"));

        User user1 = userDbStorage.persist(TestEntityFactory.createUser("User1"));
        User user2 = userDbStorage.persist(TestEntityFactory.createUser("User2"));
        likeDbStorage.addLike(new Like(user1.getId(), film1.getId()));
        likeDbStorage.addLike(new Like(user2.getId(), film1.getId()));
        likeDbStorage.addLike(new Like(user1.getId(), film2.getId()));

        List<Film> result = filmDbStorage.search("тигр", List.of("title"));
        assertThat(result)
                .extracting(Film::getId)
                .containsExactly(film1.getId(), film2.getId());
    }

    @Test
    void searchByDirectorSubstring() {
        // создаём фильм
        Film film = filmDbStorage.persist(TestEntityFactory.createFilm("Random Film"));
        // вручную вставляем режиссёра
        long directorId = 123L;
        jdbcTemplate.update(
                "INSERT INTO directors (id, name) VALUES (?, ?)",
                directorId, "Крадущийся режиссёр"
        );
        // связываем фильм и режиссёра
        jdbcTemplate.update(
                "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                film.getId(), directorId
        );

        List<Film> result = filmDbStorage.search("крад", List.of("director"));
        assertThat(result)
                .extracting(Film::getId)
                .containsExactly(film.getId());
    }

    @Test
    void searchBothFieldsSortedByLikes() {
        Film lessPopular = filmDbStorage.persist(TestEntityFactory.createFilm("Тихий тигр"));
        Film morePopular = filmDbStorage.persist(TestEntityFactory.createFilm("Громкий тигр"));

        User user1 = userDbStorage.persist(TestEntityFactory.createUser("User1"));
        User user2 = userDbStorage.persist(TestEntityFactory.createUser("User2"));
        likeDbStorage.addLike(new Like(user1.getId(), morePopular.getId()));
        likeDbStorage.addLike(new Like(user2.getId(), morePopular.getId()));
        likeDbStorage.addLike(new Like(user1.getId(), lessPopular.getId()));

        List<Film> result = filmDbStorage.search("тигр", List.of("title", "director"));
        assertThat(result)
                .extracting(Film::getId)
                .containsExactly(morePopular.getId(), lessPopular.getId());
    }
}

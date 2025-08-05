package ru.yandex.practicum.filmorate.storage.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql({"/schema.sql", "/data.sql"})
class ReviewDbStorageTest {

    @Autowired
    private ReviewStorage reviewStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Review sample;

    @BeforeEach
    void setUp() {
        // Заводим пользователей 1,2,3 (чтобы лайки/дизлайки от юзеров 2 и 3 не ломали FK)
        jdbcTemplate.update(
                "MERGE INTO users (user_id, email, login, name, birthday) " +
                        "KEY(user_id) VALUES (1, 'u1@test.com', 'u1', 'User One', '1990-01-01')"
        );
        jdbcTemplate.update(
                "MERGE INTO users (user_id, email, login, name, birthday) " +
                        "KEY(user_id) VALUES (2, 'u2@test.com', 'u2', 'User Two', '1991-02-02')"
        );
        jdbcTemplate.update(
                "MERGE INTO users (user_id, email, login, name, birthday) " +
                        "KEY(user_id) VALUES (3, 'u3@test.com', 'u3', 'User Three', '1992-03-03')"
        );
        // Заводим фильм 1
        jdbcTemplate.update(
                "MERGE INTO films (film_id, name, description, release_date, duration) " +
                        "KEY(film_id) VALUES (1, 'Test Film', 'Desc', '2000-01-01', 120)"
        );

        sample = new Review();
        sample.setContent("Отличный фильм!");
        sample.setPositive(true);
        sample.setUserId(1L);
        sample.setFilmId(1L);
        // useful по умолчанию = 0
    }

    @Test
    void addAndGetById() {
        Review created = reviewStorage.addReview(sample);
        Optional<Review> fromDb = reviewStorage.getReviewById(created.getReviewId());

        assertThat(fromDb).isPresent();
        Review loaded = fromDb.get();
        assertThat(loaded.getContent()).isEqualTo("Отличный фильм!");
        assertThat(loaded.getPositive()).isTrue();
        assertThat(loaded.getUseful()).isZero();
    }

    @Test
    void updateReview() {
        Review created = reviewStorage.addReview(sample);
        created.setContent("Если честно, так себе");
        created.setPositive(false);

        Review updated = reviewStorage.updateReview(created);
        assertThat(updated.getContent()).isEqualTo("Если честно, так себе");
        assertThat(updated.getPositive()).isFalse();
    }

    @Test
    void deleteReview() {
        Review created = reviewStorage.addReview(sample);
        long id = created.getReviewId();

        reviewStorage.deleteReview(id);
        assertThat(reviewStorage.getReviewById(id)).isEmpty();
    }

    @Test
    void likeAndDislikeFlow() {
        Review created = reviewStorage.addReview(sample);
        long id = created.getReviewId();

        // лайк от userId = 2
        reviewStorage.addLike(id, 2L);
        assertThat(reviewStorage.getReviewById(id).get().getUseful()).isEqualTo(1);

        // дизлайк от userId = 3
        reviewStorage.addDislike(id, 3L);
        assertThat(reviewStorage.getReviewById(id).get().getUseful()).isEqualTo(0);

        // убрать лайк
        reviewStorage.removeLike(id, 2L);
        assertThat(reviewStorage.getReviewById(id).get().getUseful()).isEqualTo(-1);

        // убрать дизлайк
        reviewStorage.removeDislike(id, 3L);
        assertThat(reviewStorage.getReviewById(id).get().getUseful()).isZero();
    }

    @Test
    void getReviewsByFilmIdAndAll() {
        // Добавляем сначала sample
        reviewStorage.addReview(sample);

        // а затем ещё один отзыв к тому же фильму
        Review another = new Review();
        another.setContent("Второй отзыв");
        another.setPositive(true);
        another.setUserId(1L);
        another.setFilmId(1L);
        reviewStorage.addReview(another);

        // Теперь точно два отзыва
        List<Review> all = reviewStorage.getAllReviews(10);
        assertThat(all.size()).isGreaterThanOrEqualTo(2);

        // И оба относятся к filmId=1
        List<Review> byFilm = reviewStorage.getReviewsByFilmId(1L, 5);
        assertThat(byFilm).allMatch(r -> r.getFilmId().equals(1L));
    }
}

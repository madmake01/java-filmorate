package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Реализация ReviewStorage на основе JdbcTemplate.
 */
@Repository
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    // RowMapper для преобразования строки ResultSet в объект Review
    private final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> {
        Review review = new Review();
        review.setReviewId(rs.getLong("review_id"));
        review.setContent(rs.getString("content"));
        review.setPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getLong("user_id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUseful(rs.getInt("useful"));
        return review;
    };

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        return getReviewById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден после вставки: " + id));
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        int updated = jdbcTemplate.update(sql,
                review.getContent(),
                review.getPositive(),
                review.getReviewId());
        if (updated == 0) {
            throw new EntityNotFoundException("Отзыв не найден: " + review.getReviewId());
        }
        return getReviewById(review.getReviewId())
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден после обновления: " + review.getReviewId()));
    }

    @Override
    public void deleteReview(long reviewId) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ?", reviewId);
        int removed = jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
        if (removed == 0) {
            throw new EntityNotFoundException("Отзыв не найден: " + reviewId);
        }
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        List<Review> list = jdbcTemplate.query(
                "SELECT * FROM reviews WHERE review_id = ?",
                reviewRowMapper, reviewId);
        return list.stream().findFirst();
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, filmId, count);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, count);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        // если юзер ранее дизлайкал — убираем дизлайк и возвращаем +1
        int removedDislike = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = FALSE",
                reviewId, userId);
        if (removedDislike > 0) {
            jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
        }
        // ставим новый лайк
        jdbcTemplate.update(
                "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, TRUE)",
                reviewId, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        int removed = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = TRUE",
                reviewId, userId);
        if (removed > 0) {
            jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
        }
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        // если юзер ранее лайкал — убираем лайк и возвращаем -1
        int removedLike = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = TRUE",
                reviewId, userId);
        if (removedLike > 0) {
            jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
        }
        // ставим новый дизлайк
        jdbcTemplate.update(
                "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, FALSE)",
                reviewId, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        int removed = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = FALSE",
                reviewId, userId);
        if (removed > 0) {
            jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
        }
    }
}

package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Реализация ReviewStorage на основе JdbcTemplate.
 */
@Repository
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper для преобразования строки ResultSet в объект Review
    private final RowMapper<Review> reviewRowMapper = new RowMapper<>() {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setReviewId(rs.getLong("review_id"));
            review.setContent(rs.getString("content"));
            review.setPositive(rs.getBoolean("is_positive"));
            review.setUserId(rs.getLong("user_id"));
            review.setFilmId(rs.getLong("film_id"));
            review.setUseful(rs.getInt("useful"));
            return review;
        }
    };

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        // Устанавливаем сгенерированный ID и рейтинг useful = 0
        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(0);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.isPositive(),
                review.getReviewId());
        // Возвращаем обновлённый объект из БД
        return getReviewById(review.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + review.getReviewId()));
    }

    @Override
    public void deleteReview(long reviewId) {
        // Сначала удаляем все лайки/дизлайки, потом сам отзыв
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ?", reviewId);
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> list = jdbcTemplate.query(sql, reviewRowMapper, reviewId);
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
        // Добавляем запись лайка
        String insert = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
        jdbcTemplate.update(insert, reviewId, userId);
        // Увеличиваем рейтинг полезности
        jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        // Удаляем запись лайка
        String delete = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true";
        int removed = jdbcTemplate.update(delete, reviewId, userId);
        if (removed > 0) {
            // Уменьшаем рейтинг полезности
            jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
        }
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        // Добавляем запись дизлайка
        String insert = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
        jdbcTemplate.update(insert, reviewId, userId);
        // Уменьшаем рейтинг полезности
        jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        // Удаляем запись дизлайка
        String delete = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false";
        int removed = jdbcTemplate.update(delete, reviewId, userId);
        if (removed > 0) {
            // Восстанавливаем рейтинг полезности
            jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
        }
    }
}

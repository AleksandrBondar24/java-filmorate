package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.util.enums.EventType;
import ru.yandex.practicum.filmorate.util.enums.Operation;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FeedStorage feedStorage;

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM REVIEWS";
        List<Review> reviews = jdbcTemplate.query(sql, this::rowReviewToMap);
        try {
            reviews.sort(Comparator.comparing(Review::getUseful).reversed());
            return reviews;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Review create(Review review) {
        if (review.getUserId() < 0 || review.getFilmId() < 0) {
            throw new NotFoundException("Id юзера или фильма не найден.");
        }
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new ValidationException("Невалидный id юзера или фильма.");
        }
        try {
            final String sql = "INSERT INTO REVIEWS (content, is_positive, user_id, film_id, useful) " +
                    "VALUES ( ?, ?, ?, ?, ?)";
            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"review_id"});
                ps.setString(1, review.getContent());
                ps.setBoolean(2, review.getIsPositive());
                ps.setLong(3, review.getUserId());
                ps.setLong(4, review.getFilmId());
                ps.setLong(5, 0);
                return ps;
            }, keyHolder);
            review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            feedStorage.saveFeed(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
            return review;
        } catch (ValidationException e) {
            throw new ValidationException("Review не прошел валидацию.");
        }
    }

    @Override
    public Review put(Review review) {
        String sql = "UPDATE REVIEWS SET content = ?, is_positive = ? " +
                " WHERE review_id = ?";
        int count = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        if (count == 0) {
            throw new NotFoundException("" + review.getReviewId());
        }
        Long userId = jdbcTemplate.query("SELECT u.user_id FROM USERS u " +
                        "LEFT JOIN REVIEWS r ON u.user_id = r.user_id " +
                        "WHERE review_id = ?", (rs, rowNum) -> mapperInt(rs), review.getReviewId())
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользватель не найден"));
        feedStorage.saveFeed(userId, EventType.REVIEW, Operation.UPDATE, review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public void delete(Long reviewId) {
        Long userId = jdbcTemplate.query("SELECT u.user_id FROM USERS u " +
                        "LEFT JOIN REVIEWS r ON u.user_id = r.user_id " +
                        "WHERE review_id = ?", (rs, rowNum) -> mapperInt(rs), reviewId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользватель не найден"));
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
        feedStorage.saveFeed(userId, EventType.REVIEW, Operation.REMOVE, reviewId);
    }

    private Long mapperInt(ResultSet rs) throws SQLException {
        long userId = rs.getLong("user_id");
        return userId;
    }

    @Override
    public Review findById(Long id) {
        String sql = "SELECT * FROM REVIEWS WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, this::rowReviewToMap, id);
        if (reviews.size() != 0) {
            log.info("Найден review с id {}", id);
            return reviews.get(0);
        } else {
            log.info("Review с id {} не найден", id);
            throw new NotFoundException(String.format("Review с id %d не найден.", id));
        }
    }

    @Override
    public List<Review> getAllReviewByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM REVIEWS WHERE film_id = ? ORDER BY useful DESC";
        return jdbcTemplate.query(sql, (this::rowReviewToMap), filmId).stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLikeDislike(Long reviewId, Long userId, int count) {
        String sql = "SELECT * FROM REVIEWS_RATINGS WHERE user_id = ? AND review_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, reviewId);
        if (rowSet.next()) {
            String sqlUpdate = "UPDATE REVIEWS_RATINGS SET rate = ? WHERE user_id = ? AND review_id = ?";
            jdbcTemplate.update(sqlUpdate, count, userId, reviewId);
        } else {
            String sglRate = "INSERT INTO REVIEWS_RATINGS (review_id, user_id, rate) VALUES ( ?, ?, ?)";
            jdbcTemplate.update(sglRate, reviewId, userId, count);
        }
        updateUseful(count, reviewId);
    }

    @Override
    public void deleteLikeDislike(Long reviewId, Long userId, int count) {
        String sglRate = "DELETE FROM REVIEWS_RATINGS WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sglRate, reviewId, userId);
        updateUseful(count, reviewId);
    }

    private void updateUseful(int count, Long reviewId) {
        String sql = "UPDATE REVIEWS SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(sql, count, reviewId);
    }

    private Review rowReviewToMap(ResultSet rs, int rowNum) throws SQLException {
        Review review = Review.builder()
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
        review.setReviewId(rs.getLong("review_id"));
        review.setUseful(rs.getLong("useful"));
        return review;
    }
}


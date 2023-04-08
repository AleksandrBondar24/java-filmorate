package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    List<Review> getAllReviews();

    Review create(Review review);

    Review put(Review review);

    void delete(Long id);

    Review findById(Long id);

    List<Review> getAllReviewByFilmId(Long filmId, int count);

    void addLikeDislike(Long reviewId, Long userId, int count);

    void deleteLikeDislike(Long reviewId, Long userId, int count);
}

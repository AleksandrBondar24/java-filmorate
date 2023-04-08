package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public List<Review> findAll() {
        return reviewStorage.getAllReviews();
    }

    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public Review put(Review review) {
        return reviewStorage.put(review);
    }

    public void delete(Long id) {
        reviewStorage.delete(id);
    }

    public Review getReviewById(Long id) {
        return reviewStorage.findById(id);
    }

    public List<Review> getAllReviewByFilmId(Long filmId, int count) {
        return reviewStorage.getAllReviewByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        int like = 1;
        reviewStorage.addLikeDislike(reviewId, userId, like);
        log.info("Добавлен лайк {}", reviewStorage.findById(reviewId));
    }

    public void deleteLike(Long reviewId, Long userId) {
        int dislike = -1;
        reviewStorage.deleteLikeDislike(reviewId, userId, dislike);
    }

    public void addDislike(Long reviewId, Long userId) {
        int like = -1;
        reviewStorage.addLikeDislike(reviewId, userId, like);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        int dislike = 1;
        reviewStorage.deleteLikeDislike(reviewId, userId, dislike);
    }
}


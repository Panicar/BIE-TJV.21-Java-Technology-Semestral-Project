package com.panicar.epistemic.harmony.controller;

import com.panicar.epistemic.harmony.dto.ReviewDTO;
import com.panicar.epistemic.harmony.entity.Review;
import com.panicar.epistemic.harmony.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long itemId = Long.valueOf(request.get("itemId").toString());
            Integer rating = Integer.valueOf(request.get("rating").toString());
            String comment = request.get("comment") != null ? request.get("comment").toString() : null;

            Review review = reviewService.createReview(userId, itemId, rating, comment);
            return new ResponseEntity<>(new ReviewDTO(review), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(review -> new ResponseEntity<>(new ReviewDTO(review), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews()
                .stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByUser(userId)
                .stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByItem(@PathVariable Long itemId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByItem(itemId)
                .stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRating(@PathVariable Integer rating) {
        List<ReviewDTO> reviews = reviewService.getReviewsByRating(rating)
                .stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/min-rating/{minRating}")
    public ResponseEntity<List<ReviewDTO>> getReviewsWithMinRating(@PathVariable Integer minRating) {
        List<ReviewDTO> reviews = reviewService.getReviewsWithMinRating(minRating)
                .stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}/with-comments")
    public ResponseEntity<List<ReviewDTO>> getReviewsWithCommentsForItem(@PathVariable Long itemId) {
        List<ReviewDTO> reviews = reviewService.getReviewsWithCommentsForItem(itemId)
                .stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}/average-rating")
    public ResponseEntity<Map<String, Double>> getAverageRatingForItem(@PathVariable Long itemId) {
        Map<String, Double> response = new HashMap<>();
        response.put("averageRating", reviewService.getAverageRatingForItem(itemId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}/count")
    public ResponseEntity<Map<String, Long>> countReviewsForItem(@PathVariable Long itemId) {
        Map<String, Long> response = new HashMap<>();
        response.put("count", reviewService.countReviewsForItem(itemId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/item/{itemId}/exists")
    public ResponseEntity<Map<String, Boolean>> hasUserReviewedItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasReviewed", reviewService.hasUserReviewedItem(userId, itemId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer rating = request.get("rating") != null
                    ? Integer.valueOf(request.get("rating").toString()) : null;
            String comment = request.get("comment") != null
                    ? request.get("comment").toString() : null;

            Review updated = reviewService.updateReview(id, rating, comment);
            return new ResponseEntity<>(new ReviewDTO(updated), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
package com.panicar.epistemic.harmony.service;

import com.panicar.epistemic.harmony.entity.EpistemicItem;
import com.panicar.epistemic.harmony.entity.Review;
import com.panicar.epistemic.harmony.entity.User;
import com.panicar.epistemic.harmony.repository.EpistemicItemRepository;
import com.panicar.epistemic.harmony.repository.ReviewRepository;
import com.panicar.epistemic.harmony.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EpistemicItemRepository epistemicItemRepository;

    // Create
    public Review createReview(Long userId, Long itemId, Integer rating, String comment) {
        // Check if user already reviewed this item
        if (reviewRepository.existsByUserIdAndEpistemicItemId(userId, itemId)) {
            throw new RuntimeException("User has already reviewed this item");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        EpistemicItem item = epistemicItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Epistemic item not found with id: " + itemId));

        Review review = new Review(rating, comment, user, item);
        review.setReviewDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    // Read - Get by ID
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // Read - Get all
    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // Read - Get by user
    @Transactional(readOnly = true)
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    // Read - Get by item
    @Transactional(readOnly = true)
    public List<Review> getReviewsByItem(Long itemId) {
        return reviewRepository.findByEpistemicItemId(itemId);
    }

    // Read - Get by rating
    @Transactional(readOnly = true)
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }

    // Read - Get reviews with minimum rating
    @Transactional(readOnly = true)
    public List<Review> getReviewsWithMinRating(Integer minRating) {
        return reviewRepository.findByRatingGreaterThanEqual(minRating);
    }

    // Read - Get reviews with maximum rating
    @Transactional(readOnly = true)
    public List<Review> getReviewsWithMaxRating(Integer maxRating) {
        return reviewRepository.findByRatingLessThanEqual(maxRating);
    }

    // Read - Get reviews with comments for an item
    @Transactional(readOnly = true)
    public List<Review> getReviewsWithCommentsForItem(Long itemId) {
        return reviewRepository.findReviewsWithCommentsForItem(itemId);
    }

    // Business logic - Get average rating
    @Transactional(readOnly = true)
    public Double getAverageRatingForItem(Long itemId) {
        Double avg = reviewRepository.getAverageRatingForItem(itemId);
        return avg != null ? avg : 0.0;
    }

    // Business logic - Count reviews
    @Transactional(readOnly = true)
    public Long countReviewsForItem(Long itemId) {
        return reviewRepository.countReviewsForItem(itemId);
    }

    // Update
    public Review updateReview(Long id, Integer rating, String comment) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (rating != null) {
            review.setRating(rating);
        }
        if (comment != null) {
            review.setComment(comment);
        }

        return reviewRepository.save(review);
    }

    // Delete
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    // Check if user has reviewed an item
    @Transactional(readOnly = true)
    public boolean hasUserReviewedItem(Long userId, Long itemId) {
        return reviewRepository.existsByUserIdAndEpistemicItemId(userId, itemId);
    }
}
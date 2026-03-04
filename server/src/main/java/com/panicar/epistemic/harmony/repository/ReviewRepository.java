package com.panicar.epistemic.harmony.repository;

import com.panicar.epistemic.harmony.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find all reviews by user
    List<Review> findByUserId(Long userId);

    // Find all reviews for a specific epistemic item
    List<Review> findByEpistemicItemId(Long itemId);

    // Find reviews by rating
    List<Review> findByRating(Integer rating);

    // Find reviews with rating greater than or equal to
    List<Review> findByRatingGreaterThanEqual(Integer rating);

    // Find reviews with rating less than or equal to
    List<Review> findByRatingLessThanEqual(Integer rating);

    // Complex query: Get average rating for an epistemic item
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.epistemicItem.id = :itemId")
    Double getAverageRatingForItem(@Param("itemId") Long itemId);

    // Complex query: Count reviews for an epistemic item
    @Query("SELECT COUNT(r) FROM Review r WHERE r.epistemicItem.id = :itemId")
    Long countReviewsForItem(@Param("itemId") Long itemId);

    // Complex query: Get reviews with comments (non-null comments)
    @Query("SELECT r FROM Review r WHERE r.epistemicItem.id = :itemId AND r.comment IS NOT NULL")
    List<Review> findReviewsWithCommentsForItem(@Param("itemId") Long itemId);

    // Check if user has already reviewed an item
    boolean existsByUserIdAndEpistemicItemId(Long userId, Long itemId);
}
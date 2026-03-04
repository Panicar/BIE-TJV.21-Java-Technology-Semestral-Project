package com.panicar.epistemic.harmony.repository;

import com.panicar.epistemic.harmony.entity.EpistemicItem;
import com.panicar.epistemic.harmony.entity.EpistemicItem.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpistemicItemRepository extends JpaRepository<EpistemicItem, Long> {

    // Find by item type
    List<EpistemicItem> findByItemType(ItemType itemType);

    // Find by category
    List<EpistemicItem> findByCategory(String category);

    // Find by item type and category
    List<EpistemicItem> findByItemTypeAndCategory(ItemType itemType, String category);

    // Find by name containing (case-insensitive search)
    List<EpistemicItem> findByNameContainingIgnoreCase(String name);

    // Find by category containing (case-insensitive search)
    List<EpistemicItem> findByCategoryContainingIgnoreCase(String category);

    // Check if item exists by name
    boolean existsByName(String name);

    // Find items with average rating above threshold
    @Query("SELECT ei FROM EpistemicItem ei " +
            "LEFT JOIN ei.reviews r " +
            "GROUP BY ei.id " +
            "HAVING AVG(r.rating) > :minRating")
    List<EpistemicItem> findItemsWithHighRating(@Param("minRating") Double minRating);

    // Get items with review count
    @Query("SELECT ei, COUNT(r) as reviewCount FROM EpistemicItem ei " +
            "LEFT JOIN ei.reviews r " +
            "GROUP BY ei.id " +
            "ORDER BY reviewCount DESC")
    List<Object[]> findItemsOrderedByReviewCount();

    // Get items by type with average rating
    @Query("SELECT ei, AVG(r.rating) as avgRating FROM EpistemicItem ei " +
            "LEFT JOIN ei.reviews r " +
            "WHERE ei.itemType = :itemType " +
            "GROUP BY ei.id " +
            "ORDER BY avgRating DESC")
    List<Object[]> findByItemTypeWithAvgRating(@Param("itemType") ItemType itemType);

    // Get all distinct categories
    @Query("SELECT DISTINCT ei.category FROM EpistemicItem ei ORDER BY ei.category")
    List<String> findAllDistinctCategories();

    // Get items by category with statistics
    @Query("SELECT ei, COUNT(r) as reviewCount, AVG(r.rating) as avgRating " +
            "FROM EpistemicItem ei " +
            "LEFT JOIN ei.reviews r " +
            "WHERE ei.category = :category " +
            "GROUP BY ei.id")
    List<Object[]> findByCategoryWithStats(@Param("category") String category);
}
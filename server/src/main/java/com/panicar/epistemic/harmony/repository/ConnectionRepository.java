package com.panicar.epistemic.harmony.repository;

import com.panicar.epistemic.harmony.entity.Connection;
import com.panicar.epistemic.harmony.entity.Connection.ConnectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    List<Connection> findByFromItemId(Long fromItemId);

    List<Connection> findByToItemId(Long toItemId);

    List<Connection> findByConnectionType(ConnectionType connectionType);

    List<Connection> findByStrengthGreaterThanEqual(Integer strength);

    @Query("SELECT c FROM Connection c WHERE " +
            "(c.fromItem.id = :itemId1 AND c.toItem.id = :itemId2) OR " +
            "(c.fromItem.id = :itemId2 AND c.toItem.id = :itemId1)")
    List<Connection> findConnectionsBetweenItems(@Param("itemId1") Long itemId1,
                                                 @Param("itemId2") Long itemId2);

    /**
     * CORRECTED COMPLEX QUERY: Find Highly-Rated Interdisciplinary Connections
     *
     * Fixes:
     * 1. Added JOIN FETCH to avoid lazy loading issues
     * 2. Used COALESCE to handle NULL ratings
     * 3. Proper subquery to filter connections
     * 4. Better ordering by strength
     */
    @Query("SELECT c FROM Connection c " +
            "JOIN FETCH c.fromItem fi " +
            "JOIN FETCH c.toItem ti " +
            "WHERE c.strength >= :minStrength " +
            "AND c.id IN (" +
            "    SELECT c2.id FROM Connection c2 " +
            "    LEFT JOIN Review rf ON rf.epistemicItem.id = c2.fromItem.id " +
            "    LEFT JOIN Review rt ON rt.epistemicItem.id = c2.toItem.id " +
            "    GROUP BY c2.id " +
            "    HAVING COALESCE(AVG(rf.rating), 0) > :minRating " +
            "    AND COALESCE(AVG(rt.rating), 0) > :minRating" +
            ") " +
            "ORDER BY c.strength DESC")
    List<Connection> findHighlyRatedConnections(@Param("minStrength") Integer minStrength,
                                                @Param("minRating") Double minRating);

    /**
     * ADDITIONAL COMPLEX QUERY: Get Connections Between Categories
     * (Required for the Interdisciplinary Analysis operation)
     */
    @Query("SELECT c FROM Connection c " +
            "JOIN FETCH c.fromItem fi " +
            "JOIN FETCH c.toItem ti " +
            "WHERE fi.category = :category1 " +
            "AND ti.category = :category2 " +
            "AND c.strength >= :minStrength " +
            "ORDER BY c.strength DESC")
    List<Connection> findConnectionsBetweenCategories(
            @Param("category1") String category1,
            @Param("category2") String category2,
            @Param("minStrength") Integer minStrength
    );

    /**
     * ADDITIONAL COMPLEX QUERY: Get Connection Statistics by Type
     * Returns: [ConnectionType, Count, AvgStrength]
     */
    @Query("SELECT c.connectionType, COUNT(c), AVG(c.strength) " +
            "FROM Connection c " +
            "JOIN c.fromItem fi " +
            "JOIN c.toItem ti " +
            "WHERE fi.category = :category1 " +
            "AND ti.category = :category2 " +
            "GROUP BY c.connectionType " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> getConnectionStatisticsBetweenCategories(
            @Param("category1") String category1,
            @Param("category2") String category2
    );

    // ConnectionRepository.java
    @Query("SELECT c FROM Connection c " +
            "JOIN FETCH c.fromItem fi " +
            "JOIN FETCH c.toItem ti " +
            "WHERE c.strength >= :minStrength " +
            "AND c.id IN (" +
            "    SELECT c2.id FROM Connection c2 " +
            "    LEFT JOIN Review rf ON rf.epistemicItem.id = c2.fromItem.id " +
            "    LEFT JOIN Review rt ON rt.epistemicItem.id = c2.toItem.id " +
            "    GROUP BY c2.id " +
            "    HAVING COALESCE(AVG(rf.rating), 0) >= :minRating " +
            "    AND COALESCE(AVG(rt.rating), 0) >= :minRating" +
            ")")
    List<Connection> findHighQualityLinks(@Param("minStrength") Integer minStrength,
                                          @Param("minRating") Double minRating);


    @Query("SELECT c FROM Connection c " +
            "JOIN FETCH c.fromItem fi " +
            "JOIN FETCH c.toItem ti " +
            "WHERE (fi.category = :cat1 AND ti.category = :cat2) " +
            "OR (fi.category = :cat2 AND ti.category = :cat1)")
    List<Connection> findInterdisciplinaryLinks(@Param("cat1") String cat1, @Param("cat2") String cat2);

}
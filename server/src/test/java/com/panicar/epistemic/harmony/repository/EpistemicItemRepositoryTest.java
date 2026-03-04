package com.panicar.epistemic.harmony.repository;

import com.panicar.epistemic.harmony.entity.EpistemicItem;
import com.panicar.epistemic.harmony.entity.Review;
import com.panicar.epistemic.harmony.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class EpistemicItemRepositoryTest {

    @Autowired
    private EpistemicItemRepository epistemicItemRepository;

    @Autowired
    private TestEntityManager entityManager; // Used to persist dependencies like User

    @Test
    @DisplayName("Should find items with average rating above threshold using complex JPQL")
    void testFindItemsWithHighRating() {
        // 1. Arrange: Create and persist a User (required for Review)
        User tester = new User("tester", "test@fit.cvut.cz", "password123", User.Role.USER, true);
        tester = entityManager.persist(tester);

        // 2. Arrange: Create a Theory
        EpistemicItem theory = new EpistemicItem(
                EpistemicItem.ItemType.THEORY, "Quantum Mechanics", "Description", "Physics"
        );

        // Add reviews using the new constructor
        theory.addReview(new Review(5, "Excellent", tester, theory));
        theory.addReview(new Review(4, "Good", tester, theory));

        // 3. Arrange: Create a Statement with a low rating
        EpistemicItem statement = new EpistemicItem(
                EpistemicItem.ItemType.STATEMENT, "Determinism", "Content", "Philosophy"
        );
        statement.addReview(new Review(1, "Poor", tester, statement));

        epistemicItemRepository.saveAll(List.of(theory, statement));
        entityManager.flush(); // Synchronize with DB
        entityManager.clear(); // Clear cache to force a real DB query

        // 4. Act: Run the complex query
        List<EpistemicItem> highRated = epistemicItemRepository.findItemsWithHighRating(3.5);

        // 5. Assert
        assertThat(highRated).hasSize(1);
        assertThat(highRated.getFirst().getName()).isEqualTo("Quantum Mechanics");
    }

    @Test
    @DisplayName("Should return statistics (count and avg) for a specific category")
    void testFindByCategoryWithStats() {
        // Arrange
        User tester = new User("statsTester", "stats@fit.cvut.cz", "password123", User.Role.USER, true);
        tester = entityManager.persist(tester);

        String category = "Ethics";
        EpistemicItem item = new EpistemicItem(EpistemicItem.ItemType.STATEMENT, "Utilitarianism", "...", category);

        item.addReview(new Review(5, "Great", tester, item));
        item.addReview(new Review(3, "Okay", tester, item));

        epistemicItemRepository.save(item);
        entityManager.flush();

        // Act
        List<Object[]> stats = epistemicItemRepository.findByCategoryWithStats(category);

        // Assert
        assertThat(stats).isNotEmpty();
        Object[] firstResult = stats.getFirst();

        Long count = (Long) firstResult[1];
        Double avg = (Double) firstResult[2];

        assertThat(count).isEqualTo(2L);
        assertThat(avg).isEqualTo(4.0);
    }
}
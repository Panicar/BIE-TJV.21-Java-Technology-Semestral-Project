package com.panicar.epistemic.harmony.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "epistemic_item")
public class EpistemicItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item", nullable = false)
    private ItemType itemType;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    // Relationships
    @OneToMany(mappedBy = "epistemicItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    public enum ItemType {
        THEORY, STATEMENT
    }

    // Constructors
    public EpistemicItem() {}

    public EpistemicItem(ItemType itemType, String name, String content, String category) {
        this.itemType = itemType;
        this.name = name;
        this.content = content;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Set<Review> getReviews() { return reviews; }
    public void setReviews(Set<Review> reviews) { this.reviews = reviews; }

    // Helper methods
    public void addReview(Review review) {
        reviews.add(review);
        review.setEpistemicItem(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setEpistemicItem(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EpistemicItem that = (EpistemicItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EpistemicItem{" +
                "id=" + id +
                ", itemType=" + itemType +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
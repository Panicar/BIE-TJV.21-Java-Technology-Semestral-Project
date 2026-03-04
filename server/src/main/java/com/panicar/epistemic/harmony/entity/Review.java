package com.panicar.epistemic.harmony.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "review_date", nullable = false, updatable = false)
    private LocalDateTime reviewDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private EpistemicItem epistemicItem;

    // Constructors
    public Review() {}

    public Review(Integer rating, String comment, User user, EpistemicItem epistemicItem) {
        this.rating = rating;
        this.comment = comment;
        this.user = user;
        this.epistemicItem = epistemicItem;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public EpistemicItem getEpistemicItem() { return epistemicItem; }
    public void setEpistemicItem(EpistemicItem epistemicItem) { this.epistemicItem = epistemicItem; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", rating=" + rating +
                ", reviewDate=" + reviewDate +
                ", userId=" + (user != null ? user.getId() : null) +
                ", epistemicItemId=" + (epistemicItem != null ? epistemicItem.getId() : null) +
                '}';
    }
}
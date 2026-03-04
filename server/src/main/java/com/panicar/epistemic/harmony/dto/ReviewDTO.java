package com.panicar.epistemic.harmony.dto;

import com.panicar.epistemic.harmony.entity.Review;
import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long itemId;
    private String itemName;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;

    public ReviewDTO() {}

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.username = review.getUser().getUsername();
        this.itemId = review.getEpistemicItem().getId();
        this.itemName = review.getEpistemicItem().getName();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewDate = review.getReviewDate();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
}
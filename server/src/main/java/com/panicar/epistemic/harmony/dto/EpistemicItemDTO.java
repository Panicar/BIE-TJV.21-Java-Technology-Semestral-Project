package com.panicar.epistemic.harmony.dto;

import com.panicar.epistemic.harmony.entity.EpistemicItem;
import com.panicar.epistemic.harmony.entity.EpistemicItem.ItemType;

public class EpistemicItemDTO {
    private Long id;
    private String name;
    private String content;
    private String category;
    private ItemType itemType;

    public EpistemicItemDTO() {}

    public EpistemicItemDTO(EpistemicItem epistemicItem) {
        this.id = epistemicItem.getId();
        this.name = epistemicItem.getName();
        this.content = epistemicItem.getContent();
        this.category = epistemicItem.getCategory();
        this.itemType = epistemicItem.getItemType();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public ItemType getItemType() { return itemType; }
    public void setItem(ItemType itemType) { this.itemType = itemType; }
}
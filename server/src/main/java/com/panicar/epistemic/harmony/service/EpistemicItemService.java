package com.panicar.epistemic.harmony.service;

import com.panicar.epistemic.harmony.entity.EpistemicItem;
import com.panicar.epistemic.harmony.entity.Review;
import com.panicar.epistemic.harmony.repository.EpistemicItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpistemicItemService {

    @Autowired
    private EpistemicItemRepository epistemicItemRepository;

    // CRUD Operations

    @Transactional
    public EpistemicItem createItem(EpistemicItem item) {
        if (epistemicItemRepository.existsByName(item.getName())) {
            throw new IllegalArgumentException("An item with this name already exists");
        }
        return epistemicItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<EpistemicItem> getAllItems() {
        return epistemicItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EpistemicItem getItemById(Long id) {
        return epistemicItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    }

    @Transactional
    public EpistemicItem updateItem(Long id, EpistemicItem updatedItem) {
        EpistemicItem item = epistemicItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));

        if (!item.getName().equals(updatedItem.getName()) &&
                epistemicItemRepository.existsByName(updatedItem.getName())) {
            throw new IllegalArgumentException("An item with this name already exists");
        }

        item.setItemType(updatedItem.getItemType());
        item.setName(updatedItem.getName());
        item.setContent(updatedItem.getContent());
        item.setCategory(updatedItem.getCategory());

        return epistemicItemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!epistemicItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Item not found with id: " + id);
        }
        epistemicItemRepository.deleteById(id);
    }

    // Business Operations

    @Transactional(readOnly = true)
    public List<EpistemicItem> getItemsByType(EpistemicItem.ItemType itemType) {
        return epistemicItemRepository.findByItemType(itemType);
    }

    @Transactional(readOnly = true)
    public List<EpistemicItem> getItemsByCategory(String category) {
        return epistemicItemRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<EpistemicItem> searchItemsByName(String name) {
        return epistemicItemRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return epistemicItemRepository.existsByName(name);
    }

    @Transactional(readOnly = true)
    public List<String> getAllDistinctCategories() {
        return epistemicItemRepository.findAllDistinctCategories();
    }

    // Optional: you can keep stats, but return simple objects like Map<String, Object> or just EpistemicItem with extra fields
    @Transactional(readOnly = true)
    public List<EpistemicItem> getItemsWithHighRating(double minRating) {
        return epistemicItemRepository.findItemsWithHighRating(minRating);
    }
}

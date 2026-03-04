package com.panicar.epistemic.harmony.controller;

import com.panicar.epistemic.harmony.dto.EpistemicItemDTO;
import com.panicar.epistemic.harmony.entity.EpistemicItem;
import com.panicar.epistemic.harmony.service.EpistemicItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/epistemic-items")
public class EpistemicItemController {

    @Autowired
    private EpistemicItemService epistemicItemService;

    // CRUD Operations

    @PostMapping
    public ResponseEntity<EpistemicItemDTO> createItem(@RequestBody EpistemicItemDTO itemDTO) {
        // Convert DTO to Entity
        EpistemicItem item = new EpistemicItem(
                itemDTO.getItemType(),
                itemDTO.getName(),
                itemDTO.getContent(),
                itemDTO.getCategory()
        );

        EpistemicItem created = epistemicItemService.createItem(item);
        return new ResponseEntity<>(new EpistemicItemDTO(created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EpistemicItemDTO>> getAllItems() {
        List<EpistemicItemDTO> items = epistemicItemService.getAllItems()
                .stream()
                .map(EpistemicItemDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpistemicItemDTO> getItemById(@PathVariable Long id) {
        EpistemicItem item = epistemicItemService.getItemById(id);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new EpistemicItemDTO(item), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EpistemicItemDTO> updateItem(@PathVariable Long id, @RequestBody EpistemicItemDTO itemDTO) {
        EpistemicItem item = new EpistemicItem(
                itemDTO.getItemType(),
                itemDTO.getName(),
                itemDTO.getContent(),
                itemDTO.getCategory()
        );
        item.setId(id);

        EpistemicItem updated = epistemicItemService.updateItem(id, item);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new EpistemicItemDTO(updated), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        epistemicItemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Business Operations
    @GetMapping("/type/{itemType}")
    public ResponseEntity<List<EpistemicItemDTO>> getItemsByType(@PathVariable EpistemicItem.ItemType itemType) {
        List<EpistemicItemDTO> items = epistemicItemService.getItemsByType(itemType)
                .stream()
                .map(EpistemicItemDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<EpistemicItemDTO>> getItemsByCategory(@PathVariable String category) {
        List<EpistemicItemDTO> items = epistemicItemService.getItemsByCategory(category)
                .stream()
                .map(EpistemicItemDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EpistemicItemDTO>> searchItemsByName(@RequestParam String name) {
        List<EpistemicItemDTO> items = epistemicItemService.searchItemsByName(name)
                .stream()
                .map(EpistemicItemDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/exists/{name}")
    public ResponseEntity<Map<String, Boolean>> checkItemExistsByName(@PathVariable String name) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", epistemicItemService.existsByName(name));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
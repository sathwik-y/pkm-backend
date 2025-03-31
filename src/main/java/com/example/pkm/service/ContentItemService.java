package com.example.pkm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.example.pkm.model.ContentItem;
import com.example.pkm.model.Users;
import com.example.pkm.repository.ContentItemRepo;

@RequiredArgsConstructor
@Service
public class ContentItemService {
    private final UserService userService;
    private final ContentItemRepo contentItemRepo;
    private final AIService aiService;

    @Transactional
    public ContentItem createItem(ContentItem contentItem, String username) {
        try {
            Users user = userService.findByUserName(username);
            if (user == null) throw new RuntimeException("User not found: " + username);
            contentItem.setUser(user);
            user.getContentItems().add(contentItem);

            String description = contentItem.getDescription();
            if (description != null && !description.trim().isEmpty()) {
                contentItem.setCategory(aiService.setAutoCategory(description, username));
                contentItem.setTitle(aiService.setAutoTitle(description));
                contentItem.setType(aiService.setAutoType(description));
                contentItem.setSummary(aiService.generateSummary(description));
            } else {
                contentItem.setCategory("Miscellaneous");
                contentItem.setTitle("Untitled");
                contentItem.setType("MISCELLANEOUS");
                contentItem.setSummary("No summary available");
            }
            ContentItem savedItem = contentItemRepo.save(contentItem);
            return savedItem;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ContentItem", e);
        }
    }
    @Transactional
    public List<ContentItem> getAllItems(String username) {
        Users user = userService.findByUserName(username);
        if (user == null) throw new RuntimeException("User not found: " + username);
        return user.getContentItems();
    }

    @Transactional
    public void deleteItem(Long id, String username) {
        ContentItem item = contentItemRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (!item.getUser().getUserName().equals(username)) {
            throw new RuntimeException("You don't have permission to delete this item");
        }
        
        contentItemRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ContentItem> getRecentItems(String username) {
        List<ContentItem> recentItems = contentItemRepo.findTop3ByUserOrderByDesc(username);
        if (recentItems == null || recentItems.isEmpty()) {
            Users user = userService.findByUserName(username);
            if (user != null && !user.getContentItems().isEmpty()) {
                return user.getContentItems().stream()
                        .filter(item -> item.getCreatedAt() != null)
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .limit(3)
                        .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
        return recentItems;
    }
}
package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.dtos.ApplicationDto;
import com.capibara.plaigroundbackend.models.dtos.CategoryDTO;
import com.capibara.plaigroundbackend.services.ApplicationService;
import com.capibara.plaigroundbackend.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class StoreControllerImpl implements StoreController {

    private final CategoryService categoryService;
    private final ApplicationService appService;

    @Override
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.status(200).body(categoryService.getAll().stream().map(CategoryDTO::new));
    }

    @Override
    public ResponseEntity<?> getStoreApps(@RequestParam Optional<String> category, @RequestParam int page, @RequestParam int size) {
        Map<String, Object> response = new HashMap<>();
        response.put("totalPages", appService.getPublicApps(category, page + 1, size).getTotalPages());
        response.put("apps", appService.getPublicApps(category, page - 1, size).stream().map(ApplicationDto::new));
        return ResponseEntity.ok(response);
    }
}

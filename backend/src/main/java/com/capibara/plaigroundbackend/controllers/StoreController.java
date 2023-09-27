package com.capibara.plaigroundbackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@RequestMapping("/api/store")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Store controller", description = "API to handle store items")
public interface StoreController {
    @GetMapping("/categories")
    @Operation(summary = "Get request to get all categories for AppStore",
            description = "No parameters needed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories successfully loaded"),
            @ApiResponse(responseCode = "500", description = "Failed to load categories"),
    })
    ResponseEntity<?> getAllCategories();

    @GetMapping
    @Operation(summary = "Return all or filtered public apps for AppStore",
            description = "Don't enter query parameter to get all public apps, or filter them with query param ?category:'nameOfCategory'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications found")
    })
    @Parameter(name = "category", description = "Application category", example = "movies")
    ResponseEntity<?> getStoreApps(@RequestParam Optional<String> category, @RequestParam int page, @RequestParam int size);
}

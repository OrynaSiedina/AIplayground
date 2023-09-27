package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.dtos.RequestForNewAppDto;
import com.capibara.plaigroundbackend.models.dtos.UpdateAppDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/applications")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Application controller", description = "API to handle creation, update and get requests for applications")
public interface ApplicationController {
    @PostMapping
    @Operation(summary = "Post request to create new application",
            description = "Enter the name of your application and its description. " +
                    "In the application prompt, you need to very specifically describe the idea about your application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application successfully created"),
            @ApiResponse(responseCode = "500", description = "Saving application to database failed")
    })
    ResponseEntity<?> createApp(@RequestBody RequestForNewAppDto request);

    @GetMapping("/{id}")
    @Operation(summary = "Get request to get application by id",
            description = "Enter the id of the application you want to get. " +
                    "If the application does not exist or you do not have access to it, you will get an error message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application successfully found"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "403", description = "You don't have access to this application")
    })
    @Parameter(name = "id", description = "Application id", example = "1")
    ResponseEntity<?> getAppById(@PathVariable Long id);

    @PutMapping("/{id}")
    @Operation(summary = "Put request to update application",
            description = "Enter the id of the application you want to update. " +
                    "If the application does not exist or you do not have access to it, you will get an error message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application successfully updated"),
            @ApiResponse(responseCode = "403", description = "You don't have access to this application"),
            @ApiResponse(responseCode = "400", description = "Application missing required data"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @Parameter(name = "id", description = "Application id", example = "1")
    ResponseEntity<?> updateApp(@RequestBody UpdateAppDTO updateAppDTO, @PathVariable Long id);

    @Operation(summary = "Get request to get all applications of the user",
            description = "You will get all applications that you have created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications successfully found"),
    })
    @GetMapping()
    ResponseEntity<?> getUsersApps();

    @Operation(summary = "Delete request to delete application",
            description = "Enter the id of the application you want to delete. " +
                    "If the application does not exist or you do not have access to it, you will get an error message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application successfully deleted"),
            @ApiResponse(responseCode = "403", description = "You don't have access to this application"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteApp(@PathVariable Long id);
}

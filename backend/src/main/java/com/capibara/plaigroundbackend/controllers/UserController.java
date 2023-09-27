package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.dtos.AddPublicApplicationDTO;
import com.capibara.plaigroundbackend.models.dtos.ChangePasswordDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User controller", description = "API to handle requests for specific user data")
public interface UserController {
    @Operation(summary = "Get request to return list of apps used by used",
            description = "Send the requests, receive data...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application list loaded"),
    })
    @GetMapping("/applications")
    ResponseEntity<?> getAllUserUsedApplications();

    @Operation(summary = "Put request to add public app to users list of used apps",
            description = "Send the requests, add app to used apps...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application list loaded"),
            @ApiResponse(responseCode = "403", description = "Application does not exist or is not public.")
    })
    @PutMapping("/applications")
    ResponseEntity<?> addAppToUsedApps(@RequestBody AddPublicApplicationDTO addPublicApplicationDTO);

    @Operation(summary = "Put request to change password",
            description = "Send the requests, change the password...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Old password matches the new one"),
            @ApiResponse(responseCode = "400", description = "You are missing required field(s): old/new password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/password")
    ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO);
}

package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.LLMSetting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/llm")
@Tag(name = "LLM Setting controller", description = "API to handle LLM settings")
public interface LLMSettingController {

    @PostMapping("")
    @Operation(summary = "Post request to set LLM settings",
            description = "Enter your LLM settings to set them.")
    ResponseEntity<?> setLLMSettings(@RequestBody LLMSetting llmSetting);

    @PatchMapping("/{id}/setDefault")
    @Operation(summary = "Patch request to set LLM settings as default",
            description = "Enter the id of the LLM settings to set them as default.")
    ResponseEntity<?> setLLMSettingsAsDefault(@PathVariable Long id);
}

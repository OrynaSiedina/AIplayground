package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.LLMSetting;
import com.capibara.plaigroundbackend.services.LLMSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LLMSettingControllerImpl implements LLMSettingController {

    private final LLMSettingService llmService;

    @Override
    public ResponseEntity<?> setLLMSettings(@RequestBody LLMSetting llmSetting) {
        llmService.save(llmSetting);
        return ResponseEntity.ok(llmSetting);
    }

    @Override
    public ResponseEntity<?> setLLMSettingsAsDefault(Long id) {
        return ResponseEntity.ok(llmService.setDefaultLLMSetting(id));
    }
}

package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.exceptions.AIPlaygroundException;
import com.capibara.plaigroundbackend.exceptions.GlobalExceptionHandler;
import com.capibara.plaigroundbackend.models.LLMSetting;
import com.capibara.plaigroundbackend.services.LLMSettingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LLMSettingControllerImplTest {

    @InjectMocks
    private LLMSettingControllerImpl llmSettingControllerImpl;

    @Mock
    private LLMSettingService llmSettingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(llmSettingControllerImpl)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add the exception handler advice
                .build();
    }

    @Test
    @DisplayName("Test setting LLM settings with valid data")
    void setLLMSettingsOk() throws Exception {
        // Prepare valid LLMSetting data
        LLMSetting llmSetting = new LLMSetting();
        llmSetting.setDefaultModel(true);
        llmSetting.setTemperature(1.5);
        llmSetting.setSystemMessage("Test system message");

        // Mock the llmService.save() method to return the LLMSetting data
        when(llmSettingService.save(any(LLMSetting.class))).thenReturn(llmSetting);

        // Send a POST request with JSON payload to set LLM settings
        mockMvc.perform(post("/api/llm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(llmSetting)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defaultModel").value(true))
                .andExpect(jsonPath("$.temperature").value(1.5))
                .andExpect(jsonPath("$.systemMessage").value("Test system message"));

        // Verify that the llmService.save() method was called with the correct data
        verify(llmSettingService, times(1)).save(any(LLMSetting.class));
    }

    @Test
    @DisplayName("Test setting LLM settings with invalid temperature")
    void setLLMSettingsInvalidTemperature() throws Exception {
        // Prepare invalid LLMSetting data with temperature > 2
        LLMSetting llmSetting = new LLMSetting();
        llmSetting.setDefaultModel(true);
        llmSetting.setTemperature(2.5); // Temperature > 2.0, which should throw IllegalArgumentException

        // Mock the llmService.save() method to throw IllegalArgumentException
        doThrow(new AIPlaygroundException("Temperature must be between 0.0 and 2.0", HttpStatusCode.valueOf(400)))
                .when(llmSettingService).save(any(LLMSetting.class));

        // Send a POST request with JSON payload to set LLM settings and expect 400 Bad Request response
        mockMvc.perform(post("/api/llm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(llmSetting)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Temperature must be between 0.0 and 2.0"));

        // Verify that the llmService.save() method was called with the correct data
        verify(llmSettingService, times(1)).save(any(LLMSetting.class));
    }

}
package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.DataRequiredException;
import com.capibara.plaigroundbackend.models.LLMSetting;
import com.capibara.plaigroundbackend.repositories.LLMSettingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LLMSettingServiceImplTest {

    @InjectMocks
    private LLMSettingServiceImpl llmSettingService;

    @Mock
    private LLMSettingRepository llmSettingRepo;

    @Test
    @DisplayName("Test save LLMSetting with valid data")
    void saveLLMSettingOk() {

        LLMSetting llmSetting = new LLMSetting();
        llmSetting.setDefaultModel(true);
        llmSetting.setTemperature(1.0);
        llmSetting.setSystemMessage("Test system message");

        when(llmSettingRepo.save(any(LLMSetting.class))).thenReturn(llmSetting);

        LLMSetting savedLLMSetting = llmSettingService.save(llmSetting);

        verify(llmSettingRepo, times(1)).save(any(LLMSetting.class));

        assertEquals(llmSetting, savedLLMSetting);
    }

    @Test
    @DisplayName("Test save LLMSetting with missing temperature and/or system message")
    void saveLLMSettingMissingData() {

        LLMSetting llmSettingMissingData = new LLMSetting();

        assertThrows(DataRequiredException.class, () -> llmSettingService.save(llmSettingMissingData));

        verify(llmSettingRepo, never()).save(any(LLMSetting.class));

        LLMSetting llmSettingMissingSystemMessage = new LLMSetting();
        llmSettingMissingSystemMessage.setTemperature(1.0);

        assertThrows(DataRequiredException.class, () -> llmSettingService.save(llmSettingMissingSystemMessage));

        LLMSetting llmSettingMissingTemperature = new LLMSetting();
        llmSettingMissingTemperature.setSystemMessage("Test system message");

        assertThrows(DataRequiredException.class, () -> llmSettingService.save(llmSettingMissingTemperature));

        verify(llmSettingRepo, never()).save(any(LLMSetting.class));
    }

}


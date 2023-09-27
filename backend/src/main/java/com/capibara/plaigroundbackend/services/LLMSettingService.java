package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.models.LLMSetting;

public interface LLMSettingService {

    LLMSetting save(LLMSetting llmSetting);

    LLMSetting findDefaultLLMSetting();

    LLMSetting findById(Long id);

    LLMSetting setDefaultLLMSetting(Long id);
}

package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.AIPlaygroundException;
import com.capibara.plaigroundbackend.exceptions.DataRequiredException;
import com.capibara.plaigroundbackend.exceptions.NotFoundException;
import com.capibara.plaigroundbackend.models.LLMSetting;
import com.capibara.plaigroundbackend.repositories.LLMSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LLMSettingServiceImpl implements LLMSettingService {

    private final LLMSettingRepository llmSettingRepo;


    private final static String conditions =
            "The HTML part of the code should be surrounded by HTML>> <<HTML marks." +
                    "Means that CSS part of the code should should start with mark HTML>> and end with <<HTML mark" +
                    "The style part of the code should be surrounded by CSS>> <<CSS marks." +
                    "Means that CSS part of the code should should start with mark CSS>> and end with <<CSS mark" +
                    "The script part of the code should be surrounded by SCRIPT>> <<SCRIPT marks." +
                    "Means that script part of the code should should start with mark SCRIPT>> and end with <<SCRIPT mark" +
                    "Response should mandatory contain all three parts.";

    @Override
    public LLMSetting save(LLMSetting llmSetting) {
        if (llmSetting.getTemperature() == null || llmSetting.getSystemMessage() == null || llmSetting.getSystemMessage().isBlank()) {
            throw new DataRequiredException("Temperature and/or system message");
        }
        if (llmSetting.getTemperature() < 0 || llmSetting.getTemperature() > 2) {
            throw new AIPlaygroundException("Temperature must be between 0 and 2", HttpStatusCode.valueOf(400));
        }
        if (llmSetting.isDefaultModel()) {
            llmSettingRepo.setAllDefaultFalse();
        }

        llmSetting.setSystemMessage(llmSetting.getSystemMessage() + conditions);

        return llmSettingRepo.save(llmSetting);
    }

    @Override
    public LLMSetting findDefaultLLMSetting() {
        Optional<LLMSetting> defaultSettingOptional = llmSettingRepo.findByDefaultModelTrue();
        if (defaultSettingOptional.isPresent()) {
            return defaultSettingOptional.get();
        }
        return fallbackLLMSettings();
    }

    @Override
    public LLMSetting findById(Long id) {
        return llmSettingRepo.findById(id).orElseThrow(() -> new NotFoundException("LLM setting" + id));
    }

    public LLMSetting setDefaultLLMSetting(Long id) {
        llmSettingRepo.setAllDefaultFalse();
        LLMSetting llmSetting = findById(id);
        llmSetting.setDefaultModel(true);
        return llmSettingRepo.save(llmSetting);
    }

    private LLMSetting fallbackLLMSettings() {
        LLMSetting llmSetting = new LLMSetting();
        llmSetting.setTemperature(0.4);
        llmSetting.setSystemMessage("You are a web application developer. " +
                "Write an application according to the user specification. " +
                "Application will run in web browser. " +
                "Write the code without comments. " +
                "Separate the answer into 3 parts. " +
                "Use single quotes for parameters. " +
                "Don't write the style directly into the html code." +
                "Use provided by user name in HTML code. " +
                conditions);
        llmSetting.setDefaultModel(true);
        return llmSetting;
    }
}

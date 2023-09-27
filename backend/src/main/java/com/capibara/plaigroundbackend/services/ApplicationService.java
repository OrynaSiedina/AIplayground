package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.models.Application;
import com.capibara.plaigroundbackend.models.dtos.ApplicationDto;
import com.capibara.plaigroundbackend.models.dtos.RequestForNewAppDto;
import com.capibara.plaigroundbackend.models.dtos.UpdateAppDTO;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    Application create(RequestForNewAppDto request);

    ChatCompletionRequest createChatRequest(RequestForNewAppDto request);

    Application build(RequestForNewAppDto request, String response);

    String parseResponse(String input, String marker);

    Application update(UpdateAppDTO updateAppDTO, Long id);

    Application getById(Long id);

    List<ApplicationDto> getUsersApps();

    Page<Application> getPublicApps(Optional<String> category, int pageNumber, int pageSize);

    Application delete(Long id);
}


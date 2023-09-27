package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.ServerException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AiServiceImpl implements AiService {

    @Value("${openai.api.key}")
    private String token;

    @Override
    public String getAiResponse(ChatCompletionRequest request) {
        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(300));

        try {
            return service.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("Fetching application from AI failed");
        }
    }
}

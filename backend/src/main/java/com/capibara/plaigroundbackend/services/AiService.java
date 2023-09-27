package com.capibara.plaigroundbackend.services;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;

public interface AiService {
    String getAiResponse(ChatCompletionRequest request);
}

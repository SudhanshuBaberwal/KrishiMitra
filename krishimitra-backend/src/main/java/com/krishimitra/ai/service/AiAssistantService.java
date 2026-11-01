package com.krishimitra.ai.service;

import com.krishimitra.ai.dto.request.ChatRequest;
import com.krishimitra.ai.dto.response.ChatResponse;
import com.krishimitra.ai.dto.response.ConversationResponse;

import java.util.List;

public interface AiAssistantService {

    ChatResponse chat(ChatRequest request, String userId);

    List<ConversationResponse> getConversations(String userId);

    ConversationResponse getConversation(String id, String userId);

    void deleteConversation(String id, String userId);
}
package com.krishimitra.ai.dto.response;

import com.krishimitra.ai.entity.ChatMessage;

import java.time.Instant;
import java.util.List;

public record ConversationResponse(

        String id,

        String title,

        List<ChatMessage> messages,

        Instant createdAt,

        Instant updatedAt

) {}
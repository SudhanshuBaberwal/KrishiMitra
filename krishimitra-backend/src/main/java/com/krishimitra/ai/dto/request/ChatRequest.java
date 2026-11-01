package com.krishimitra.ai.dto.request;

import com.krishimitra.ai.enums.ChatLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(

        String conversationId,

        @NotBlank(message = "Message cannot be empty")
        @Size(max = 4000, message = "Message cannot exceed 4000 characters")
        String message,

        ChatLanguage language

) {}
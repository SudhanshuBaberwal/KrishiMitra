package com.krishimitra.ai.dto.response;

import com.krishimitra.ai.enums.ChatLanguage;

import java.time.Instant;

public record ChatResponse(

        String conversationId,

        String reply,

        ChatLanguage language,

        Instant timestamp

) {}
package com.krishimitra.ai.entity;

import com.krishimitra.ai.enums.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private MessageRole role;

    private String content;

    private Instant timestamp;
}
package com.krishimitra.ai.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "ai_conversations")
public class ChatConversation {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;

    private List<ChatMessage> messages = new ArrayList<>();

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}
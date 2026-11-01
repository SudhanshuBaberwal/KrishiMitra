package com.krishimitra.ai.repository;

import com.krishimitra.ai.entity.ChatConversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository
        extends MongoRepository<ChatConversation, String> {

    List<ChatConversation> findByUserIdAndActiveTrueOrderByUpdatedAtDesc(
            String userId
    );

    Optional<ChatConversation> findByIdAndUserIdAndActiveTrue(
            String id,
            String userId
    );
}
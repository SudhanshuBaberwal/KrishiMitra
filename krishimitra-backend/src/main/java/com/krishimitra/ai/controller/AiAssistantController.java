package com.krishimitra.ai.controller;

import com.krishimitra.ai.dto.request.ChatRequest;
import com.krishimitra.ai.dto.response.ChatResponse;
import com.krishimitra.ai.dto.response.ConversationResponse;
import com.krishimitra.ai.service.AiAssistantService;
import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(
            AiAssistantService aiAssistantService
    ) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        ChatResponse response = aiAssistantService.chat(
                request,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "AI response generated successfully",
                        response
                )
        );
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>>
    getConversations(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        List<ConversationResponse> response =
                aiAssistantService.getConversations(
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversations fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ApiResponse<ConversationResponse>>
    getConversation(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        ConversationResponse response =
                aiAssistantService.getConversation(
                        id,
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversation fetched successfully",
                        response
                )
        );
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteConversation(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        aiAssistantService.deleteConversation(
                id,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversation deleted successfully",
                        null
                )
        );
    }
}
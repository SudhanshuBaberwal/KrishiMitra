package com.krishimitra.ai.service;

import com.krishimitra.ai.dto.request.ChatRequest;
import com.krishimitra.ai.dto.response.ChatResponse;
import com.krishimitra.ai.dto.response.ConversationResponse;
import com.krishimitra.ai.entity.ChatConversation;
import com.krishimitra.ai.entity.ChatMessage;
import com.krishimitra.ai.enums.ChatLanguage;
import com.krishimitra.ai.enums.MessageRole;
import com.krishimitra.ai.exception.AiConversationNotFoundException;
import com.krishimitra.ai.exception.AiServiceException;
import com.krishimitra.ai.repository.ChatConversationRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    private final ChatClient chatClient;
    private final ChatConversationRepository conversationRepository;
    private final FarmerContextService farmerContextService;

    public AiAssistantServiceImpl(
            ChatClient.Builder chatClientBuilder,
            ChatConversationRepository conversationRepository,
            FarmerContextService farmerContextService
    ) {
        this.chatClient = chatClientBuilder.build();
        this.conversationRepository = conversationRepository;
        this.farmerContextService = farmerContextService;
    }

    @Override
    public ChatResponse chat(ChatRequest request, String userId) {

        ChatConversation conversation;

        if (request.conversationId() == null
                || request.conversationId().isBlank()) {

            conversation = new ChatConversation();
            conversation.setUserId(userId);
            conversation.setActive(true);
            conversation.setCreatedAt(Instant.now());

        } else {

            conversation = conversationRepository
                    .findByIdAndUserIdAndActiveTrue(
                            request.conversationId(),
                            userId
                    )
                    .orElseThrow(() ->
                            new AiConversationNotFoundException(
                                    "Conversation not found"
                            )
                    );
        }

        ChatLanguage language = request.language() == null
                ? ChatLanguage.EN
                : request.language();

        String farmerContext =
                farmerContextService.buildContext(userId);

        String systemPrompt = buildSystemPrompt(language);

        String prompt = buildUserPrompt(
                request.message(),
                farmerContext,
                conversation.getMessages()
        );

        String aiReply;

        try {

            aiReply = chatClient.prompt()
                    .system(systemPrompt)
                    .user(prompt)
                    .call()
                    .content();

            if (aiReply == null || aiReply.isBlank()) {
                throw new IllegalStateException(
                        "Gemini returned an empty response"
                );
            }

        } catch (Exception ex) {

            throw new AiServiceException(
                    "Unable to generate AI response. Please try again.",
                    ex
            );
        }

        Instant now = Instant.now();

        if (conversation.getMessages() == null) {
            conversation.setMessages(new java.util.ArrayList<>());
        }

        if (conversation.getTitle() == null) {
            String title = request.message().trim();
            conversation.setTitle(
                    title.length() > 60
                            ? title.substring(0, 60)
                            : title
            );
        }

        conversation.getMessages().add(
                new ChatMessage(
                        MessageRole.USER,
                        request.message(),
                        now
                )
        );

        conversation.getMessages().add(
                new ChatMessage(
                        MessageRole.ASSISTANT,
                        aiReply,
                        now
                )
        );

        conversation.setUpdatedAt(now);

        ChatConversation saved =
                conversationRepository.save(conversation);

        return new ChatResponse(
                saved.getId(),
                aiReply,
                language,
                now
        );
    }

    @Override
    public List<ConversationResponse> getConversations(String userId) {

        return conversationRepository
                .findByUserIdAndActiveTrueOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ConversationResponse getConversation(
            String id,
            String userId
    ) {

        ChatConversation conversation =
                findConversation(id, userId);

        return toResponse(conversation);
    }

    @Override
    public void deleteConversation(String id, String userId) {

        ChatConversation conversation =
                findConversation(id, userId);

        conversation.setActive(false);
        conversation.setUpdatedAt(Instant.now());

        conversationRepository.save(conversation);
    }

    private ChatConversation findConversation(
            String id,
            String userId
    ) {
        return conversationRepository
                .findByIdAndUserIdAndActiveTrue(id, userId)
                .orElseThrow(() ->
                        new AiConversationNotFoundException(
                                "Conversation not found"
                        )
                );
    }

    private String buildSystemPrompt(ChatLanguage language) {

        String languageInstruction = switch (language) {
            case EN -> "Respond in clear, simple English.";
            case HI -> "Respond in simple Hindi.";
            case KN -> "Respond in simple Kannada.";
        };

        return """
                You are KrishiMitra AI, a responsible agricultural
                information assistant for farmers in India.

                YOUR ROLE:
                Help farmers understand their crops, farming activities,
                soil information, weather information and agricultural
                information.

                LANGUAGE:
                %s

                FARMER CONTEXT:
                Use only the farmer information provided in the user prompt.
                Do not invent missing profile, crop or location information.

                SAFETY RULES:
                1. Never invent weather forecasts, mandi prices or official
                   agricultural advisories.
                2. Do not claim that you checked live data unless it is
                   explicitly included in the supplied context.
                3. Do not invent pesticide names, pesticide dosages,
                   fertilizer quantities or chemical application schedules.
                4. For potentially harmful crop treatments, advise the farmer
                   to consult the local KVK or agriculture officer.
                5. Clearly distinguish verified information from general
                   educational guidance.
                6. If information is missing, say so honestly.
                7. Never claim that an activity has been completed unless
                   the farmer's supplied data confirms it.
                8. Give practical, concise and easy-to-understand answers.

                IMPORTANT:
                You are an informational assistant, not a replacement for
                qualified local agricultural experts.
                """.formatted(languageInstruction);
    }

    private String buildUserPrompt(
            String message,
            String farmerContext,
            List<ChatMessage> previousMessages
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("FARMER INFORMATION:\n")
                .append(farmerContext)
                .append("\n");

        if (previousMessages != null && !previousMessages.isEmpty()) {

            prompt.append("RECENT CONVERSATION HISTORY:\n");

            int start = Math.max(0, previousMessages.size() - 10);

            for (int i = start; i < previousMessages.size(); i++) {

                ChatMessage previous = previousMessages.get(i);

                prompt.append(previous.getRole())
                        .append(": ")
                        .append(previous.getContent())
                        .append("\n");
            }
        }

        prompt.append("\nCURRENT FARMER QUESTION:\n")
                .append(message);

        return prompt.toString();
    }

    private ConversationResponse toResponse(
            ChatConversation conversation
    ) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getMessages(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }
}
package com.ipnet.assistant.service;

import com.ipnet.assistant.config.OpenAiProperties;
import com.ipnet.assistant.model.AssistantConversationTurn;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AssistantConversationMemory {

    private final OpenAiProperties properties;
    private final Map<String, Deque<AssistantConversationTurn>> histories =
            new ConcurrentHashMap<>();

    public AssistantConversationMemory(OpenAiProperties properties) {
        this.properties = properties;
    }

    public List<Object> buildInput(String conversationKey) {
        Deque<AssistantConversationTurn> history = histories.get(conversationKey);
        List<Object> input = new ArrayList<>();

        if (history == null) {
            return input;
        }

        synchronized (history) {
            for (AssistantConversationTurn turn : history) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("role", turn.role());
                item.put("content", turn.content());
                input.add(item);
            }
        }

        return input;
    }

    public void remember(
            String conversationKey,
            String userMessage,
            String assistantMessage
    ) {
        Deque<AssistantConversationTurn> history = histories.computeIfAbsent(
                conversationKey,
                key -> new ArrayDeque<>()
        );

        synchronized (history) {
            history.addLast(new AssistantConversationTurn("user", userMessage));
            history.addLast(new AssistantConversationTurn("assistant", assistantMessage));

            int maximum = Math.max(2, properties.getMaxHistoryMessages());
            while (history.size() > maximum) {
                history.removeFirst();
            }
        }
    }

    public void clear(String conversationKey) {
        histories.remove(conversationKey);
    }
}

package com.pfe.dataops.dataopsapi.chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OllamaProvider implements ChatProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${llm.ollama.endpoint:http://localhost:11434/api/chat}")
    private String endpoint;

    @Value("${llm.ollama.defaultModel:llama3.1}")
    private String defaultModel;

    @Override
    public String generate(String systemPrompt, String userPrompt, String model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", (model == null || model.isBlank()) ? defaultModel : model);
        body.put("stream", false);
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        Map<?, ?> resp = restTemplate.postForObject(endpoint, req, Map.class);
        if (resp == null) throw new IllegalStateException("Empty response");
        Map<?, ?> message = (Map<?, ?>) resp.get("message");
        if (message == null) throw new IllegalStateException("No message");
        Object content = message.get("content");
        if (content == null) throw new IllegalStateException("No content");
        return content.toString();
    }
}

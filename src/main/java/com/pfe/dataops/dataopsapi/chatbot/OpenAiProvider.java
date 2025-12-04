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
public class OpenAiProvider implements ChatProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${llm.openai.apiKey:}")
    private String apiKey;

    @Value("${llm.openai.endpoint:https://api.openai.com/v1/chat/completions}")
    private String endpoint;

    @Value("${llm.openai.defaultModel:gpt-4o-mini}")
    private String defaultModel;

    @Override
    public String generate(String systemPrompt, String userPrompt, String model) {
        if (apiKey == null || apiKey.isBlank()) throw new IllegalStateException("OPENAI API key missing");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", (model == null || model.isBlank()) ? defaultModel : model);
        body.put("temperature", 0.2);
        Map<String, String> sys = Map.of("role", "system", "content", systemPrompt);
        Map<String, String> usr = Map.of("role", "user", "content", userPrompt);
        body.put("messages", List.of(sys, usr));

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        Map<?, ?> resp = restTemplate.postForObject(endpoint, req, Map.class);
        if (resp == null) throw new IllegalStateException("Empty response");
        List<?> choices = (List<?>) resp.get("choices");
        if (choices == null || choices.isEmpty()) throw new IllegalStateException("No choices");
        Object first = choices.get(0);
        if (!(first instanceof Map)) throw new IllegalStateException("Invalid response");
        Map<?, ?> firstMap = (Map<?, ?>) first;
        Map<?, ?> message = (Map<?, ?>) firstMap.get("message");
        if (message == null) throw new IllegalStateException("No message");
        Object content = message.get("content");
        if (content == null) throw new IllegalStateException("No content");
        return content.toString();
    }
}

package com.pfe.dataops.dataopsapi.chatbot;

public interface ChatProvider {
    String generate(String systemPrompt, String userPrompt, String model);
}

package com.pfe.dataops.dataopsapi.chatbot;

public record ChatReportResponse(
        String report,
        String provider,
        String model
) {}

package com.pfe.dataops.dataopsapi.chatbot;

public record ChatReportRequest(
        Long runAId,
        Long runBId,
        String provider,
        String model,
        String extra
) {}

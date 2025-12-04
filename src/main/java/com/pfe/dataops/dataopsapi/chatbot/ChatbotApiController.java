package com.pfe.dataops.dataopsapi.chatbot;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotApiController {

    private final ChatbotService service;

    public ChatbotApiController(ChatbotService service) {
        this.service = service;
    }

    // POST /api/chatbot/ask -> plain text response
    @PostMapping(value = "/ask", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ask(@RequestBody AskRequest body) {
        if (body == null || body.prompt() == null) return "";
        return service.ask(body.prompt());
    }

    // GET /api/chatbot/report?runAId=X&runBId=Y -> Markdown
    @GetMapping(value = "/report", produces = "text/markdown")
    public String report(@RequestParam Long runAId, @RequestParam Long runBId) {
        return service.generateReportMarkdown(runAId, runBId);
    }

    public static record AskRequest(String prompt) {}
}

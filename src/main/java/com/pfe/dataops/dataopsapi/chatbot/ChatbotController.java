package com.pfe.dataops.dataopsapi.chatbot;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService service;

    public ChatbotController(ChatbotService service) {
        this.service = service;
    }

    @PostMapping("/report")
    public ChatReportResponse report(@RequestBody ChatReportRequest req) {
        try {
            return service.generate(req);
        } catch (Exception e) {
            String html = service.toHtml("Erreur: " + (e.getMessage() == null ? "" : e.getMessage()));
            return new ChatReportResponse(html, req.provider(), req.model());
        }
    }

    @PostMapping("/report/latest")
    public ChatReportResponse reportLatest(
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String extra
    ) {
        try {
            return service.generateLatest(provider, model, extra);
        } catch (Exception e) {
            String html = service.toHtml("Erreur: " + (e.getMessage() == null ? "" : e.getMessage()));
            return new ChatReportResponse(html, provider, model);
        }
    }

    @GetMapping(value = "/report/latest/html", produces = "text/html")
    public String reportLatestHtml(
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String extra
    ) {
        try {
            return service.generateLatestHtml(provider, model, extra);
        } catch (Exception e) {
            return service.toHtml("Erreur: " + (e.getMessage() == null ? "" : e.getMessage()));
        }
    }

    @GetMapping(value = "/report/html", produces = "text/html")
    public String reportHtml(
            @RequestParam Long runAId,
            @RequestParam Long runBId,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String extra
    ) {
        try {
            return service.generateHtml(new ChatReportRequest(runAId, runBId, provider, model, extra));
        } catch (Exception e) {
            return service.toHtml("Erreur: " + (e.getMessage() == null ? "" : e.getMessage()));
        }
    }
}

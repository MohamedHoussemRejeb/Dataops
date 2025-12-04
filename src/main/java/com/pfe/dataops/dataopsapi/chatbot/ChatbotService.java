package com.pfe.dataops.dataopsapi.chatbot;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import com.pfe.dataops.dataopsapi.backend.etl.EtlRunRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    private final EtlRunRepository etlRunRepository;
    private final OpenAiProvider openai;
    private final OllamaProvider ollama;
    private final OpenRouterProvider openrouter;

    public ChatbotService(EtlRunRepository etlRunRepository, OpenAiProvider openai, OllamaProvider ollama, OpenRouterProvider openrouter) {
        this.etlRunRepository = etlRunRepository;
        this.openai = openai;
        this.ollama = ollama;
        this.openrouter = openrouter;
    }

    public ChatReportResponse generate(ChatReportRequest req) {
        EtlRun a = etlRunRepository.findById(req.runAId()).orElseThrow(() -> new RuntimeException("Run A not found"));
        EtlRun b = etlRunRepository.findById(req.runBId()).orElseThrow(() -> new RuntimeException("Run B not found"));

        String sys = "Tu es un analyste Senior DataOps. Tu compares deux exécutions et produis un rapport clair, factuel, actionnable.";
        String user = buildUserPrompt(a, b, req.extra());
        String model = req.model();
        String prov = (req.provider() == null || req.provider().isBlank()) ? "openrouter" : req.provider().toLowerCase();
        ChatProvider p = switch (prov) {
            case "openrouter" -> openrouter;
            case "ollama" -> ollama;
            case "openai" -> openai;
            default -> openai;
        };
        String report;
        try {
            report = p.generate(sys, user, model);
        } catch (Exception e) {
            report = buildFallbackReport(a, b, req.extra(), e.getMessage());
        }
        return new ChatReportResponse(report, prov, (model == null || model.isBlank()) ? null : model);
    }

    public ChatReportResponse generateLatest(String provider, String model, String extra) {
        var latestPage = etlRunRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 2, org.springframework.data.domain.Sort.by("createdAt").descending()));
        var latest = latestPage.getContent();
        if (latest == null || latest.size() < 2) throw new RuntimeException("Not enough runs to compare");
        EtlRun a = latest.get(0);
        EtlRun b = latest.get(1);
        String sys = "Tu es un analyste Senior DataOps. Tu compares deux exécutions et produis un rapport clair, factuel, actionnable.";
        String user = buildUserPrompt(a, b, extra);
        String prov = (provider == null || provider.isBlank()) ? "openrouter" : provider.toLowerCase();
        ChatProvider p = switch (prov) {
            case "openrouter" -> openrouter;
            case "ollama" -> ollama;
            case "openai" -> openai;
            default -> openai;
        };
        String report;
        try {
            report = p.generate(sys, user, model);
        } catch (Exception e) {
            report = buildFallbackReport(a, b, extra, e.getMessage());
        }
        return new ChatReportResponse(report, prov, (model == null || model.isBlank()) ? null : model);
    }

    private String buildFallbackReport(EtlRun a, EtlRun b, String extra, String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Fallback sans LLM]\n");
        if (error != null && !error.isBlank()) sb.append("Raison: ").append(error).append("\n\n");
        sb.append("Executive Summary\n");
        sb.append("- A: ").append(a.getJobName()).append(" ").append(a.getStatus()).append("\n");
        sb.append("- B: ").append(b.getJobName()).append(" ").append(b.getStatus()).append("\n");
        sb.append("- Δ rows_out: ").append(delta(a.getRowsOut(), b.getRowsOut())).append("\n\n");

        sb.append("Métriques clés\n");
        sb.append("A rows_in= ").append(a.getRowsIn()).append(", rows_out= ").append(a.getRowsOut()).append(", errors= ").append(a.getRowsError()).append(", dur_ms= ").append(a.getDurationMs()).append("\n");
        sb.append("B rows_in= ").append(b.getRowsIn()).append(", rows_out= ").append(b.getRowsOut()).append(", errors= ").append(b.getRowsError()).append(", dur_ms= ").append(b.getDurationMs()).append("\n\n");

        sb.append("Comparaison\n");
        sb.append("- Δ rows_in: ").append(delta(a.getRowsIn(), b.getRowsIn())).append("\n");
        sb.append("- Δ rows_out: ").append(delta(a.getRowsOut(), b.getRowsOut())).append("\n");
        sb.append("- Δ errors: ").append(delta(a.getRowsError(), b.getRowsError())).append("\n");
        sb.append("- Δ dur_ms: ").append(delta(a.getDurationMs(), b.getDurationMs())).append("\n\n");

        if (extra != null && !extra.isBlank()) {
            sb.append("Contexte\n");
            sb.append(extra).append("\n");
        }

        return sb.toString();
    }

    private String delta(Long a, Long b) {
        if (a == null && b == null) return "";
        if (a == null) return "-" + b;
        if (b == null) return "+" + a;
        long d = a - b;
        return (d >= 0 ? "+" : "") + d;
    }

    public String toHtml(String text) {
        String s = text == null ? "" : text;
        s = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<!doctype html><html><head><meta charset=\"utf-8\"><title>Rapport</title><style>body{font-family:system-ui,Segoe UI,Roboto,Arial}pre{white-space:pre-wrap}</style></head><body><pre>" + s + "</pre></body></html>";
    }

    public String generateLatestHtml(String provider, String model, String extra) {
        ChatReportResponse r = generateLatest(provider, model, extra);
        return toHtml(r.report());
    }

    public String generateHtml(ChatReportRequest req) {
        ChatReportResponse r = generate(req);
        return toHtml(r.report());
    }

    /**
     * Simple free-text ask endpoint backed by OpenRouter only.
     */
    public String ask(String prompt) {
        String sys = "Tu es un assistant DataOps concis et utile. Réponds en texte clair.";
        String model = "gpt-4o-mini";
        // Force OpenRouter provider
        return openrouter.generate(sys, prompt, model);
    }

    /**
     * Generate a human-readable Markdown report comparing two runs using OpenRouter.
     */
    public String generateReportMarkdown(Long runAId, Long runBId) {
        EtlRun a = etlRunRepository.findById(runAId).orElseThrow(() -> new RuntimeException("Run A not found"));
        EtlRun b = etlRunRepository.findById(runBId).orElseThrow(() -> new RuntimeException("Run B not found"));

        String sys = "Tu es un analyste Senior DataOps. Produis un rapport Markdown clair, factuel et actionnable. Utilise titres, listes et tableaux légers si utile.";
        String user = buildUserPrompt(a, b, null);
        String model = "gpt-4o-mini";

        String report;
        try {
            report = openrouter.generate(sys, user, model);
        } catch (Exception e) {
            // fallback to plain text summary converted to Markdown
            StringBuilder md = new StringBuilder();
            md.append("# Rapport comparatif (fallback)\n\n");
            md.append("**Raison fallback:** ").append(e.getMessage()).append("\n\n");
            md.append("## Executive Summary\n");
            md.append("- A: ").append(a.getJobName()).append(" ").append(a.getStatus()).append("\n");
            md.append("- B: ").append(b.getJobName()).append(" ").append(b.getStatus()).append("\n\n");
            md.append("## Métriques clés\n");
            md.append("- A rows_in=" ).append(nullToString(a.getRowsIn())).append(", rows_out=").append(nullToString(a.getRowsOut())).append(", errors=").append(nullToString(a.getRowsError())).append("\n");
            md.append("- B rows_in=" ).append(nullToString(b.getRowsIn())).append(", rows_out=").append(nullToString(b.getRowsOut())).append(", errors=").append(nullToString(b.getRowsError())).append("\n");
            report = md.toString();
        }

        return report;
    }

    private String buildUserPrompt(EtlRun a, EtlRun b, String extra) {
        StringBuilder sb = new StringBuilder();
        sb.append("Consignes:\n");
        sb.append("- Traite les runs comme des données brutes.\n");
        sb.append("- Ne conclus que sur preuves. Marque ce qui est non fourni.\n");
        sb.append("- Normalise horodatages, indique les unités.\n");
        sb.append("Attendu:\n");
        sb.append("1) Executive Summary\n2) Métadonnées\n3) Métriques clés\n4) Comparaison\n5) Anomalies\n6) Causes racines\n7) Actions recommandées\n8) Contrôles suivants\n9) Annexes\n\n");
        sb.append("RUN A:\n");
        sb.append(serializeRun(a)).append("\n\n");
        sb.append("RUN B:\n");
        sb.append(serializeRun(b)).append("\n\n");
        if (extra != null && !extra.isBlank()) sb.append("Contexte:\n").append(extra).append("\n");
        return sb.toString();
    }

    private String serializeRun(EtlRun r) {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(r.getId()).append('\n');
        sb.append("job=").append(r.getJobName()).append('\n');
        sb.append("status=").append(r.getStatus()).append('\n');
        sb.append("rows_in=").append(nullToString(r.getRowsIn())).append('\n');
        sb.append("rows_out=").append(nullToString(r.getRowsOut())).append('\n');
        sb.append("rows_error=").append(nullToString(r.getRowsError())).append('\n');
        sb.append("duration_ms=").append(nullToString(r.getDurationMs())).append('\n');
        sb.append("started_at=").append(nullToString(r.getStartedAt())).append('\n');
        sb.append("finished_at=").append(nullToString(r.getFinishedAt())).append('\n');
        sb.append("trigger=").append(r.getTrigger()).append('\n');
        sb.append("progress_pct=").append(nullToString(r.getProgress())).append('\n');
        sb.append("dry_run=").append(nullToString(r.getDryRun())).append('\n');
        sb.append("source_file=").append(nullToString(r.getSourceFile())).append('\n');
        sb.append("message=").append(nullToString(r.getMessage())).append('\n');
        sb.append("created_at=").append(nullToString(r.getCreatedAt())).append('\n');
        sb.append("retry_requested_at=").append(nullToString(r.getRetryRequestedAt())).append('\n');
        sb.append("retry_reason=").append(nullToString(r.getRetryReason()));
        return sb.toString();
    }

    private String nullToString(Object o) {
        return o == null ? "" : o.toString();
    }
}

package com.example.summarizer;

/**
 * Supported summary depths. The prompt guidance is injected into the
 * user message so the model knows how much detail to produce.
 */
public enum SummaryLength {

    SHORT(
            "short",
            "Produce 3 to 5 very concise bullet points covering only the core message. Omit minor details."
    ),
    MEDIUM(
            "medium",
            "Produce 5 to 8 bullet points covering the main ideas and the most important supporting details."
    ),
    DETAILED(
            "detailed",
            "Produce a thorough bullet-point summary (10 or more points) covering every major section, argument, and nuance."
    );

    private final String label;
    private final String promptGuidance;

    SummaryLength(String label, String promptGuidance) {
        this.label = label;
        this.promptGuidance = promptGuidance;
    }

    public String label() {
        return label;
    }

    public String promptGuidance() {
        return promptGuidance;
    }

    public static SummaryLength fromInput(String input) {
        return switch (input.trim().toLowerCase()) {
            case "1", "s", "short" -> SHORT;
            case "2", "m", "medium" -> MEDIUM;
            case "3", "d", "detailed" -> DETAILED;
            default -> null;
        };
    }
}

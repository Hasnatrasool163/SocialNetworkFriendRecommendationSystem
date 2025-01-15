package org.example.dsassignment3_4.model;

public enum SuggestionLevel {
    LOW, MEDIUM, HIGH;

    public static SuggestionLevel getLevel(double score) {
        if (score >= 0.7) return HIGH;
        if (score >= 0.5) return MEDIUM;
        return LOW;
    }
}

package dev.cyberjar.neurowatch.ai.domain;


public record PlanStep(
        int order,
        String title,
        String rationale,
        StepType type,
        boolean reversible,
        String expectedOutcome
) {
}

package dev.cyberjar.neurowatch.ai.domain;

public record IncidentAssessment(
        IncidentSignal signal,
        int numberOfLogs,
        RiskLevel riskLevel
) { }

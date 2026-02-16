package dev.cyberjar.neurowatch.ai.domain;

public record AffectedImplant(
        String serialNumber,
        String lotNumber,
        String model,
        String civilianNationalId,
        double anomalyScore
) {}

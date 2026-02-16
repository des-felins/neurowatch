package dev.cyberjar.neurowatch.ai.domain;

import java.util.List;

public record ContainmentPlan(
        List<ContainmentStep> steps,
        boolean requiresApproval,
        EstimatedBlastRadius estimatedBlastRadius
) { }


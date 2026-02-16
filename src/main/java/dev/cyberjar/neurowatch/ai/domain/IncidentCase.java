package dev.cyberjar.neurowatch.ai.domain;

import java.time.Instant;
import java.util.List;

public record IncidentCase(
        String id,
        Instant createdAt,
        IncidentSignal signal,
        IncidentAssessment assessment,
        List<AffectedImplant> affected,
        RootCauseHypothesis hypothesis,
        ContainmentPlan plan
) { }

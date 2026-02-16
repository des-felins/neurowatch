package dev.cyberjar.neurowatch.ai;

import dev.cyberjar.neurowatch.ai.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IncidentTriageService {

    private final IncidentSignalExtractor extractor;
    private final EvidenceBuilder evidenceBuilder;
    private final HypothesisBuilder hypothesisBuilder;
    private final ContainmentPlanBuilder containmentPlanBuilder;

    public IncidentTriageService(IncidentSignalExtractor extractor,
                                 EvidenceBuilder evidenceBuilder,
                                 HypothesisBuilder hypothesisBuilder,
                                 ContainmentPlanBuilder containmentPlanBuilder) {
        this.extractor = extractor;
        this.evidenceBuilder = evidenceBuilder;
        this.hypothesisBuilder = hypothesisBuilder;
        this.containmentPlanBuilder = containmentPlanBuilder;
    }


    public IncidentCase triage(String userText) {

        IncidentSignal signal = extractor.extractIncidentSignal(userText);

        IncidentAssessment assessment = evidenceBuilder.triageIncident(signal);
        List<AffectedImplant> affected = evidenceBuilder.findAffectedImplants(signal);
        EstimatedBlastRadius estimatedBlastRadius = evidenceBuilder.estimateRadius(signal, affected);

        RootCauseHypothesis hypothesis = hypothesisBuilder.makeRootCauseHypothesis(signal, assessment, affected);

        ContainmentPlan containmentPlan = containmentPlanBuilder.planContainment(assessment, hypothesis, estimatedBlastRadius);

        return new IncidentCase(
                UUID.randomUUID().toString(),
                Instant.now(),
                signal,
                assessment,
                affected,
                hypothesis,
                containmentPlan
        );
    }
}

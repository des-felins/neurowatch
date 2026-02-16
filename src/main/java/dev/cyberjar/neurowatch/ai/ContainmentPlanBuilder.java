package dev.cyberjar.neurowatch.ai;

import dev.cyberjar.neurowatch.ai.domain.ContainmentPlan;
import dev.cyberjar.neurowatch.ai.domain.EstimatedBlastRadius;
import dev.cyberjar.neurowatch.ai.domain.IncidentAssessment;
import dev.cyberjar.neurowatch.ai.domain.RootCauseHypothesis;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

@Component
public class ContainmentPlanBuilder {

    private final ChatClient chatClient;

    private final String CHAT_PROMT = """
                    You are an incident triage assistant for implant monitoring logs.
                    Create a containment plan based on the incident assessment, hypothesis and blast radius.
                    You will receive:
                    1) Risk level;
                    2) Root cause hypothesis;
                    3) Estimated blast radius.
                    Produce a ContainmentPlan JSON object.
            
                Rules:
                - steps must be a list of objects like: { "text": "..." }
                - 4-8 steps max, short imperative text
            """;

    public ContainmentPlanBuilder(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ContainmentPlan planContainment(
            IncidentAssessment assessment,
            RootCauseHypothesis hypothesis,
            EstimatedBlastRadius blastRadius) {

        var converter = new BeanOutputConverter<>(ContainmentPlan.class);

        String system = CHAT_PROMT + "\n" + converter.getFormat();

        String content = chatClient.prompt()
                .system(system)
                .user(u -> u.text("""
                                Risk level:
                                {riskLevel}
                                
                                Root cause hypothesis:
                                {hypothesis}
                                
                                Estimated blast radius:
                                {blastRadius}
                                """)
                        .param("riskLevel", assessment.riskLevel())
                        .param("hypothesis", hypothesis)
                        .param("blastRadius", blastRadius))
                .call()
                .content();

        assert content != null;
        return converter.convert(content);

    }

}

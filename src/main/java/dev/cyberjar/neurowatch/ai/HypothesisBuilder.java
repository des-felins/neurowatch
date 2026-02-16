package dev.cyberjar.neurowatch.ai;

import dev.cyberjar.neurowatch.ai.domain.AffectedImplant;
import dev.cyberjar.neurowatch.ai.domain.IncidentAssessment;
import dev.cyberjar.neurowatch.ai.domain.IncidentSignal;
import dev.cyberjar.neurowatch.ai.domain.RootCauseHypothesis;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HypothesisBuilder {

    private final ChatClient chatClient;

    private final String CHAT_PROMT = """
                    You are an incident triage assistant for implant monitoring logs.
                    Based on the incident details,
                    choose a root cause hypothesis for the observed anomaly in the implant monitoring logs.
                    You will receive:
                    1) Incident signal;
                    2) Incident assessment;
                    3) A deterministic evidence summary (top affected implants).
                    Rules:
                    - type must be one of: FIRMWARE_REGRESSION, BAD_LOT, ATTACK_PATTERN, ENVIRONMENTAL
                    - confidence is 0..1
                    - evidence is a short bullet list of specific signals from the inputs
            """;

    public HypothesisBuilder(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    public RootCauseHypothesis makeRootCauseHypothesis(IncidentSignal signal,
                                                       IncidentAssessment assessment,
                                                       List<AffectedImplant> affectedImplants) {

        var converter = new BeanOutputConverter<>(RootCauseHypothesis.class);
        String system = CHAT_PROMT + "\n" + converter.getFormat();

        String content = chatClient.prompt()
                .system(system)
                .user(u -> u.text("""
                                Incident signal:
                                {signal}
                                
                                Incident assessment:
                                {assessment}
                                
                                Top affected implants:
                                {affectedImplants}
                                """)
                        .param("signal", signal)
                        .param("assessment", assessment)
                        .param("evidence", affectedImplants.stream().limit(10).toList()))
                .call()
                .content();

        assert content != null;
        return converter.convert(content);
    }


}

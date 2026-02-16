package dev.cyberjar.neurowatch.ai;

import dev.cyberjar.neurowatch.ai.domain.IncidentSignal;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

@Component
public class IncidentSignalExtractor {

    private final ChatClient chatClient;

    private final String CHAT_PROMT = """
            Extract an IncidentSignal from the user's message.
            
            Output rules:
            - lon is a number in [-180, 180]
            - lat is a number in [-90, 90]
            - radiusMeters is a number in meters. If missing, leave null.
            - from/to are ISO-8601 LocalDateTime (e.g. 2026-02-02T02:00:00)
            - metric is one of: neuralLatencyMs, cpuUsagePct, powerUsageUw
            - threshold is a finite number
            
             Hard rules:
                - Do not invent addresses or geocoding results.
                - If the user specifies a city/area without coordinates, leave longitude/latitude null.
                - Return ONLY valid JSON.
           
            User message:
            %s
            """;

    public IncidentSignalExtractor(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    public IncidentSignal extractIncidentSignal(String userText) {

        var converter = new BeanOutputConverter<>(IncidentSignal.class);
        String system = CHAT_PROMT + "\n" + converter.getFormat();

        String content = chatClient.prompt()
                .system(system)
                .user(userText)
                .call()
                .content();

        assert content != null;
        return converter.convert(content);
    }
}

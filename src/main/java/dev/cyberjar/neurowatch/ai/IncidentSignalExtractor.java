package dev.cyberjar.neurowatch.ai;

import dev.cyberjar.neurowatch.ai.domain.IncidentSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class IncidentSignalExtractor {

    private final ChatClient chatClient;
    private final Clock clock;
    private static final ZoneId USER_ZONE = ZoneId.of("Europe/Amsterdam");

    private static final Logger logger = LoggerFactory.getLogger(IncidentSignalExtractor.class);


    private final String CHAT_PROMPT = """
        You extract an IncidentSignal from the user's message.

        IMPORTANT: Resolve relative time expressions (e.g. "yesterday", "last 36 hours")
        relative to this reference time:
        now = %s (timezone = %s)

        Output rules:
        - lon is a number in [-180, 180]
        - lat is a number in [-90, 90]
        - radiusMeters is a number in meters. If missing, leave null.
        - from/to MUST be ISO-8601 UTC Instants ending with 'Z' (e.g. 2026-02-16T12:05:28Z)
        - metric is one of: neuralLatencyMs, cpuUsagePct, powerUsageUw
        - threshold is a finite number

        Hard rules:
        - Do not invent addresses or geocoding results.
        - If the user specifies a city/area without coordinates, leave longitude/latitude null.
        - Return ONLY valid JSON.
        """;

    public IncidentSignalExtractor(ChatClient chatClient, Clock clock) {
        this.chatClient = chatClient;
        this.clock = clock;
    }


    public IncidentSignal extractIncidentSignal(String userText) {

        var converter = new BeanOutputConverter<>(IncidentSignal.class);

        Instant nowUtc = clock.instant();
        ZonedDateTime nowLocal = nowUtc.atZone(USER_ZONE);

        String system = (CHAT_PROMPT.formatted(nowUtc.toString(), nowLocal.toString())
                + "\n" + converter.getFormat());

        String content = chatClient.prompt()
                .system(system)
                .user(userText)
                .call()
                .content();

        assert content != null;
        logger.debug("Content parsed: {}", content);
        return converter.convert(content);
    }

}

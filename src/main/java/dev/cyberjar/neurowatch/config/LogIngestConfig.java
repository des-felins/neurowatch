package dev.cyberjar.neurowatch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cyberjar.neurowatch.entity.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.repository.implantmonitoring.ImplantMonitoringLogRepository;
import dev.cyberjar.neurowatch.service.LiveLogBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.function.Consumer;

@Configuration
public class LogIngestConfig {

    private static final Logger logger = LoggerFactory.getLogger(LogIngestConfig.class);


    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules(); // JSR-310
    }

    @Bean
    public Consumer<byte[]> logConsumer(
            ObjectMapper mapper,
            ImplantMonitoringLogRepository repo,
            LiveLogBus bus) {
        return bytes -> {
            try {
                ImplantMonitoringLog log = mapper.readValue(bytes, ImplantMonitoringLog.class);
                repo.save(log);
                bus.publish(log);
            } catch (Exception e) {
                logger.debug("Failed to parse incoming log -> {}", e.getMessage());
            }
        };
    }

}

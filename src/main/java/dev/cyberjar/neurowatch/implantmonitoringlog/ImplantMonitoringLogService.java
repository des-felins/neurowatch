package dev.cyberjar.neurowatch.implantmonitoringlog;

import dev.cyberjar.neurowatch.civilian.Civilian;
import dev.cyberjar.neurowatch.civilian.CivilianService;
import dev.cyberjar.neurowatch.implantmonitoringlog.dto.MonitoringStats;
import dev.cyberjar.neurowatch.implantmonitoringlog.dto.SeedLog;
import dev.cyberjar.neurowatch.implantmonitoringlog.dto.SeedLogsData;
import dev.cyberjar.neurowatch.implantmonitoringlog.repository.ImplantMonitoringLogRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImplantMonitoringLogService {

    private final ImplantMonitoringLogRepository implantMonitoringLogRepository;
    private final CivilianService civilianService;

    public ImplantMonitoringLogService(ImplantMonitoringLogRepository implantMonitoringLogRepository, CivilianService civilianService) {
        this.implantMonitoringLogRepository = implantMonitoringLogRepository;
        this.civilianService = civilianService;
    }

    public List<ImplantMonitoringLog> findAllByImplantSerialNumber(String serialNumber) {
        return implantMonitoringLogRepository.findByImplantSerialNumber(serialNumber);
    }

    public List<ImplantMonitoringLog> findByImplantSerialNumberAndAfter(String serialNumber, Instant timestamp) {
        return implantMonitoringLogRepository.findByImplantSerialNumberAndTimestampAfter(serialNumber, timestamp);
    }

    public MonitoringStats aggregateStatsForImplantForPeriod(String serialNumber,
                                                             Instant from, Instant to) {
        return implantMonitoringLogRepository.aggregateStats(serialNumber, from, to);
    }

    public Map<String, List<ImplantMonitoringLog>> findLogsByAreaAndTime(
            GeoJsonPoint center,
            double maxDistanceMeters,
            Instant from,
            Instant to) {

        return implantMonitoringLogRepository.findLogsByAreaAndTimeGrouped(
                center,
                maxDistanceMeters,
                from,
                to);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ImportResult importLogsForCivilian(String implantSerialNumber, InputStream yamlStream) {

        Civilian civilian = civilianService.findCivilianByImplantSerialNumber(implantSerialNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Civilian not found for implant serial number: " + implantSerialNumber));


        // CAUTION! Intentionally unsafe for CVE demo purposes (SnakeYAML Constructor)
        Yaml yaml = new Yaml(new Constructor(SeedLogsData.class));
        SeedLogsData seed = yaml.load(yamlStream);

        if (seed == null || seed.getLogs() == null || seed.getLogs().isEmpty()) {
            return new ImportResult(civilian.getId(), 0);
        }

        List<ImplantMonitoringLog> logs = new ArrayList<>();
        for (SeedLog sl : seed.getLogs()) {
            Instant ts = (sl.getTimestamp() == null || sl.getTimestamp().isBlank())
                    ? Instant.now()
                    : Instant.parse(sl.getTimestamp());

            GeoJsonPoint location = null;
            if (sl.getLocation() != null) {
                location = new GeoJsonPoint(sl.getLocation().getLongitude(), sl.getLocation().getLatitude());
            }

            logs.add(new ImplantMonitoringLog(
                    null,
                    sl.getImplantSerialNumber(),
                    civilian.getNationalId(),
                    ts,
                    sl.getPowerUsageUw(),
                    sl.getCpuUsagePct(),
                    sl.getNeuralLatencyMs(),
                    location
            ));
        }

        implantMonitoringLogRepository.saveAll(logs);

        return new ImportResult(civilian.getId(), logs.size());
    }

    public record ImportResult(String civilianId, int logsImported) {
    }

}

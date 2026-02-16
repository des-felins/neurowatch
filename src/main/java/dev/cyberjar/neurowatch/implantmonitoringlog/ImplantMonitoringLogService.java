package dev.cyberjar.neurowatch.implantmonitoringlog;

import dev.cyberjar.neurowatch.implantmonitoringlog.repository.ImplantMonitoringLogRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ImplantMonitoringLogService {

    private final ImplantMonitoringLogRepository implantMonitoringLogRepository;

    public ImplantMonitoringLogService(ImplantMonitoringLogRepository implantMonitoringLogRepository) {
        this.implantMonitoringLogRepository = implantMonitoringLogRepository;
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
}

package dev.cyberjar.neurowatch.implantmonitoringlog;

import dev.cyberjar.neurowatch.implantmonitoringlog.repository.ImplantMonitoringLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImplantMonitoringLogService {

    private final ImplantMonitoringLogRepository implantMonitoringLogRepository;

    public ImplantMonitoringLogService(ImplantMonitoringLogRepository implantMonitoringLogRepository) {
        this.implantMonitoringLogRepository = implantMonitoringLogRepository;
    }

    public List<ImplantMonitoringLog> findAllByImplantSerialNumber(String serialNumber) {
        return implantMonitoringLogRepository.findByImplantSerialNumber(serialNumber);
    }

    public List<ImplantMonitoringLog> findByImplantSerialNumberAndAfter(String serialNumber, LocalDateTime timestamp) {
        return implantMonitoringLogRepository.findByImplantSerialNumberAndTimestampAfter(serialNumber, timestamp);
    }

    public MonitoringStats aggregateStatsForImplantForPeriod(String serialNumber,
                                                             LocalDateTime from, LocalDateTime to) {
        return implantMonitoringLogRepository.aggregateStats(serialNumber, from, to);
    }

}

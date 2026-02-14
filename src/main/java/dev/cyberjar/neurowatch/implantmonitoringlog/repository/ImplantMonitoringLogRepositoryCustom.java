package dev.cyberjar.neurowatch.implantmonitoringlog.repository;

import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.implantmonitoringlog.MonitoringStats;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ImplantMonitoringLogRepositoryCustom {

    MonitoringStats aggregateStats(String serialNumber, LocalDateTime from, LocalDateTime to);

    Map<String, List<ImplantMonitoringLog>> findLogsByAreaAndTimeGrouped(
            Point center, double maxDistanceMeters, LocalDateTime from, LocalDateTime to);

}
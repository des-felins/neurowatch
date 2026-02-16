package dev.cyberjar.neurowatch.implantmonitoringlog.repository;

import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.implantmonitoringlog.MonitoringStats;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ImplantMonitoringLogRepositoryCustom {

    MonitoringStats aggregateStats(String serialNumber, Instant from, Instant to);

    Map<String, List<ImplantMonitoringLog>> findLogsByAreaAndTimeGrouped(
            GeoJsonPoint center, double maxDistanceMeters, Instant from, Instant to);

}
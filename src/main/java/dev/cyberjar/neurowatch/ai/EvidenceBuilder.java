package dev.cyberjar.neurowatch.ai;

import dev.cyberjar.neurowatch.ai.domain.*;
import dev.cyberjar.neurowatch.civilian.Civilian;
import dev.cyberjar.neurowatch.civilian.CivilianService;
import dev.cyberjar.neurowatch.civilian.Implant;
import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLogService;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class EvidenceBuilder {

    private final ImplantMonitoringLogService logService;
    private final CivilianService civilianService;


    public EvidenceBuilder(ImplantMonitoringLogService logService, CivilianService civilianService) {
        this.logService = logService;
        this.civilianService = civilianService;
    }


    public IncidentAssessment triageIncident(IncidentSignal signal) {
        Map<String, List<ImplantMonitoringLog>> logs = extractLogs(signal);

        RiskLevel risk = classifyRisk(logs, signal);

        return new IncidentAssessment(signal, logs.size(), risk);
    }


    public List<AffectedImplant> findAffectedImplants(IncidentSignal signal) {

        Map<String, List<ImplantMonitoringLog>> logs = extractLogs(signal);

        return logs.entrySet().stream()
                .map(entry -> toAffectedImplant(entry.getKey(), entry.getValue(), signal))
                .sorted(Comparator.comparingDouble(AffectedImplant::anomalyScore).reversed())
                .toList();
    }

    public EstimatedBlastRadius estimateRadius(IncidentSignal signal,
                                               List<AffectedImplant> affectedImplants) {

        if (affectedImplants == null) affectedImplants = List.of();

        int affectedEstimate = affectedImplants.size();

        List<String> affectedLots = affectedImplants.stream()
                .map(AffectedImplant::lotNumber)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .limit(5)
                .toList();

        List<String> affectedModels = affectedImplants.stream()
                .map(AffectedImplant::model)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .limit(5)
                .toList();

        String geoSummary = "Within %.0fm of (%.5f, %.5f)"
                .formatted(signal.radiusMeters(), signal.latitude(), signal.longitude());

        String timeSummary = "From %s to %s".formatted(signal.from(), signal.to());

        return new EstimatedBlastRadius(
                affectedEstimate,
                affectedLots,
                affectedModels,
                geoSummary,
                timeSummary
        );
    }


    private Map<String, List<ImplantMonitoringLog>> extractLogs(IncidentSignal signal) {
        return logService.findLogsByAreaAndTime(
                toSpringPoint(signal.longitude(), signal.latitude()),
                signal.radiusMeters(),
                signal.from(),
                signal.to());
    }

    private static Point toSpringPoint(double lon, double lat) {
        return new Point(lon, lat);
    }

    private AffectedImplant toAffectedImplant(
            String serialNumber,
            List<ImplantMonitoringLog> logsPerImplant,
            IncidentSignal signal) {


        if (logsPerImplant == null || logsPerImplant.isEmpty()) {
            // No logs means no evidence; score 0, rest unknown.
            return new AffectedImplant(
                    serialNumber,
                    null,
                    null,
                    null,
                    0.0);
        }

        double anomalyScore = calculateAnomalyScore(logsPerImplant, signal);

        Optional<Civilian> civilian = civilianService.findCivilianByImplantSerialNumber(serialNumber);
        if (civilian.isEmpty()) {
            throw new RuntimeException("No civilian found for implant serial number " + serialNumber);
        }

        Civilian c = civilian.get();
        String civilianNationalId = c.getNationalId();

        Implant implant = c.getImplants()
                .stream()
                .filter(i -> serialNumber.equals(i.getSerialNumber()))
                .findFirst()
                .orElseThrow();


        String lotNumber = String.valueOf(implant.getLotNumber());
        String model = implant.getModel();

        return new AffectedImplant(
                serialNumber,
                lotNumber,
                model,
                civilianNationalId,
                anomalyScore);

    }

    private double calculateAnomalyScore(List<ImplantMonitoringLog> logsPerImplant, IncidentSignal signal) {

        double threshold = signal.threshold();
        if (threshold <= 0.0) return 0.0;

        double max = logsPerImplant.stream()
                .mapToDouble(l -> getMetricValue(l, signal.metric()))
                .max()
                .orElse(0.0);

        if (max <= threshold) return 0.0;

        double score = (max - threshold) / threshold; // exceed ratio
        return Math.min(1.0, score);
    }

    private static double getMetricValue(ImplantMonitoringLog log, String metric) {
        return switch (metric) {
            case "neuralLatencyMs" -> log.getNeuralLatencyMs();
            case "cpuUsagePct" -> log.getCpuUsagePct();
            case "powerUsageUw" -> log.getPowerUsageUw();
            default -> 0.0;
        };
    }

    private static RiskLevel classifyRisk(
            Map<String, List<ImplantMonitoringLog>> logs,
            IncidentSignal signal) {

        if (logs.isEmpty()) return RiskLevel.LOW;

        long distinctImplants = logs.size();

        long exceedCount = logs.values()
                .stream()
                .flatMap(List::stream)
                .mapToDouble(log -> getMetricValue(log, signal.metric()))
                .filter(value -> value >= signal.threshold())
                .count();


        if (exceedCount >= 60 && distinctImplants >= 5) return RiskLevel.CRITICAL;
        if (exceedCount >= 30 && distinctImplants >= 3) return RiskLevel.HIGH;
        if (exceedCount >= 10) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

}

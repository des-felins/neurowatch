package dev.cyberjar.neurowatch.repository.implantmonitoring;

import dev.cyberjar.neurowatch.entity.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.entity.MonitoringStats;
import org.bson.Document;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ImplantMonitoringLogRepositoryCustomImpl implements ImplantMonitoringLogRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ImplantMonitoringLogRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MonitoringStats aggregateStats(String serialNumber, LocalDateTime from, LocalDateTime to) {
        MatchOperation match = Aggregation.match(Criteria.where("implantSerialNumber").is(serialNumber)
                .and("timestamp").gte(from).lte(to));

        GroupOperation group = Aggregation.group("implantSerialNumber")
                .avg("powerUsageUw").as("avgPowerUsageUw")
                .avg("cpuUsagePct").as("avgCpuUsagePct")
                .avg("neuralLatencyMs").as("avgNeuralLatencyMs");

        ProjectionOperation project = Aggregation.project()
                .and("_id").as("implantSerialNumber")
                .and(ArithmeticOperators.Round.roundValueOf("avgPowerUsageUw").place(2)).as("avgPowerUsageUw")
                .and(ArithmeticOperators.Round.roundValueOf("avgCpuUsagePct").place(2)).as("avgCpuUsagePct")
                .and(ArithmeticOperators.Round.roundValueOf("avgNeuralLatencyMs").place(2)).as("avgNeuralLatencyMs");

        Aggregation aggregation = Aggregation.newAggregation(match, group, project);

        AggregationResults<MonitoringStats> results = mongoTemplate.aggregate(
                aggregation, "implant_logs", MonitoringStats.class);

        return results.getUniqueMappedResult();
    }

    @Override
    public Map<String, List<ImplantMonitoringLog>> findLogsByAreaAndTimeGrouped(Point center,
                                                                                double maxDistanceMeters,
                                                                                LocalDateTime from,
                                                                                LocalDateTime to) {
        MatchOperation match = Aggregation.match(
                Criteria.where("location").nearSphere(center)
                        .maxDistance(maxDistanceMeters)
                        .and("timestamp").gte(from).lte(to));

        GroupOperation group = Aggregation.group("implantSerialNumber")
                .push(Aggregation.ROOT).as("logs");

        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, "implant_logs", Document.class);

        Map<String, List<ImplantMonitoringLog>> grouped = new HashMap<>();

        for (Document doc : results.getMappedResults()) {
            String serialNumber = doc.getString("_id");
            List<Document> logsDocs = (List<Document>) doc.get("logs");

            List<ImplantMonitoringLog> logs = logsDocs.stream()
                    .map(d -> mongoTemplate.getConverter()
                            .read(ImplantMonitoringLog.class, d))
                    .toList();

            grouped.put(serialNumber, logs);
        }

        return grouped;
    }
}

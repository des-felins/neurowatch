package dev.cyberjar.neurowatch.implantmonitoringlog;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

@Document(collection = "implant_logs")
@CompoundIndex(name = "implant_ts_idx",
        def = "{'implant_serial_number': 1, 'timestamp': -1}")
public class ImplantMonitoringLog {

    @Id
    private String id;
    private String civilianNationalId;
    private String implantSerialNumber;
    @CreatedDate
    private Instant timestamp;
    private double powerUsageUw;
    private double cpuUsagePct;
    private double neuralLatencyMs;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    public ImplantMonitoringLog(String id,
                                String implantSerialNumber,
                                String civilianNationalId,
                                double powerUsageUw,
                                double cpuUsagePct,
                                double neuralLatencyMs,
                                GeoJsonPoint location) {
        this.id = id;
        this.implantSerialNumber = implantSerialNumber;
        this.civilianNationalId = civilianNationalId;
        this.powerUsageUw = powerUsageUw;
        this.cpuUsagePct = cpuUsagePct;
        this.neuralLatencyMs = neuralLatencyMs;
        timestamp = Instant.now();
        this.location = location;
    }

    public ImplantMonitoringLog(String id,
                                String implantSerialNumber,
                                String civilianNationalId,
                                Instant timestamp,
                                double powerUsageUw,
                                double cpuUsagePct,
                                double neuralLatencyMs,
                                GeoJsonPoint location) {
        this.id = id;
        this.implantSerialNumber = implantSerialNumber;
        this.civilianNationalId = civilianNationalId;
        this.timestamp = timestamp;
        this.powerUsageUw = powerUsageUw;
        this.cpuUsagePct = cpuUsagePct;
        this.neuralLatencyMs = neuralLatencyMs;
        this.location = location;
    }


    public ImplantMonitoringLog() {
        timestamp = Instant.now();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getNeuralLatencyMs() {
        return neuralLatencyMs;
    }

    public void setNeuralLatencyMs(double neuralLatencyMs) {
        this.neuralLatencyMs = neuralLatencyMs;
    }

    public double getCpuUsagePct() {
        return cpuUsagePct;
    }

    public void setCpuUsagePct(double cpuUsagePct) {
        this.cpuUsagePct = cpuUsagePct;
    }

    public double getPowerUsageUw() {
        return powerUsageUw;
    }

    public void setPowerUsageUw(double powerUsageUw) {
        this.powerUsageUw = powerUsageUw;
    }

    public String getCivilianNationalId() {
        return civilianNationalId;
    }

    public void setCivilianNationalId(String civilianNationalId) {
        this.civilianNationalId = civilianNationalId;
    }

    public String getImplantSerialNumber() {
        return implantSerialNumber;
    }

    public void setImplantSerialNumber(String implantSerialNumber) {
        this.implantSerialNumber = implantSerialNumber;
    }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImplantMonitoringLog that = (ImplantMonitoringLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImplantMonitoringLog{" +
                "id='" + id + '\'' +
                ", civilianNationalId=" + civilianNationalId +
                ", implantSerialNumber='" + implantSerialNumber + '\'' +
                ", timestamp=" + timestamp +
                ", powerUsageUw=" + powerUsageUw +
                ", cpuUsagePct=" + cpuUsagePct +
                ", neuralLatencyMs=" + neuralLatencyMs +
                '}';
    }
}

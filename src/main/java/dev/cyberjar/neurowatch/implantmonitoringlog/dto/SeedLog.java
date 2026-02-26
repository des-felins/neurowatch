package dev.cyberjar.neurowatch.implantmonitoringlog.dto;

public class SeedLog {
    private String implantSerialNumber;
    private String timestamp;
    private double powerUsageUw;
    private double cpuUsagePct;
    private double neuralLatencyMs;
    private SeedLocation location;

    public String getImplantSerialNumber() {
        return implantSerialNumber;
    }

    public void setImplantSerialNumber(String implantSerialNumber) {
        this.implantSerialNumber = implantSerialNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getPowerUsageUw() {
        return powerUsageUw;
    }

    public void setPowerUsageUw(double powerUsageUw) {
        this.powerUsageUw = powerUsageUw;
    }

    public double getCpuUsagePct() {
        return cpuUsagePct;
    }

    public void setCpuUsagePct(double cpuUsagePct) {
        this.cpuUsagePct = cpuUsagePct;
    }

    public double getNeuralLatencyMs() {
        return neuralLatencyMs;
    }

    public void setNeuralLatencyMs(double neuralLatencyMs) {
        this.neuralLatencyMs = neuralLatencyMs;
    }

    public SeedLocation getLocation() {
        return location;
    }

    public void setLocation(SeedLocation location) {
        this.location = location;
    }
}

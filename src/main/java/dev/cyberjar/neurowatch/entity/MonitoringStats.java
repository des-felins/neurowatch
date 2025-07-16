package dev.cyberjar.neurowatch.entity;

public record MonitoringStats(String implantSerialNumber,
                              double avgPowerUsageUw,
                              double avgCpuUsagePct,
                              double avgNeuralLatencyMs) {

}

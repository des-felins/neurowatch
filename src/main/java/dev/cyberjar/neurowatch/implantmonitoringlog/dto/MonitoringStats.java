package dev.cyberjar.neurowatch.implantmonitoringlog.dto;

public record MonitoringStats(String implantSerialNumber,
                              double avgPowerUsageUw,
                              double avgCpuUsagePct,
                              double avgNeuralLatencyMs) {

}

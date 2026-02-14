package dev.cyberjar.neurowatch.implantmonitoringlog;

public record MonitoringStats(String implantSerialNumber,
                              double avgPowerUsageUw,
                              double avgCpuUsagePct,
                              double avgNeuralLatencyMs) {

}

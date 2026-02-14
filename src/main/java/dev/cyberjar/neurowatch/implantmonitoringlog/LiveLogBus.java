package dev.cyberjar.neurowatch.implantmonitoringlog;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class LiveLogBus {

    private final Sinks.Many<ImplantMonitoringLog> sink =
            Sinks.many().multicast().onBackpressureBuffer(2048, false);

    public void publish(ImplantMonitoringLog log) {
        sink.tryEmitNext(log);
    }

    public Flux<ImplantMonitoringLog> stream() {
        return sink.asFlux();
    }

}

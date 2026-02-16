package dev.cyberjar.neurowatch.ai.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record IncidentSignal(
        @NotNull double longitude,
        @NotNull double latitude,
        @Positive double radiusMeters,
        @NotNull @Past Instant from,
        @NotNull @Past Instant to,
        @NotNull @NotEmpty String metric,
        @Positive double threshold
) {
}

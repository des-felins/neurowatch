package dev.cyberjar.neurowatch.ai.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record IncidentSignal(
        @NotNull double longitude,
        @NotNull double latitude,
        @Positive double radiusMeters,
        @NotNull @Past LocalDateTime from,
        @NotNull @Past LocalDateTime to,
        @NotNull @NotEmpty String metric,
        @Positive double threshold
) { }

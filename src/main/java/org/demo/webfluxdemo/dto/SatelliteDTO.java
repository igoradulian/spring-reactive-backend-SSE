package org.demo.webfluxdemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Igor Adulyan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Single satellite telemetry record")
public record SatelliteDTO(
        @Schema(description = "Satellite identifier", example = "23")
        int id,
        @Schema(description = "Azimuth in degrees", example = "6.78")
        double azimuth,
        @Schema(description = "Elevation in degrees", example = "63.73")
        double elevation,
        @Schema(description = "Signal-to-noise ratio", example = "24.0")
        double signal,
        @Schema(description = "Whether this satellite is used for position fix", example = "true")
        boolean used,
        @Schema(description = "Whether signal is fading", example = "false")
        boolean fading) {
}

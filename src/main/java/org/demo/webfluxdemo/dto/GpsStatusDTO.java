package org.demo.webfluxdemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author Igor Adulyan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Aggregated GPS status from receiver")
public record GpsStatusDTO(
        @Schema(description = "Number of visible satellites", example = "12")
        int visibleCount,
        @Schema(description = "Number of satellites used for positioning", example = "4")
        int usedCount,
        @Schema(description = "Horizontal dilution of precision", example = "1.9")
        double hdop,
        @ArraySchema(schema = @Schema(implementation = SatelliteDTO.class))
        List<SatelliteDTO> satellites) {
}

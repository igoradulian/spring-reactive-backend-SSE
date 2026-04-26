package org.demo.webfluxdemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author Igor Adulyan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GpsStatusDTO(
        int visibleCount,
        int usedCount,
        double hdop,
        List<SatelliteDTO> satellites) {
}

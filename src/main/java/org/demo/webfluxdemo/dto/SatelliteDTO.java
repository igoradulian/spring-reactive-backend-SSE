package org.demo.webfluxdemo.dto;

/**
 * @author Igor Adulyan
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SatelliteDTO(int id,
                           double azimuth,
                           double elevation,
                           double signal,
                           boolean used,
                           boolean fading) {
}

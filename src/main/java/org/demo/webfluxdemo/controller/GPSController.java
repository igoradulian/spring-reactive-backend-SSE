package org.demo.webfluxdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.demo.webfluxdemo.dto.GpsStatusDTO;
import org.demo.webfluxdemo.service.HiveMQSubscriberService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author Igor Adulyan
 */
@RestController
@RequestMapping("/api/gps/data")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "GPS Data", description = "Endpoints for live GPS satellite status")
public class GPSController {

    private final HiveMQSubscriberService subscriberService;


    public GPSController(HiveMQSubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Operation(
            summary = "Subscribe to live GPS updates",
            description = "Streams live GPS status as server-sent events from MQTT updates",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SSE stream started",
                            content = @Content(
                                    mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                                    schema = @Schema(implementation = GpsStatusDTO.class)
                            )
                    )
            }
    )
    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<GpsStatusDTO>> subscribeToGPSData() {
        return subscriberService.getMessages()
                .map(gpsStatus -> ServerSentEvent.<GpsStatusDTO>builder()
                        .event("sky-update")
                        .data(gpsStatus)
                        .build());
    }

}

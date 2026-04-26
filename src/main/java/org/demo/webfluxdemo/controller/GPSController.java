package org.demo.webfluxdemo.controller;

import org.demo.webfluxdemo.dto.GpsStatusDTO;
import org.demo.webfluxdemo.service.HiveMQSubscriberService;
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
public class GPSController {

    private final HiveMQSubscriberService subscriberService;


    public GPSController(HiveMQSubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping(path = "/subscribe", produces = "text/event-stream")
    public Flux<ServerSentEvent<GpsStatusDTO>> subscribeToGPSData() {
        return subscriberService.getMessages()
                .map(gpsStatus -> ServerSentEvent.<GpsStatusDTO>builder()
                        .event("sky-update")
                        .data(gpsStatus)
                        .build());
    }

}

package org.demo.webfluxdemo.controller;

import org.demo.webfluxdemo.dto.GpsStatusDTO;
import org.demo.webfluxdemo.dto.SatelliteDTO;
import org.demo.webfluxdemo.service.HiveMQSubscriberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(GPSController.class)
class GPSControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private HiveMQSubscriberService subscriberService;

    @Test
    void subscribeToGpsDataReturnsSseStreamWithMappedEventAndPayload() {
        GpsStatusDTO first = new GpsStatusDTO(
                12,
                4,
                1.9,
                List.of(
                        new SatelliteDTO(23, 6.7899, 63.7387, 0.0, false, false),
                        new SatelliteDTO(10, 283.0730, 57.9709, 24.0, true, false)
                )
        );
        GpsStatusDTO second = new GpsStatusDTO(
                8,
                5,
                1.2,
                List.of(new SatelliteDTO(8, 320.0421, 11.1638, 25.0, true, false))
        );

        when(subscriberService.getMessages()).thenReturn(Flux.just(first, second));

        Flux<ServerSentEvent<GpsStatusDTO>> eventFlux = webTestClient.get()
                .uri("/gps/data/subscribe")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<GpsStatusDTO>>() {})
                .getResponseBody();

        StepVerifier.create(eventFlux.take(2))
                .assertNext(event -> {
                    GpsStatusDTO payload = event.data();
                    org.junit.jupiter.api.Assertions.assertEquals("gps-update", event.event());
                    org.junit.jupiter.api.Assertions.assertNotNull(payload);
                    org.junit.jupiter.api.Assertions.assertEquals(12, payload.visibleCount());
                    org.junit.jupiter.api.Assertions.assertEquals(4, payload.usedCount());
                    org.junit.jupiter.api.Assertions.assertEquals(1.9, payload.hdop());
                    org.junit.jupiter.api.Assertions.assertEquals(2, payload.satellites().size());
                })
                .assertNext(event -> {
                    GpsStatusDTO payload = event.data();
                    org.junit.jupiter.api.Assertions.assertEquals("gps-update", event.event());
                    org.junit.jupiter.api.Assertions.assertNotNull(payload);
                    org.junit.jupiter.api.Assertions.assertEquals(8, payload.visibleCount());
                    org.junit.jupiter.api.Assertions.assertEquals(5, payload.usedCount());
                })
                .verifyComplete();
    }

    @Test
    void subscribeToGpsDataReturnsEmptySseStreamWhenNoMessagesAvailable() {
        when(subscriberService.getMessages()).thenReturn(Flux.empty());

        Flux<ServerSentEvent<GpsStatusDTO>> eventFlux = webTestClient.get()
                .uri("/gps/data/subscribe")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<GpsStatusDTO>>() {})
                .getResponseBody();

        StepVerifier.create(eventFlux)
                .verifyComplete();
    }
}

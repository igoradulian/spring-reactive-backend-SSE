package org.demo.webfluxdemo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.demo.webfluxdemo.config.MqttConfig;
import org.demo.webfluxdemo.dto.GpsStatusDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import tools.jackson.databind.ObjectMapper;

/**
 * @author Igor Adulyan
 */
@Service
@Slf4j
public class HiveMQSubscriberService {

    private final MqttConfig mqttConfig;
    private final Sinks.Many<GpsStatusDTO> sink = Sinks.many().replay().latest();
    private final ObjectMapper objectMapper;

    public HiveMQSubscriberService(MqttConfig mqttConfig, ObjectMapper objectMapper) {
        this.mqttConfig = mqttConfig;
        this.objectMapper = objectMapper;
    }

    public Flux<GpsStatusDTO> getMessages() {
        return sink.asFlux();
    }

    @PostConstruct
    public void start() {
        subscribe();
    }

    public void subscribe() {
        mqttConfig.mqtt5AsyncClient()
                .connect()
                .whenComplete(
                        (connAck, throwable) -> {
                            if (throwable != null) {
                                log.warn("Failed to connect to MQTT broker: {}", throwable.getMessage());
                            } else {
                                log.warn("Connected to MQTT broker successfully");
                            }})
                .thenCompose(connAck ->
                        mqttConfig.mqtt5AsyncClient()
                                .subscribeWith()
                                .topicFilter("gps/satellites")
                                .callback(publish -> {
                                 try {
                                    byte[] payload = publish.getPayloadAsBytes();
                                    GpsStatusDTO gpsStatus = objectMapper.readValue(payload, GpsStatusDTO.class);
                                    Sinks.EmitResult result = sink.tryEmitNext(gpsStatus);
                                    if (result.isFailure()) {
                                        log.warn("Sink emit failed: {}",result);
                                    }
                                }
                                 catch (Exception e) {
                                     log.warn("Failed to parse MQTT payload: {}", e.getMessage());
                                 }
                                })
                                .send()
                )
                .thenAccept(subAck -> log.warn("Subscribed successfully"));}
}

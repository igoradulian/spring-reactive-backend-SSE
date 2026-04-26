package org.demo.webfluxdemo.service;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import org.demo.webfluxdemo.config.MqttConfig;
import org.demo.webfluxdemo.dto.GpsStatusDTO;
import org.demo.webfluxdemo.dto.SatelliteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HiveMQSubscriberServiceTest {

    @Mock private MqttConfig mqttConfig;

    @Mock private ObjectMapper objectMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Mqtt5AsyncClient mqtt5AsyncClient;

    private HiveMQSubscriberService service;

    @BeforeEach void setUp() {
        service = new HiveMQSubscriberService(mqttConfig, objectMapper);
        when(mqttConfig.mqtt5AsyncClient()).thenReturn(mqtt5AsyncClient);
    }

    @Test void subscribeEmitsParsedGpsMessageToFlux() {
        Mqtt5ConnAck connAck = mock(Mqtt5ConnAck.class);
        Mqtt5SubAck subAck = mock(Mqtt5SubAck.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<Mqtt5Publish>> callbackCaptor = ArgumentCaptor.forClass(Consumer.class);

        when(mqtt5AsyncClient.connect()).thenReturn(CompletableFuture.completedFuture(connAck));
        when(mqtt5AsyncClient.subscribeWith()
                .topicFilter(anyString())
                .callback(callbackCaptor.capture())
                .send()).thenReturn(CompletableFuture.completedFuture(subAck));

        GpsStatusDTO dto = new GpsStatusDTO(12, 4, 1.9,
                List.of(new SatelliteDTO(23, 6.7, 63.7, 0.0, false, false)));

        when(objectMapper.readValue(any(byte[].class), eq(GpsStatusDTO.class))).thenReturn(dto);

        service.subscribe();

        Mqtt5Publish publish = mock(Mqtt5Publish.class);
        when(publish.getPayloadAsBytes()).thenReturn("{\"visibleCount\":12}".getBytes(StandardCharsets.UTF_8));

        StepVerifier.create(service.getMessages())
                .then(() -> callbackCaptor.getValue().accept(publish))
                .assertNext(emitted -> assertEquals(dto, emitted))
                .thenCancel()
                .verify();
    }

    @Test void subscribeSkipsEmissionWhenPayloadParsingFails() {
        Mqtt5ConnAck connAck = mock(Mqtt5ConnAck.class);
        Mqtt5SubAck subAck = mock(Mqtt5SubAck.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<Mqtt5Publish>> callbackCaptor = ArgumentCaptor.forClass(Consumer.class);

        when(mqtt5AsyncClient.connect()).thenReturn(CompletableFuture.completedFuture(connAck));
        when(mqtt5AsyncClient.subscribeWith()
                .topicFilter(anyString())
                .callback(callbackCaptor.capture())
                .send()).thenReturn(CompletableFuture.completedFuture(subAck));

        when(objectMapper.readValue(any(byte[].class), eq(GpsStatusDTO.class)))
                .thenThrow(new RuntimeException("bad payload"));

        service.subscribe();

        Mqtt5Publish publish = mock(Mqtt5Publish.class);
        when(publish.getPayloadAsBytes()).thenReturn("invalid".getBytes(StandardCharsets.UTF_8));

        StepVerifier.create(service.getMessages())
                .then(() -> callbackCaptor.getValue().accept(publish))
                .expectNoEvent(Duration.ofMillis(150))
                .thenCancel()
                .verify();
    }

    @Test void subscribeDoesNotStartSubscriptionWhenConnectFails() {
        CompletableFuture<Mqtt5ConnAck> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("connect failed"));

        when(mqtt5AsyncClient.connect()).thenReturn(failed);

        service.subscribe();

        verify(mqtt5AsyncClient, never()).subscribeWith();
    }
}

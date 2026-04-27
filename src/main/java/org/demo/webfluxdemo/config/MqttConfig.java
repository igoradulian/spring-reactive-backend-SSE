package org.demo.webfluxdemo.config;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Igor Adulyan
 */
@Configuration
@Slf4j
public class MqttConfig {

    @Value("${MQTT_USER}")
    String user;

    @Value("${MQTT_PASSWORD}")
    String password;

    @Value("${MQTT_HOST}")
    String host;

    @Value("${MQTT_PORT}")
    int port;

    @Bean
    public Mqtt5AsyncClient mqtt5AsyncClient() {
        String clientId = "webflux-subscriber-" + UUID.randomUUID();
        log.info("Creating MQTT async client for host={} port={} clientId={}", host, port, clientId);

        return MqttClient.builder()
                .useMqttVersion5()
                .identifier(clientId)
                .serverHost(host)
                .serverPort(port)
                .sslWithDefaultConfig()
                .simpleAuth()
                .username(user)
                .password(StandardCharsets.UTF_8.encode(password))
                .applySimpleAuth()
                .automaticReconnect()
                .applyAutomaticReconnect()
                .buildAsync();

    }
}

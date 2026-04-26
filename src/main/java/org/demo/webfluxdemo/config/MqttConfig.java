package org.demo.webfluxdemo.config;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * @author Igor Adulyan
 */
@Configuration
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
        return MqttClient.builder()
                .useMqttVersion5()
                .identifier("webflux-subscriber-" + UUID.randomUUID())
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

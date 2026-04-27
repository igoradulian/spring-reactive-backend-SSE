# Web Flux Demo

![Status](https://img.shields.io/badge/status-active-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring_Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F)
![WebFlux](https://img.shields.io/badge/WebFlux-reactive-blue)
![Maven](https://img.shields.io/badge/Maven-build-C71A36)
![MQTT](https://img.shields.io/badge/MQTT-HiveMQ-00A3E0)

Reactive Spring Boot service that connects to HiveMQ Cloud over MQTT v5, parses GPS satellite payloads, and streams updates to clients via Server-Sent Events (SSE).

## Features

- Connects to HiveMQ using async MQTT client (`hivemq-mqtt-client`)
- Subscribes to MQTT topic: `gps/satellites`
- Parses incoming JSON into DTOs (`GpsStatusDTO`, `SatelliteDTO`)
- Publishes live updates from `/api/gps/data/subscribe` as SSE
- Includes unit and controller tests with JUnit 5 + Mockito + WebTestClient

## Coding Stack

- Java 17
- Spring Boot 4 (WebFlux)
- Reactor
- Maven
- HiveMQ MQTT Client

## Prerequisites

- Java 17+
- Maven (or use `./mvnw`)
- HiveMQ Cloud credentials

## Configuration

The app imports environment variables from `.env`:

```dotenv
MQTT_HOST=your-broker-host
MQTT_PORT=8883
MQTT_USER=your-username
MQTT_PASSWORD=your-password
```

## Run

```bash
./mvnw spring-boot:run
```

Default server URL:

- `http://localhost:8080`

## API

### Subscribe to GPS stream

- **Method:** `GET`
- **Path:** `/api/gps/data/subscribe`
- **Response type:** `text/event-stream`
- **SSE event name:** `sky-update`

Example event payload:

```json
{
  "visibleCount": 12,
  "usedCount": 4,
  "hdop": 1.9,
  "satellites": [
    {
      "id": 23,
      "azimuth": 6.78,
      "elevation": 63.73,
      "signal": 0.0,
      "used": false,
      "fading": false
    }
  ]
}
```

Quick check from terminal:

```bash
curl -N http://localhost:8080/api/gps/data/subscribe
```

## Tests

Run all tests:

```bash
./mvnw test
```

Run specific tests:

```bash
./mvnw -Dtest=GPSControllerTest test
./mvnw -Dtest=HiveMQSubscriberServiceTest test
```

## Project Structure

- `src/main/java/org/demo/webfluxdemo/config` - MQTT client config
- `src/main/java/org/demo/webfluxdemo/service` - MQTT subscription + reactive sink
- `src/main/java/org/demo/webfluxdemo/controller` - SSE endpoint
- `src/main/java/org/demo/webfluxdemo/dto` - GPS payload records
- `src/test/java/org/demo/webfluxdemo` - unit/integration tests

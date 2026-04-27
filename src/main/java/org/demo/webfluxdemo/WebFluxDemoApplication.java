package org.demo.webfluxdemo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Web Flux GPS API",
        version = "1.0",
        description = "Reactive API that streams GPS satellite updates from HiveMQ MQTT"
))
public class WebFluxDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxDemoApplication.class, args);
    }

}

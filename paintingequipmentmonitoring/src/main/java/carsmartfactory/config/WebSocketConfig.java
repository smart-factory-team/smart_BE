package carsmartfactory.config;

import carsmartfactory.infra.MonitoringWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ObjectMapper objectMapper;

    public WebSocketConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(monitoringWebSocketHandler(), "/ws/monitoring-data").setAllowedOrigins("*");
    }

    @Bean
    public MonitoringWebSocketHandler monitoringWebSocketHandler() {
        return new MonitoringWebSocketHandler(objectMapper);
    }
}

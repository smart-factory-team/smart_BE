package carsmartfactory.infra.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 웹소켓 설정
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MonitoringWebSocketHandler monitoringHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(monitoringHandler, "/ws/monitoring")
                .setAllowedOrigins("*"); // CORS 설정 (프로덕션에서는 특정 도메인으로 제한)
    }
}
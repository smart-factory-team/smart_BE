package carsmartfactory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import carsmartfactory.websocket.DefectNotificationWebSocketHandler;
import carsmartfactory.websocket.MonitoringDataWebSocketHandler; // ✨ 새로 추가

/**
 * WebSocket 설정 클래스 1. 실시간 이상 탐지 결과 전송을 위한 WebSocket 엔드포인트 설정 ✅ (기존) 2. 실시간 모니터링 차트를 위한 WebSocket 엔드포인트 설정 ✨ (신규 추가)
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // ✅ 기존 핸들러 (그대로 유지)
    @Autowired
    private DefectNotificationWebSocketHandler defectNotificationHandler;

    // ✨ 새로 추가된 핸들러
    @Autowired
    private MonitoringDataWebSocketHandler monitoringDataHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        // ✅ 기존 이상 탐지 알림용 WebSocket 엔드포인트 (그대로 유지)
        registry.addHandler(defectNotificationHandler, "/ws/defect-notifications")
                .setAllowedOrigins("*"); // 개발환경: 모든 Origin 허용

        // SockJS 지원 (브라우저 호환성 향상)
        registry.addHandler(defectNotificationHandler, "/ws/defect-notifications-sockjs")
                .setAllowedOrigins("*")
                .withSockJS();

        // ✨ 새로 추가: 실시간 모니터링 차트용 WebSocket 엔드포인트
        registry.addHandler(monitoringDataHandler, "/ws/monitoring-data")
                .setAllowedOrigins("*"); // 개발환경: 모든 Origin 허용

        // SockJS 지원 (브라우저 호환성 향상)
        registry.addHandler(monitoringDataHandler, "/ws/monitoring-data-sockjs")
                .setAllowedOrigins("*")
                .withSockJS();

        System.out.println("✅ WebSocket 엔드포인트 등록 완료:");
        System.out.println("   📢 이상 알림: /ws/defect-notifications");
        System.out.println("   📊 모니터링: /ws/monitoring-data");
    }
}
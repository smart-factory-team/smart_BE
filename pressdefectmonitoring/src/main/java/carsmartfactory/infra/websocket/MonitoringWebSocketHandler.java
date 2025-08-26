package carsmartfactory.infra.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 모니터링 데이터 웹소켓 핸들러
 */
@Slf4j
@Component
public class MonitoringWebSocketHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("웹소켓 연결 생성: {} (총 연결: {})", session.getId(), sessions.size());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        log.info("웹소켓 메시지 수신: {} - {}", session.getId(), message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("웹소켓 전송 오류: {} - {}", session.getId(), exception.getMessage());
        sessions.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessions.remove(session.getId());
        log.info("웹소켓 연결 종료: {} (총 연결: {})", session.getId(), sessions.size());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 모든 연결된 클라이언트에게 모니터링 데이터 전송
     */
    public void broadcastMonitoringData(Object monitoringData) {
        if (sessions.isEmpty()) {
            log.debug("연결된 웹소켓 클라이언트 없음");
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(monitoringData);
            
            sessions.values().parallelStream().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(message));
                        log.debug("모니터링 데이터 전송 성공: {}", session.getId());
                    }
                } catch (Exception e) {
                    log.error("모니터링 데이터 전송 실패: {} - {}", session.getId(), e.getMessage());
                    sessions.remove(session.getId());
                }
            });
            
            log.info("모니터링 데이터 브로드캐스트 완료: {} 클라이언트", sessions.size());
            
        } catch (Exception e) {
            log.error("모니터링 데이터 직렬화 실패: {}", e.getMessage());
        }
    }
}
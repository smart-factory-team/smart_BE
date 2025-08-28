package carsmartfactory.infra.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 모니터링 데이터 웹소켓 핸들러
 */
@Component
public class MonitoringWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocket 연결 성공: " + session.getId() + 
                          " (현재 연결: " + sessions.size() + "개)");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket 연결 종료: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket 오류: " + session.getId());
        sessions.remove(session);
    }

    public void broadcastMonitoringData(Object monitoringData) {
        if (sessions.isEmpty()) {
            System.out.println("연결된 웹소켓 클라이언트 없음");
            return;
        }

        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    String message = objectMapper.writeValueAsString(monitoringData);
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    System.err.println("메시지 전송 실패: " + e.getMessage());
                    sessions.remove(session);
                }
            }
        });
    }
}
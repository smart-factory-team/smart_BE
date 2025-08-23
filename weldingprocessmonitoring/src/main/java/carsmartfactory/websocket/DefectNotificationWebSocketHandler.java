package carsmartfactory.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import carsmartfactory.dto.DefectNotificationMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 실시간 이상 탐지 알림을 위한 WebSocket 핸들러 클라이언트 연결 관리 및 메시지 브로드캐스팅 담당
 */
@Component
public class DefectNotificationWebSocketHandler extends TextWebSocketHandler {

    // 연결된 모든 WebSocket 세션 관리
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // 세션별 메타데이터 관리 (필요시 확장 가능)
    private final ConcurrentHashMap<String, Object> sessionMetadata = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 클라이언트 연결 시 호출
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sessionMetadata.put(session.getId(), System.currentTimeMillis());

        System.out.println("✅ WebSocket 연결 성공: " + session.getId() +
                " (현재 연결된 클라이언트: " + sessions.size() + "개)");

        // 연결 확인 메시지 전송
        DefectNotificationMessage welcomeMessage = new DefectNotificationMessage(
                "CONNECTION_ESTABLISHED",
                "WebSocket 연결이 성공적으로 설정되었습니다.",
                null,
                "NORMAL",
                System.currentTimeMillis()
        );

        sendMessageToSession(session, welcomeMessage);
    }

    /**
     * 클라이언트로부터 메시지 수신 시 호출 (현재는 단방향 통신)
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 현재는 서버에서 클라이언트로만 메시지 전송
        // 필요시 클라이언트 요청 처리 로직 추가 가능
        System.out.println("📥 클라이언트 메시지 수신: " + message.getPayload());
    }

    /**
     * 연결 종료 시 호출
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionMetadata.remove(session.getId());

        System.out.println("❌ WebSocket 연결 종료: " + session.getId() +
                " (현재 연결된 클라이언트: " + sessions.size() + "개)");
    }

    /**
     * 에러 발생 시 호출
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("🚨 WebSocket 전송 오류: " + session.getId() + " - " + exception.getMessage());

        // 오류 발생한 세션 정리
        if (sessions.contains(session)) {
            sessions.remove(session);
            sessionMetadata.remove(session.getId());
        }
    }

    /**
     * 모든 연결된 클라이언트에게 이상 탐지 알림 브로드캐스팅
     */
    public void broadcastDefectNotification(DefectNotificationMessage message) {
        if (sessions.isEmpty()) {
            System.out.println("⚠️ 연결된 WebSocket 클라이언트가 없습니다.");
            return;
        }

        // 모든 세션에 메시지 전송
        sessions.forEach(session -> {
            if (session.isOpen()) {
                sendMessageToSession(session, message);
            } else {
                // 닫힌 세션 제거
                sessions.remove(session);
                sessionMetadata.remove(session.getId());
            }
        });

        System.out.println("📡 이상 탐지 알림 브로드캐스팅 완료: " +
                message.getEventType() + " -> " + sessions.size() + "개 클라이언트");
    }

    /**
     * 특정 세션에 메시지 전송
     */
    private void sendMessageToSession(WebSocketSession session, DefectNotificationMessage message) {
        try {
            if (session.isOpen()) {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        } catch (IOException e) {
            System.err.println("❌ 메시지 전송 실패 (세션: " + session.getId() + "): " + e.getMessage());

            // 전송 실패한 세션 제거
            sessions.remove(session);
            sessionMetadata.remove(session.getId());
        }
    }

    /**
     * 현재 연결된 클라이언트 수 반환
     */
    public int getConnectedClientCount() {
        return sessions.size();
    }

    /**
     * 연결 상태 확인
     */
    public boolean hasConnectedClients() {
        return !sessions.isEmpty();
    }
}
package carsmartfactory.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import carsmartfactory.dto.MonitoringDataMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 실시간 모니터링 차트용 WebSocket 핸들러 모든 예측 결과(정상/이상)를 차트 데이터로 전송 기존 DefectNotificationWebSocketHandler와 완전 분리된 독립적인 핸들러
 */
@Component
public class MonitoringDataWebSocketHandler extends TextWebSocketHandler {

    // 연결된 모든 WebSocket 세션 관리 (차트 클라이언트용)
    private final CopyOnWriteArraySet<WebSocketSession> monitoringSessions = new CopyOnWriteArraySet<>();

    // 세션별 메타데이터 관리
    private final ConcurrentHashMap<String, SessionMetadata> sessionMetadata = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 클라이언트 연결 시 호출
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        monitoringSessions.add(session);

        // 세션 메타데이터 저장
        SessionMetadata metadata = new SessionMetadata(
                System.currentTimeMillis(),
                session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown"
        );
        sessionMetadata.put(session.getId(), metadata);

        System.out.println("📊 모니터링 차트 WebSocket 연결 성공: " + session.getId() +
                " (현재 모니터링 클라이언트: " + monitoringSessions.size() + "개)");

        // 연결 확인 메시지 전송
        MonitoringDataMessage welcomeMessage = new MonitoringDataMessage(
                "CONNECTION_ESTABLISHED",
                "모니터링 차트 WebSocket 연결이 성공적으로 설정되었습니다."
        );

        sendMessageToSession(session, welcomeMessage);
    }

    /**
     * 클라이언트로부터 메시지 수신 시 호출 현재는 단방향 통신이지만, 필요시 클라이언트 요청 처리 가능
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("📥 모니터링 클라이언트 메시지 수신: " + message.getPayload());

        // 필요시 클라이언트 요청 처리 로직 추가
        // 예: 특정 장비만 모니터링, 시간 범위 설정 등
    }

    /**
     * 연결 종료 시 호출
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        monitoringSessions.remove(session);
        sessionMetadata.remove(session.getId());

        System.out.println("📊❌ 모니터링 차트 WebSocket 연결 종료: " + session.getId() +
                " (현재 모니터링 클라이언트: " + monitoringSessions.size() + "개)");
    }

    /**
     * 에러 발생 시 호출
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("🚨 모니터링 차트 WebSocket 전송 오류: " + session.getId() + " - " + exception.getMessage());

        // 오류 발생한 세션 정리
        if (monitoringSessions.contains(session)) {
            monitoringSessions.remove(session);
            sessionMetadata.remove(session.getId());
        }
    }

    /**
     * 모든 연결된 모니터링 클라이언트에게 예측 결과 브로드캐스팅 정상/이상 구분 없이 모든 결과를 차트 데이터로 전송
     */
    public void broadcastMonitoringData(MonitoringDataMessage message) {
        if (monitoringSessions.isEmpty()) {
            // 로그 레벨을 낮춤 (차트 클라이언트가 없어도 정상)
            System.out.println("📊⚪ 연결된 모니터링 차트 클라이언트가 없습니다.");
            return;
        }

        // 모든 모니터링 세션에 메시지 전송
        monitoringSessions.forEach(session -> {
            if (session.isOpen()) {
                sendMessageToSession(session, message);
            } else {
                // 닫힌 세션 제거
                monitoringSessions.remove(session);
                sessionMetadata.remove(session.getId());
            }
        });

        System.out.println("📊📡 모니터링 데이터 브로드캐스팅 완료: " +
                message.getEquipmentId() + "-" + message.getSignalType() +
                " (" + message.getPredictionResult().getStatus() + ") -> " +
                monitoringSessions.size() + "개 클라이언트");
    }

    /**
     * 시스템 상태 메시지 브로드캐스팅
     */
    public void broadcastSystemStatus(String messageType, String systemMessage) {
        MonitoringDataMessage statusMessage = new MonitoringDataMessage(messageType, systemMessage);

        monitoringSessions.forEach(session -> {
            if (session.isOpen()) {
                sendMessageToSession(session, statusMessage);
            }
        });

        System.out.println("📊🔔 시스템 상태 브로드캐스팅: " + messageType + " -> " +
                monitoringSessions.size() + "개 클라이언트");
    }

    /**
     * 특정 세션에 메시지 전송
     */
    private void sendMessageToSession(WebSocketSession session, MonitoringDataMessage message) {
        try {
            if (session.isOpen()) {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        } catch (IOException e) {
            System.err.println("❌ 모니터링 메시지 전송 실패 (세션: " + session.getId() + "): " + e.getMessage());

            // 전송 실패한 세션 제거
            monitoringSessions.remove(session);
            sessionMetadata.remove(session.getId());
        }
    }

    /**
     * 현재 연결된 모니터링 클라이언트 수 반환
     */
    public int getConnectedMonitoringClientCount() {
        return monitoringSessions.size();
    }

    /**
     * 모니터링 클라이언트 연결 상태 확인
     */
    public boolean hasConnectedMonitoringClients() {
        return !monitoringSessions.isEmpty();
    }

    /**
     * 세션 메타데이터 내부 클래스
     */
    private static class SessionMetadata {
        private final long connectionTime;
        private final String clientAddress;

        public SessionMetadata(long connectionTime, String clientAddress) {
            this.connectionTime = connectionTime;
            this.clientAddress = clientAddress;
        }

        public long getConnectionTime() {
            return connectionTime;
        }

        public String getClientAddress() {
            return clientAddress;
        }
    }
}
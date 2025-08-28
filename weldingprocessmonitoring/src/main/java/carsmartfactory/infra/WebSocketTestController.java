package carsmartfactory.infra;

import carsmartfactory.services.DefectNotificationService;
import carsmartfactory.services.WeldingDataService;
import carsmartfactory.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * WebSocket 테스트 및 관리용 컨트롤러
 */
@RestController
@RequestMapping(value = "/api/websocket")
public class WebSocketTestController {

    @Autowired
    private DefectNotificationService notificationService;

    @Autowired
    private WeldingDataService weldingDataService;

    /**
     * WebSocket 연결 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Object>> getWebSocketStatus() {
        try {
            int clientCount = notificationService.getConnectedClientCount();
            boolean hasClients = notificationService.hasConnectedClients();

            return ResponseEntity.ok(
                    ApiResponse.success("WebSocket 상태 조회 성공",
                            new WebSocketStatus(clientCount, hasClients))
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("WebSocket 상태 조회 실패: " + e.getMessage(), "STATUS_ERROR")
            );
        }
    }

    /**
     * 테스트 메시지 전송
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<String>> sendTestMessage() {
        try {
            weldingDataService.sendTestNotification();

            return ResponseEntity.ok(
                    ApiResponse.success("테스트 메시지 전송 완료", "WebSocket 테스트 메시지가 전송되었습니다.")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("테스트 메시지 전송 실패: " + e.getMessage(), "TEST_ERROR")
            );
        }
    }

    /**
     * 이상 탐지 시뮬레이션 (테스트용)
     */
    @PostMapping("/simulate/defect/{machineId}")
    public ResponseEntity<ApiResponse<String>> simulateDefect(@PathVariable Long machineId) {
        try {
            // 가짜 이상 탐지 알림 전송
            notificationService.sendSystemMessage(
                    "DEFECT_DETECTED",
                    "용접기 " + machineId + "번에서 시뮬레이션 이상이 감지되었습니다.",
                    "ANOMALY"
            );

            return ResponseEntity.ok(
                    ApiResponse.success("이상 탐지 시뮬레이션 완료",
                            "용접기 " + machineId + "번 이상 탐지 알림이 전송되었습니다.")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("이상 탐지 시뮬레이션 실패: " + e.getMessage(), "SIMULATION_ERROR")
            );
        }
    }

    /**
     * 정상 복구 시뮬레이션 (테스트용)
     */
    @PostMapping("/simulate/normal/{machineId}")
    public ResponseEntity<ApiResponse<String>> simulateNormal(@PathVariable Long machineId) {
        try {
            // 가짜 정상 복구 알림 전송
            notificationService.sendSystemMessage(
                    "SYSTEM_NORMAL",
                    "용접기 " + machineId + "번이 정상 상태로 복구되었습니다.",
                    "NORMAL"
            );

            return ResponseEntity.ok(
                    ApiResponse.success("정상 복구 시뮬레이션 완료",
                            "용접기 " + machineId + "번 정상 복구 알림이 전송되었습니다.")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("정상 복구 시뮬레이션 실패: " + e.getMessage(), "SIMULATION_ERROR")
            );
        }
    }

    /**
     * WebSocket 상태 정보 내부 클래스
     */
    public static class WebSocketStatus {
        private int connectedClients;
        private boolean hasActiveConnections;

        public WebSocketStatus(int connectedClients, boolean hasActiveConnections) {
            this.connectedClients = connectedClients;
            this.hasActiveConnections = hasActiveConnections;
        }

        // Getters
        public int getConnectedClients() {
            return connectedClients;
        }

        public boolean isHasActiveConnections() {
            return hasActiveConnections;
        }
    }
}
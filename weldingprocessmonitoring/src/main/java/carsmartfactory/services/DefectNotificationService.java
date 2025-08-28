package carsmartfactory.services;

import carsmartfactory.dto.DefectNotificationMessage;
import carsmartfactory.dto.ModelPredictionResponse;
import carsmartfactory.dto.SensorDataRequest;
import carsmartfactory.websocket.DefectNotificationWebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 이상 탐지 알림 서비스 WebSocket을 통한 실시간 알림 전송 담당
 */
@Service
public class DefectNotificationService {

    @Autowired
    private DefectNotificationWebSocketHandler webSocketHandler;

    /**
     * 이상 탐지 시 실시간 알림 전송
     */
    public void sendDefectAlert(SensorDataRequest sensorData, ModelPredictionResponse prediction) {
        try {
            // machineId에서 숫자만 추출
            Long machineId = extractMachineId(sensorData.getMachineId());

            DefectNotificationMessage message = DefectNotificationMessage.createDefectAlert(
                    machineId,
                    sensorData.getSignalType(),
                    String.format("WELDING-%s-ANOMALY (MAE: %.4f, Threshold: %.4f)",
                            sensorData.getSignalType().toUpperCase(),
                            prediction.getMae(),
                            prediction.getThreshold()),
                    prediction.getMae(),
                    prediction.getThreshold()
            );

            // WebSocket을 통해 모든 연결된 클라이언트에게 브로드캐스팅
            webSocketHandler.broadcastDefectNotification(message);

            System.out.println("🚨 이상 탐지 알림 전송 완료: " + sensorData.getMachineId() +
                    " (" + sensorData.getSignalType() + ")");

        } catch (Exception e) {
            System.err.println("❌ 이상 탐지 알림 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 정상 상태 알림 전송
     */
    public void sendNormalAlert(SensorDataRequest sensorData) {
        try {
            // machineId에서 숫자만 추출
            Long machineId = extractMachineId(sensorData.getMachineId());

            DefectNotificationMessage message = DefectNotificationMessage.createNormalAlert(
                    machineId,
                    sensorData.getSignalType()
            );

            // WebSocket을 통해 모든 연결된 클라이언트에게 브로드캐스팅
            webSocketHandler.broadcastDefectNotification(message);

            System.out.println("✅ 정상 상태 알림 전송 완료: " + sensorData.getMachineId() +
                    " (" + sensorData.getSignalType() + ")");

        } catch (Exception e) {
            System.err.println("❌ 정상 상태 알림 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 시스템 상태 메시지 전송 (연결 테스트 등)
     */
    public void sendSystemMessage(String eventType, String message, String status) {
        try {
            DefectNotificationMessage systemMessage = new DefectNotificationMessage(
                    eventType,
                    message,
                    null, // equipmentData 없음
                    status,
                    System.currentTimeMillis()
            );

            webSocketHandler.broadcastDefectNotification(systemMessage);

            System.out.println("📡 시스템 메시지 전송 완료: " + eventType);

        } catch (Exception e) {
            System.err.println("❌ 시스템 메시지 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 연결된 클라이언트 수 확인
     */
    public int getConnectedClientCount() {
        return webSocketHandler.getConnectedClientCount();
    }

    /**
     * WebSocket 연결 상태 확인
     */
    public boolean hasConnectedClients() {
        return webSocketHandler.hasConnectedClients();
    }

    /**
     * machineId 문자열에서 숫자만 추출 예: "WELDING_MACHINE_001" -> 1
     */
    private Long extractMachineId(String machineIdStr) {
        try {
            // 문자열에서 숫자만 추출
            String numberStr = machineIdStr.replaceAll("[^0-9]", "");
            return Long.parseLong(numberStr);
        } catch (NumberFormatException e) {
            // 추출 실패시 기본값 1 반환
            System.err.println("⚠️ machineId 추출 실패, 기본값 사용: " + machineIdStr);
            return 1L;
        }
    }
}
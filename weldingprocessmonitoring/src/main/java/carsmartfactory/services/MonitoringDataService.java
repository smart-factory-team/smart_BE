package carsmartfactory.services;

import carsmartfactory.dto.MonitoringDataMessage;
import carsmartfactory.dto.MonitoringDataMessage.PredictionData;
import carsmartfactory.dto.MonitoringDataMessage.SensorSummary;
import carsmartfactory.dto.SensorDataRequest;
import carsmartfactory.dto.ModelPredictionResponse;
import carsmartfactory.websocket.MonitoringDataWebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.DoubleSummaryStatistics;

/**
 * 실시간 모니터링 차트용 데이터 서비스 모든 예측 결과를 차트 데이터로 변환하여 WebSocket으로 전송 기존 DefectNotificationService와 완전 분리된 독립적인 서비스
 */
@Service
@Transactional
public class MonitoringDataService {

    @Autowired
    private MonitoringDataWebSocketHandler monitoringWebSocketHandler;

    /**
     * MAE 값으로부터 신뢰도 계산 MAE가 낮을수록 높은 신뢰도를 가지도록 계산
     */
    private double calculateConfidenceFromMae(Double mae, Double threshold) {
        if (mae == null || threshold == null || threshold <= 0) {
            return 0.5; // 기본값
        }

        // MAE가 임계값보다 낮으면 높은 신뢰도, 높으면 낮은 신뢰도
        if (mae <= threshold) {
            // 정상: MAE가 0에 가까울수록 신뢰도 1.0에 가까움
            return Math.max(0.5, 1.0 - (mae / threshold) * 0.5);
        } else {
            // 이상: MAE가 임계값을 초과할수록 이상 탐지 신뢰도 증가
            double ratio = mae / threshold;
            return Math.min(0.95, 0.5 + (ratio - 1.0) * 0.3);
        }
    }

    /**
     * 모든 예측 결과를 모니터링 차트용으로 전송 정상/이상 구분 없이 모든 결과를 차트 데이터로 변환
     */
    public void sendMonitoringData(SensorDataRequest request, ModelPredictionResponse prediction) {
        if (prediction == null) {
            System.out.println("⚠️ 예측 결과가 null이므로 모니터링 데이터 전송을 건너뜁니다.");
            return;
        }

        try {
            // 센서 데이터 요약 계산
            SensorSummary sensorSummary = calculateSensorSummary(request.getSensorValues());

            // 예측 데이터 구성 (ModelPredictionResponse 실제 필드에 맞게 수정)
            PredictionData predictionData = new PredictionData(
                    prediction.isAnomalous(),
                    calculateConfidenceFromMae(prediction.getMae(), prediction.getThreshold()), // MAE로부터 신뢰도 계산
                    prediction.getMae() != null ? prediction.getMae() : 0.0, // MAE를 이상 점수로 사용
                    prediction.getThreshold() != null ? prediction.getThreshold() : 0.5, // 실제 임계값 사용
                    sensorSummary
            );

            // 모니터링 메시지 생성
            MonitoringDataMessage monitoringMessage = new MonitoringDataMessage(
                    request.getMachineId(),
                    request.getSignalType(),
                    predictionData
            );

            // WebSocket으로 브로드캐스팅
            monitoringWebSocketHandler.broadcastMonitoringData(monitoringMessage);

            System.out.println("📊✅ 모니터링 데이터 전송 완료: " +
                    request.getMachineId() + "-" + request.getSignalType() +
                    " (" + (prediction.isAnomalous() ? "이상" : "정상") + ")");

        } catch (Exception e) {
            System.err.println("❌ 모니터링 데이터 전송 실패: " + e.getMessage());

            // 에러 발생 시 시스템 메시지 전송
            sendSystemErrorMessage("모니터링 데이터 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 센서 데이터 요약 통계 계산
     */
    private SensorSummary calculateSensorSummary(List<Double> sensorValues) {
        if (sensorValues == null || sensorValues.isEmpty()) {
            return new SensorSummary(0.0, 0.0, 0.0, 0.0, 0);
        }

        // Java 8 Stream을 사용한 통계 계산
        DoubleSummaryStatistics stats = sensorValues.stream()
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        // 표준편차 계산
        double mean = stats.getAverage();
        double variance = sensorValues.stream()
                .mapToDouble(Double::doubleValue)
                .map(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0);
        double standardDeviation = Math.sqrt(variance);

        return new SensorSummary(
                stats.getMin(),
                stats.getMax(),
                stats.getAverage(),
                standardDeviation,
                (int) stats.getCount()
        );
    }

    /**
     * 시스템 상태 메시지 전송
     */
    public void sendSystemStatusMessage(String messageType, String message) {
        try {
            monitoringWebSocketHandler.broadcastSystemStatus(messageType, message);
            System.out.println("📊🔔 시스템 상태 메시지 전송: " + messageType);
        } catch (Exception e) {
            System.err.println("❌ 시스템 상태 메시지 전송 실패: " + e.getMessage());
        }
    }

    /**
     * 에러 메시지 전송
     */
    public void sendSystemErrorMessage(String errorMessage) {
        sendSystemStatusMessage("SYSTEM_ERROR", errorMessage);
    }

    /**
     * 모니터링 서비스 상태 확인 메시지 전송
     */
    public void sendServiceHealthCheck() {
        sendSystemStatusMessage("SERVICE_HEALTH_CHECK",
                "모니터링 서비스가 정상 작동 중입니다. (연결된 클라이언트: " +
                        getConnectedClientCount() + "개)");
    }

    /**
     * 연결된 모니터링 클라이언트 수 반환
     */
    public int getConnectedClientCount() {
        return monitoringWebSocketHandler.getConnectedMonitoringClientCount();
    }

    /**
     * 모니터링 클라이언트 연결 상태 확인
     */
    public boolean hasConnectedClients() {
        return monitoringWebSocketHandler.hasConnectedMonitoringClients();
    }

    /**
     * 테스트용 샘플 데이터 전송
     */
    public void sendTestMonitoringData() {
        try {
            // 테스트용 센서 데이터 생성
            SensorSummary testSensorSummary = new SensorSummary(
                    -1.5, 2.3, 0.2, 0.8, 1024
            );

            // 테스트용 예측 데이터 생성
            PredictionData testPredictionData = new PredictionData(
                    false, 0.95, 0.15, 0.5, testSensorSummary
            );

            // 테스트 메시지 생성
            MonitoringDataMessage testMessage = new MonitoringDataMessage(
                    "WELDING_MACHINE_TEST",
                    "current",
                    testPredictionData
            );

            // 브로드캐스팅
            monitoringWebSocketHandler.broadcastMonitoringData(testMessage);

            System.out.println("🧪 테스트 모니터링 데이터 전송 완료");

        } catch (Exception e) {
            System.err.println("❌ 테스트 데이터 전송 실패: " + e.getMessage());
        }
    }
}
package carsmartfactory.services;

import carsmartfactory.domain.WeldingMachineDefectDetectionLog;
import carsmartfactory.domain.WeldingMachineDefectDetectionLogRepository;
import carsmartfactory.dto.SensorDataRequest;
import carsmartfactory.dto.ModelPredictionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class WeldingDataService {

    @Autowired
    private WeldingMachineDefectDetectionLogRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    // ✅ 기존: 실시간 알림 서비스 (그대로 유지)
    @Autowired
    private DefectNotificationService notificationService;

    // ✨ 새로 추가: 실시간 모니터링 데이터 서비스
    @Autowired
    private MonitoringDataService monitoringDataService;

    @Value("${welding.model-service.url:http://localhost:8006}")
    private String modelServiceUrl;

    /**
     * 센서 데이터 전체 처리 플로우 1. 모델 서빙 서비스 호출 2. ✅ 기존: 이상 감지시 DB 저장 + 알림 전송 (그대로 유지) 3. ✨ 새로 추가: 모든 예측 결과를 모니터링 차트로 전송
     */
    public WeldingMachineDefectDetectionLog processSensorData(SensorDataRequest request) {

        // 1. 모델 서빙 서비스 호출
        ModelPredictionResponse prediction = callModelService(request);

        // ✨ 새로 추가: 모든 예측 결과를 모니터링 차트로 전송 (정상/이상 구분 없이)
        if (prediction != null) {
            monitoringDataService.sendMonitoringData(request, prediction);
        }

        // 2. ✅ 기존 로직: 예측 결과에 따른 처리 및 실시간 알림 (그대로 유지)
        if (prediction != null && prediction.isAnomalous()) {
            // 이상 감지: DB 저장 + 실시간 알림
            WeldingMachineDefectDetectionLog saved = saveAnomalyLog(request, prediction);

            // ✅ 기존: 이상 탐지 실시간 알림 전송 (그대로 유지)
            notificationService.sendDefectAlert(request, prediction);

            return saved;
        } else {
            // 정상: 로그만 출력 + 정상 상태 실시간 알림
            System.out.println("✅ 정상 데이터: " + request.getSignalType() +
                    " (Machine: " + request.getMachineId() + ")");

            // ✅ 기존: 정상 상태 실시간 알림 전송 (선택사항, 그대로 유지)
            // 너무 많은 알림을 방지하기 위해 필요시에만 활성화
            // notificationService.sendNormalAlert(request);

            return null;
        }
    }

    /**
     * ✅ 기존: 모델 서빙 서비스 호출 (그대로 유지)
     */
    private ModelPredictionResponse callModelService(SensorDataRequest request) {
        try {
            String url = modelServiceUrl + "/api/predict";

            // 모델 서빙 서비스에 보낼 요청 데이터 구성
            Map<String, Object> modelRequest = new HashMap<>();
            modelRequest.put("signal_type", request.getSignalType());
            modelRequest.put("values", request.getSensorValues());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(modelRequest, headers);

            // API 호출
            ModelPredictionResponse response = restTemplate.postForObject(url, entity, ModelPredictionResponse.class);

            System.out.println("🔍 모델 예측 결과: " + response);
            return response;

        } catch (Exception e) {
            System.err.println("⌚ 모델 서비스 호출 실패: " + e.getMessage());

            // ✅ 기존: 모델 서비스 연결 실패 알림 (그대로 유지)
            notificationService.sendSystemMessage(
                    "MODEL_SERVICE_ERROR",
                    "모델 서빙 서비스 연결에 실패했습니다: " + e.getMessage(),
                    "ERROR"
            );

            // ✨ 새로 추가: 모니터링 서비스에도 에러 알림
            monitoringDataService.sendSystemErrorMessage(
                    "모델 서빙 서비스 연결 실패: " + e.getMessage()
            );

            throw new RuntimeException("모델 서비스 호출 실패", e);
        }
    }

    /**
     * ✅ 기존: 이상 감지 로그 저장 (그대로 유지)
     */
    private WeldingMachineDefectDetectionLog saveAnomalyLog(SensorDataRequest request,
                                                            ModelPredictionResponse prediction) {

        WeldingMachineDefectDetectionLog log = new WeldingMachineDefectDetectionLog();

        // 기본 정보
        log.setMachineId(Long.parseLong(request.getMachineId().replaceAll("[^0-9]", "")));
        log.setTimeStamp(parseTimestamp(request.getTimestamp()));
        log.setIsSolved(false);

        // 이상 정보
        String issueDescription = String.format("WELDING-%s-ANOMALY-%s",
                request.getSignalType().toUpperCase(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
        log.setIssue(issueDescription);

        // ✅ 센서 값들 매핑 (기존 컬럼 구조에 맞게)
        List<Double> values = request.getSensorValues();
        if (!values.isEmpty()) {
            // 인덱스별 센서 값 매핑 (1024개 배열에서 특정 인덱스만 추출)
            log.setSensorValue0Ms(getValueAtIndex(values, 0));
            log.setSensorValue25Ms(getValueAtIndex(values, 25));
            log.setSensorValue125Ms(getValueAtIndex(values, 125));
            log.setSensorValue312Ms(getValueAtIndex(values, 312));
            log.setSensorValue375Ms(getValueAtIndex(values, 375));
            log.setSensorValue625Ms(getValueAtIndex(values, 625));
            log.setSensorValue938Ms(getValueAtIndex(values, 938));
            log.setSensorValue1562Ms(getValueAtIndex(values, 1562));
            log.setSensorValue1875Ms(getValueAtIndex(values, 1875));
            log.setSensorValue2188Ms(getValueAtIndex(values, 2188));
            log.setSensorValue2812Ms(getValueAtIndex(values, 2812));
            log.setSensorValue3125Ms(getValueAtIndex(values, 3125));
            log.setSensorValue3438Ms(getValueAtIndex(values, 3438));
            log.setSensorValue4062Ms(getValueAtIndex(values, 4062));
        }

        WeldingMachineDefectDetectionLog saved = repository.save(log);
        System.out.println("💾 이상 감지 로그 저장 완료: " + saved.getId());

        return saved;
    }

    /**
     * ✅ 기존: 연결된 WebSocket 클라이언트 수 확인 (그대로 유지)
     */
    public int getWebSocketClientCount() {
        return notificationService.getConnectedClientCount();
    }

    /**
     * ✨ 새로 추가: 연결된 모니터링 클라이언트 수 확인
     */
    public int getMonitoringClientCount() {
        return monitoringDataService.getConnectedClientCount();
    }

    /**
     * ✅ 기존: 테스트용 알림 전송 메서드 (그대로 유지)
     */
    public void sendTestNotification() {
        notificationService.sendSystemMessage(
                "TEST_MESSAGE",
                "WebSocket 연결 테스트 메시지입니다.",
                "INFO"
        );
    }

    /**
     * ✨ 새로 추가: 테스트용 모니터링 데이터 전송 메서드
     */
    public void sendTestMonitoringData() {
        monitoringDataService.sendTestMonitoringData();
    }

    /**
     * ✨ 새로 추가: 서비스 상태 확인 (두 WebSocket 서비스 모두)
     */
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("notificationClients", getWebSocketClientCount());
        status.put("monitoringClients", getMonitoringClientCount());
        status.put("modelServiceUrl", modelServiceUrl);
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }

    /**
     * ✅ 기존: 배열에서 안전하게 값 추출 (그대로 유지)
     */
    private Float getValueAtIndex(List<Double> values, int index) {
        if (index < values.size()) {
            return values.get(index).floatValue();
        }
        return null; // 인덱스가 범위를 벗어나면 null
    }

    /**
     * ✅ 기존: 타임스탬프 파싱 (그대로 유지)
     */
    private java.util.Date parseTimestamp(String timestamp) {
        try {
            LocalDateTime ldt = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return java.sql.Timestamp.valueOf(ldt);
        } catch (Exception e) {
            return new java.util.Date(); // 파싱 실패시 현재 시간
        }
    }
}
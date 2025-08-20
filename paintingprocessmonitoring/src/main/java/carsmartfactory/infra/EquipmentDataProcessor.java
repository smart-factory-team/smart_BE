package carsmartfactory.infra;

import carsmartfactory.domain.PaintingProcessEquipmentDefectDetectionLog;
import carsmartfactory.domain.PaintingProcessEquipmentDefectDetectionLogRepository;
import carsmartfactory.domain.EquipmentDataReceived;
import carsmartfactory.infra.client.ModelPredictionRequest;
import carsmartfactory.infra.client.ModelPredictionResponse;
import carsmartfactory.infra.client.ModelServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class EquipmentDataProcessor {

    private final ModelServiceClient modelServiceClient;
    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;
    private final PaintingProcessEquipmentDefectDetectionLogRepository repository; // DB 저장을 위한 Repository

    /**
     * Kafka 토픽 A (equipment-data-topic)에서 메시지를 수신하여 처리합니다.
     * 흐름 3 & 4 & 5: 이벤트 소비 -> 모델 API 호출 -> 결과 이벤트를 Kafka 토픽 B로 발행
     */
    @Bean
    public Consumer<EquipmentDataReceived> processEquipmentData() {
        return event -> {
            log.info("=== Kafka 토픽 A (equipment-data-topic) 메시지 수신 ===");
            log.info("수신된 데이터: {}", event.toString());

            try {
                ModelPredictionRequest request = objectMapper.convertValue(event, ModelPredictionRequest.class);

                log.info("모델 서비스 API 호출 시작: /predict");
                ModelPredictionResponse response = modelServiceClient.predict(request);
                log.info("모델 서비스 API 호출 완료");

                // 모델이 204 No Content를 반환하면 response가 null이 될 수 있음
                if (response == null || response.getIssue() == null || response.getIssue().isEmpty()) {
                    log.info("모델 분석 결과: 특이사항 없음. 처리를 종료합니다.");
                    return;
                }

                log.info("모델 분석 결과: {}", response.toString());

                boolean sent = streamBridge.send(
                    "modelResultOut-out-0", // 토픽 B 바인딩
                    MessageBuilder.withPayload(response)
                        .setHeader("type", "ModelResultReceived")
                        .build()
                );

                if (sent) {
                    log.info("✅ Kafka 토픽 B로 모델 분석 결과 발행 성공");
                } else {
                    log.error("❌ Kafka 토픽 B로 모델 분석 결과 발행 실패");
                }

            } catch (FeignException e) {
                log.error("❌ 모델 서비스 호출 실패: status={}, response={}", e.status(), e.contentUTF8(), e);
            } catch (Exception e) {
                log.error("❌ 데이터 처리 중 예외 발생", e);
            }
        };
    }

    /**
     * Kafka 토픽 B (model-result-topic)에서 메시지를 수신하여 DB에 저장합니다.
     * 흐름 6: 이벤트 소비 -> DB 저장
     */
    @Bean
    public Consumer<ModelPredictionResponse> processModelResult() {
        return response -> {
            log.info("=== Kafka 토픽 B (model-result-topic) 메시지 수신 ===");
            log.info("DB에 저장할 데이터: {}", response.toString());

            try {
                // 1. 응답 데이터를 DB Entity로 변환
                PaintingProcessEquipmentDefectDetectionLog logEntity =
                    objectMapper.convertValue(response, PaintingProcessEquipmentDefectDetectionLog.class);

                // 2. Repository를 사용해 DB에 저장
                repository.save(logEntity);

                log.info("✅ 모델 분석 결과를 DB에 성공적으로 저장했습니다. (ID: {})", logEntity.getId());

            } catch (Exception e) {
                log.error("❌ DB 저장 중 예외 발생", e);
            }
        };
    }
}
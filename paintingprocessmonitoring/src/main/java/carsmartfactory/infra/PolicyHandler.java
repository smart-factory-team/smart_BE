package carsmartfactory.infra;

import carsmartfactory.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
// javax → jakarta 변경
import jakarta.transaction.Transactional;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    PaintingSurfaceDefectDetectionLogRepository paintingSurfaceDefectDetectionLogRepository;

    @Autowired
    PaintingProcessEquipmentDefectDetectionLogRepository paintingProcessEquipmentDefectDetectionLogRepository;

    /**
     * Kafka 'carsmartfactory' 토픽에서 들어오는 모든 이벤트 처리 Spring Cloud Stream 4.x 함수형 바인딩 방식 application.yml:
     * spring.cloud.stream.bindings.eventIn-in-0
     */
    // @Bean
    public Consumer<Message<String>> eventIn() {
        return message -> {
            try {
                String payload = message.getPayload();
                String eventType = (String) message.getHeaders().get("type"); // 'type' 헤더 사용

                System.out.println("\n\n##### Received Event: " + eventType + " #####");
                System.out.println("##### Payload: " + payload + " #####\n\n");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
                );

                // 이벤트 타입에 따른 분기 처리 (기존 로직 유지)
                if ("IssueSolved".equals(eventType)) {
                    handleIssueSolved(payload, objectMapper);
                } else {
                    System.out.println("##### Unknown event type: " + eventType + " #####");
                }

            } catch (Exception e) {
                System.err.println("##### Error processing event: " + e.getMessage() + " #####");
                e.printStackTrace();
            }
        };
    }

    /**
     * IssueSolved 이벤트 처리 (기존 두 개의 리스너 로직 통합)
     */
    private void handleIssueSolved(String payload, ObjectMapper objectMapper) {
        try {
            IssueSolved event = objectMapper.readValue(payload, IssueSolved.class);

            // Equipment 이슈 해결 정책
            System.out.println("\n\n##### listener EquipmentIssueSolvedPolicy : " + event + "\n\n");
            PaintingProcessEquipmentDefectDetectionLog.equipmentIssueSolvedPolicy(event);

            // Surface 이슈 해결 정책
            System.out.println("\n\n##### listener SurfaceIssueSolvedPolicy : " + event + "\n\n");
            PaintingSurfaceDefectDetectionLog.surfaceIssueSolvedPolicy(event);

        } catch (Exception e) {
            System.err.println("##### Error handling IssueSolved: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
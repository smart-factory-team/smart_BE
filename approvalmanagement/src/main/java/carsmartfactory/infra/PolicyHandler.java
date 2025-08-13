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
    UserApprovalRepository userApprovalRepository;

    /**
     * Kafka 'carsmartfactory' 토픽에서 들어오는 모든 이벤트 처리 Spring Cloud Stream 4.x 함수형 바인딩 방식 application.yml:
     * spring.cloud.stream.bindings.eventIn-in-0
     */
    @Bean
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

                // 이벤트 타입에 따른 분기 처리
                if ("UserRegistered".equals(eventType)) {
                    handleUserRegistered(payload, objectMapper);
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
     * 사용자 등록 이벤트 처리
     */
    private void handleUserRegistered(String payload, ObjectMapper objectMapper) {
        try {
            UserRegistered event = objectMapper.readValue(
                    payload,
                    UserRegistered.class
            );

            System.out.println("\n\n##### listener ApproveUserRegistration : " + event + "\n\n");

            // 비즈니스 로직 실행
            UserApproval.approveUserRegistration(event);

        } catch (Exception e) {
            System.err.println("##### Error handling UserRegistered: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
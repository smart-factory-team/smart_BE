package carsmartfactory.infra;

import carsmartfactory.domain.UserApproval;
import carsmartfactory.domain.UserApprovalRepository;
import carsmartfactory.domain.UserRegistered;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PolicyHandler {

    @Autowired
    UserApprovalRepository userApprovalRepository;

    /**
     * Kafka 'event-out' 토픽에서 들어오는 모든 이벤트 처리 application.yml: spring.cloud.stream.bindings.eventIn-in-0
     */
    @Bean
    public Consumer<Message<String>> eventIn() {
        System.out.println("##### [DEBUG] PolicyHandler eventIn() Bean created! #####");

        return message -> {
            try {
                System.out.println("\n\n===== [CRITICAL] MESSAGE RECEIVED!!! =====");
                System.out.println("Message received at: " + new java.util.Date());
                System.out.println("Raw message: " + message);

                String payload = message.getPayload();

                // 헤더 정보 모두 출력 (디버깅용)
                System.out.println("===== [ApprovalManagement] EVENT RECEIVED =====");
                System.out.println("Headers: " + message.getHeaders());
                System.out.println("Payload: " + payload);

                // 헤더에서 이벤트 타입 찾기 (여러 가능성 체크)
                String eventType = null;

                // 가능한 헤더 키들 체크
                if (message.getHeaders().get("type") != null) {
                    eventType = (String) message.getHeaders().get("type");
                } else if (message.getHeaders().get("eventType") != null) {
                    eventType = (String) message.getHeaders().get("eventType");
                } else if (message.getHeaders().get("messageType") != null) {
                    eventType = (String) message.getHeaders().get("messageType");
                }

                System.out.println("Detected EventType: " + eventType);
                System.out.println("==============================================\n");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
                );

                // 이벤트 타입에 따른 분기 처리
                if ("UserRegistered".equals(eventType)) {
                    handleUserRegistered(payload, objectMapper);
                } else if ("ApprovalRequestRegistered".equals(eventType)) {
                    System.out.println(
                            "##### [ApprovalManagement] Ignoring self-generated ApprovalRequestRegistered event #####");
                    // 자신이 생성한 이벤트는 무시
                } else {
                    System.out.println("##### [ApprovalManagement] Unknown event type: " + eventType + " #####");
                }

            } catch (Exception e) {
                System.err.println("##### [ApprovalManagement] Error processing event: " + e.getMessage() + " #####");
                e.printStackTrace();
            }
        };
    }

    /**
     * 사용자 등록 이벤트 처리
     */
    private void handleUserRegistered(String payload, ObjectMapper objectMapper) {
        try {
            UserRegistered event = objectMapper.readValue(payload, UserRegistered.class);

            System.out.println(
                    "\n\n##### [ApprovalManagement] Processing UserRegistered: " + event.getEmail() + " #####");

            // 중복 체크
            if (userApprovalRepository.existsByEmail(event.getEmail())) {
                System.out.println(
                        "##### [ApprovalManagement] User already exists in approval queue: " + event.getEmail()
                                + " #####");
                return;
            }

            // 비즈니스 로직 실행
            UserApproval.handleUserRegistration(event);

            System.out.println(
                    "##### [ApprovalManagement] UserRegistered event processed successfully: " + event.getEmail()
                            + " #####");

        } catch (Exception e) {
            System.err.println(
                    "##### [ApprovalManagement] Error handling UserRegistered: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }

    /**
     * 승인 처리 헬퍼 메서드 (컨트롤러에서 사용)
     */
    public void processApproval(String userApprovalId, String approvedBy, String reason) {
        try {
            userApprovalRepository.findById(userApprovalId)
                    .ifPresent(userApproval -> {
                        userApproval.approve(approvedBy, reason);
                        userApprovalRepository.save(userApproval);

                        System.out.println("##### [ApprovalManagement] User approved: " + userApproval.getEmail() +
                                " by " + approvedBy + " #####");
                    });

        } catch (Exception e) {
            System.err.println("##### [ApprovalManagement] Error processing approval: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }

    /**
     * 거절 처리 헬퍼 메서드 (컨트롤러에서 사용)
     */
    public void processRejection(String userApprovalId, String rejectedBy, String reason) {
        try {
            userApprovalRepository.findById(userApprovalId)
                    .ifPresent(userApproval -> {
                        userApproval.reject(rejectedBy, reason);
                        userApprovalRepository.save(userApproval);

                        System.out.println("##### [ApprovalManagement] User rejected: " + userApproval.getEmail() +
                                " by " + rejectedBy + " reason: " + reason + " #####");
                    });

        } catch (Exception e) {
            System.err.println("##### [ApprovalManagement] Error processing rejection: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }
}
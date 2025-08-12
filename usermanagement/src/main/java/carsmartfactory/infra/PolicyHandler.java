package carsmartfactory.infra;

import carsmartfactory.domain.User;
import carsmartfactory.domain.UserRegisterationRepository;
import carsmartfactory.domain.UserRegistrationApproved;
import carsmartfactory.domain.UserRegistrationRejected;
import carsmartfactory.domain.UserRepository;
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
    UserRepository userRepository;

    @Autowired
    UserRegisterationRepository userRegisterationRepository;

    /**
     * Kafka 'carsmartfactory' 토픽에서 들어오는 모든 이벤트 처리 application.yml: spring.cloud.stream.bindings.event-in
     */
    @Bean
    public Consumer<Message<String>> eventIn() {
        return message -> {
            try {
                String payload = message.getPayload();
                String eventType = (String) message.getHeaders().get("eventType");

                System.out.println("\n\n##### Received Event: " + eventType + " #####");
                System.out.println("##### Payload: " + payload + " #####\n\n");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
                );

                // 이벤트 타입에 따른 분기 처리
                if ("UserRegistrationApproved".equals(eventType)) {
                    handleUserRegistrationApproved(payload, objectMapper);
                } else if ("UserRegistrationRejected".equals(eventType)) {
                    handleUserRegistrationRejected(payload, objectMapper);
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
     * 사용자 등록 승인 이벤트 처리
     */
    private void handleUserRegistrationApproved(String payload, ObjectMapper objectMapper) {
        try {
            UserRegistrationApproved event = objectMapper.readValue(
                    payload,
                    UserRegistrationApproved.class
            );

            System.out.println("\n\n##### listener EnableUserAccount : " + event + "\n\n");

            // 비즈니스 로직 실행
            User.enableUserAccount(event);

        } catch (Exception e) {
            System.err.println("##### Error handling UserRegistrationApproved: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }

    /**
     * 사용자 등록 거부 이벤트 처리
     */
    private void handleUserRegistrationRejected(String payload, ObjectMapper objectMapper) {
        try {
            UserRegistrationRejected event = objectMapper.readValue(
                    payload,
                    UserRegistrationRejected.class
            );

            System.out.println("\n\n##### listener DisableUserAccount : " + event + "\n\n");

            // 비즈니스 로직 실행
            User.disableUseAccount(event);

        } catch (Exception e) {
            System.err.println("##### Error handling UserRegistrationRejected: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }

    /**
     * 승인 이벤트 직접 처리 (관리자가 수동 승인할 때 사용)
     */
    public void processUserApproval(String userRegistrationId, String approvedBy) {
        try {
            userRegisterationRepository.findById(userRegistrationId)
                    .ifPresent(registration -> {
                        // UserRegistration 업데이트
                        // registration.approve(approvedBy); // 승인 메서드가 있다면

                        // User 테이블로 데이터 이동
                        User newUser = new User();
                        newUser.setId(registration.getId());
                        newUser.setEmail(registration.getEmail());
                        newUser.setPassword(registration.getPassword());
                        newUser.setName(registration.getName());
                        newUser.setDepartment(registration.getDepartment());
                        newUser.setRole(registration.getRole());
                        newUser.setIsApproved(true);
                        newUser.setCreatedAt(registration.getCreatedAt());
                        newUser.setUpdatedAt(new java.util.Date());

                        userRepository.save(newUser);

                        System.out.println(
                                "##### User approved and moved to User table: " + newUser.getEmail() + " #####");
                    });

        } catch (Exception e) {
            System.err.println("##### Error processing user approval: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }

    /**
     * 거부 이벤트 직접 처리
     */
    public void processUserRejection(String userRegistrationId, String rejectedBy, String reason) {
        try {
            userRegisterationRepository.findById(userRegistrationId)
                    .ifPresent(registration -> {
                        // UserRegistration 상태 업데이트만 (User 테이블로 이동하지 않음)
                        // registration.reject(rejectedBy, reason); // 거부 메서드가 있다면

                        System.out.println("##### User registration rejected: " + registration.getEmail() +
                                " by " + rejectedBy + " reason: " + reason + " #####");
                    });

        } catch (Exception e) {
            System.err.println("##### Error processing user rejection: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }


}
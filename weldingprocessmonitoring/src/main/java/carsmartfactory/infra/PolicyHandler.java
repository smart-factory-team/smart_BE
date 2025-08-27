package carsmartfactory.infra;

import carsmartfactory.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    WeldingMachineDefectDetectionLogRepository weldingMachineDefectDetectionLogRepository;

    /**
     * Spring Cloud Stream 4.x 함수형 방식 application.yml의 eventIn과 연결됨
     */
    @Bean
    public Consumer<String> eventIn() {
        return this::handleEvent;
    }

    /**
     * 이벤트 처리 메서드
     */
    public void handleEvent(String eventString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // 먼저 기본 이벤트로 파싱해서 타입 확인
            Object event = mapper.readValue(eventString, Object.class);

            if (event instanceof java.util.Map) {
                java.util.Map<String, Object> eventMap = (java.util.Map<String, Object>) event;
                String eventType = (String) eventMap.get("eventType");

                if ("IssueSolved".equals(eventType)) {
                    IssueSolved issueSolved = mapper.readValue(eventString, IssueSolved.class);
                    wheneverIssueSolved_IssueSolvedPolicy(issueSolved);
                }
            }
        } catch (Exception e) {
            System.err.println("이벤트 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * IssueSolved 이벤트 처리
     */
    public void wheneverIssueSolved_IssueSolvedPolicy(IssueSolved issueSolved) {
        System.out.println("\n\n##### listener IssueSolvedPolicy : " + issueSolved + "\n\n");

        // Sample Logic //
        WeldingMachineDefectDetectionLog.issueSolvedPolicy(issueSolved);
    }
}
//>>> Clean Arch / Inbound Adaptor
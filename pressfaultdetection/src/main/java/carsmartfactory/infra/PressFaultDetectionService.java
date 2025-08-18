package carsmartfactory.infra;

import carsmartfactory.domain.PressFaultDataReceivedEvent;
import carsmartfactory.domain.PressFaultDetectionLog;
import carsmartfactory.domain.PressFaultDetectionLogRepository;
import carsmartfactory.infra.dto.PressFaultDataDto;
import carsmartfactory.infra.dto.PressFaultPredictionResponseDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PressFaultDetectionService {

    @Autowired
    private PressFaultModelClient modelClient;

    @Autowired
    private PressFaultDetectionLogRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @EventListener
    @Transactional
    public void handlePressFaultDataReceived(PressFaultDataReceivedEvent event) {
        
        System.out.println("=== 1단계: 이벤트 수신됨 ===");
        System.out.println("데이터 소스: " + event.getSource());
        System.out.println("데이터 길이: " + event.getData_length());
        
        // FastAPI 호출을 위한 DTO 구성
        PressFaultDataDto requestData = new PressFaultDataDto();
        requestData.setAI0_Vibration(event.getAI0_Vibration());
        requestData.setAI1_Vibration(event.getAI1_Vibration());
        requestData.setAI2_Current(event.getAI2_Current());
        requestData.setTimestamp(event.getDataTimestamp());
        requestData.setSource(event.getSource());
        requestData.setData_length(event.getData_length());

        // FastAPI 모델 서비스 호출
        PressFaultPredictionResponseDto prediction = modelClient.predict(requestData);
        
        // 2단계: 고장 여부 판단 및 DB 저장
        if (prediction != null && prediction.getIs_fault()) {
            System.out.println("=== 고장 감지: PressFaultDetectionLog 엔티티 저장 ===");
            
            PressFaultDetectionLog logEntity = new PressFaultDetectionLog();
            
            try {
                // 엔티티 데이터 채우기
                logEntity.setTimeStamp(new Date()); // 현재 시간
                
                // List<Double> to JSON String
                logEntity.setAi0Vibration(objectMapper.writeValueAsString(event.getAI0_Vibration()));
                logEntity.setAi1Vibration(objectMapper.writeValueAsString(event.getAI1_Vibration()));
                logEntity.setAi2Current(objectMapper.writeValueAsString(event.getAI2_Current()));
                
                logEntity.setMachineId(1L);
                // attribute_errors 분석해서 issue 형식 생성
                String issueCode = generateIssueCode(prediction.getAttribute_errors());
                logEntity.setIssue(issueCode);
                logEntity.setIsSolved(false); // 초기 상태

                // 리포지토리를 통해 엔티티 저장 -> @PostPersist가 이벤트 발행을 트리거
                repository.save(logEntity);
                System.out.println("=== DB 저장 및 이벤트 발행 완료 ===");

            } catch (JsonProcessingException e) {
                System.err.println("=== JSON 변환 실패 ===");
                e.printStackTrace();
            }

        } else if (prediction == null) {
            System.out.println("=== FastAPI 호출 실패 또는 응답 없음 ===");
        } else {
            System.out.println("=== 정상 상태 ===");
        }
    }

    private String generateIssueCode(Map<String, Double> attributeErrors) {
        
        // 현재 시간을 DATETIME 형식으로 생성
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String datetime = dateFormat.format(new Date());
        
        if (attributeErrors == null || attributeErrors.isEmpty()) {
            return "PRESS-COMPLEX-ANOMALY-" + datetime;
        }
        
        // 가장 큰 에러 값을 가진 속성 찾기
        String maxErrorAttribute = null;
        double maxError = -1.0;
        
        for (Map.Entry<String, Double> entry : attributeErrors.entrySet()) {
            if (entry.getValue() > maxError) {
                maxError = entry.getValue();
                maxErrorAttribute = entry.getKey();
            }
        }
        
        System.out.println("=== 최대 에러 속성: " + maxErrorAttribute + " (값: " + maxError + ") ===");
        
        // 속성명에 따라 분류
        if (maxErrorAttribute != null) {
            if (maxErrorAttribute.toLowerCase().contains("ai0") || 
                maxErrorAttribute.toLowerCase().contains("vib") && maxErrorAttribute.contains("0")) {
                return "PRESS-VIB1-ANOMALY-" + datetime;
            } else if (maxErrorAttribute.toLowerCase().contains("ai1") || 
                       maxErrorAttribute.toLowerCase().contains("vib") && maxErrorAttribute.contains("1")) {
                return "PRESS-VIB2-ANOMALY-" + datetime;
            } else if (maxErrorAttribute.toLowerCase().contains("ai2") || 
                       maxErrorAttribute.toLowerCase().contains("cur")) {
                return "PRESS-CUR-ANOMALY-" + datetime;
            }
        }
        
        // 분류되지 않은 경우 복합 이상
        return "PRESS-COMPLEX-ANOMALY-" + datetime;
    }
}
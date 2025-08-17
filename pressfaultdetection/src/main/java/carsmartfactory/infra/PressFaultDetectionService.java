package carsmartfactory.infra;

import carsmartfactory.domain.PressFaultDataReceivedEvent;
import carsmartfactory.infra.dto.PressFaultDataDto;
import carsmartfactory.infra.dto.PressFaultPredictionResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PressFaultDetectionService {

    @Autowired
    private PressFaultModelClient modelClient;

    @EventListener
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
        try {
            PressFaultPredictionResponseDto prediction = modelClient.predict(requestData);
            
            System.out.println("=== FastAPI 호출 결과 ===");
            System.out.println("예측 결과: " + prediction.getPrediction());
            System.out.println("고장 여부: " + prediction.getIs_fault());
            System.out.println("재구성 오차: " + prediction.getReconstruction_error());
            System.out.println("속성별 오차: " + prediction.getAttribute_errors());
            
            // TODO: 2단계에서 고장일 경우 이벤트 발행 추가 예정
            
        } catch (Exception e) {
            System.out.println("=== FastAPI 호출 실패 ===");
            e.printStackTrace();
        }
    }
}
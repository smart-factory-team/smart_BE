package carsmartfactory.infra;

import carsmartfactory.domain.PressFaultDataReceivedEvent;
import carsmartfactory.infra.dto.PressFaultPredictionResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FrontendNotificationService {

    @Autowired
    private PressFaultDetectionController controller;

    public void sendFaultStatus(PressFaultPredictionResponseDto prediction, PressFaultDataReceivedEvent event) {
        
        try {
            // 프론트엔드로 보낼 데이터 구성
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("isFault", prediction.getIs_fault());
            statusData.put("prediction", prediction.getPrediction());
            
            // SSE로 모든 연결된 클라이언트에게 전송
            controller.broadcastFaultStatus(statusData);
            
            System.out.println("=== SSE 상태 전송 완료: " + statusData.get("status") + " ===");
            
        } catch (Exception e) {
            System.out.println("=== SSE 상태 전송 실패: " + e.getMessage() + " ===");
        }
    }
}
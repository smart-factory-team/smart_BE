package carsmartfactory.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carsmartfactory.domain.PressFaultDataReceivedEvent;
import carsmartfactory.infra.dto.PressFaultDataDto;

@RestController
@RequestMapping("/pressFaultDetectionLogs")
public class PressFaultDetectionController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/data")
    public ResponseEntity<String> receivePressFaultData(@RequestBody PressFaultDataDto data) {
        
        // 데이터 수신 이벤트 생성
        PressFaultDataReceivedEvent event = new PressFaultDataReceivedEvent();
        event.setAI0_Vibration(data.getAI0_Vibration());
        event.setAI1_Vibration(data.getAI1_Vibration());
        event.setAI2_Current(data.getAI2_Current());
        event.setDataTimestamp(data.getTimestamp());
        event.setSource(data.getSource());
        event.setData_length(data.getData_length());
        
        // Spring 내부 이벤트로 발행 (EventListener가 받을 수 있도록)
        eventPublisher.publishEvent(event);
        
        // Kafka로도 이벤트 발행 (기존 방식 유지)
        event.publish();
        
        return ResponseEntity.ok("Press fault data received successfully");
    }
}

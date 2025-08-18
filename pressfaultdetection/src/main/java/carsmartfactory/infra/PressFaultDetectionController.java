package carsmartfactory.infra;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import carsmartfactory.domain.PressFaultDataReceivedEvent;
import carsmartfactory.infra.dto.PressFaultDataDto;

@RestController
@RequestMapping("/pressFaultDetectionLogs")
public class PressFaultDetectionController {
    // SSE 연결된 클라이언트들 저장
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

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

    @GetMapping(value = "/status/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamFaultStatus() {
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        // 새 클라이언트 추가
        emitters.add(emitter);
        
        // 연결 종료 시 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));
        
        try {
            // 연결 확인 메시지
            emitter.send(SseEmitter.event()
                .name("connected")
                .data("SSE connected"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        
        return emitter;
    }

    // 모든 연결된 클라이언트에게 상태 전송
    public void broadcastFaultStatus(Object statusData) {
        
        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("faultStatus")
                    .data(statusData));
                return false;
            } catch (IOException e) {
                System.out.println("=== SSE 전송 실패, 클라이언트 제거 ===");
                return true;
            }
        });
        
        System.out.println("=== SSE로 " + emitters.size() + "개 클라이언트에 상태 전송 ===");
    }
}

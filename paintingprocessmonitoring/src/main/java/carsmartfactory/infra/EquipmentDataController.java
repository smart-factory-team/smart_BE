package carsmartfactory.infra;

import carsmartfactory.domain.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/equipment-data")
@Slf4j
public class EquipmentDataController {

    @Autowired
    private StreamBridge streamBridge;

    /**
     * 시뮬레이터로부터 장비 데이터 수신
     * 플로우: 시뮬레이터 -> Spring Boot (이 API) -> Kafka 토픽 A
     */
    @PostMapping
    public ResponseEntity<String> receiveEquipmentData(@Valid @RequestBody EquipmentDataRequest request) {
        try {
            log.info("=== 장비 데이터 수신 ===");
            log.info("Machine ID: {}", request.getMachineId());
            log.info("Timestamp: {}", request.getTimeStamp());
            log.info("Thickness: {}, Voltage: {}, Current: {}, Temperature: {}", 
                    request.getThick(), request.getVoltage(), request.getCurrent(), request.getTemper());

            // 1. 이벤트 생성 (Kafka 토픽 A로 발행할 데이터)
            EquipmentDataReceived event = new EquipmentDataReceived();
            event.setMachineId(request.getMachineId());
            event.setTimeStamp(request.getTimeStamp());
            event.setThick(request.getThick());
            event.setVoltage(request.getVoltage());
            event.setCurrent(request.getCurrent());
            event.setTemper(request.getTemper());
            event.setIssue(request.getIssue());
            event.setIsSolved(request.getIsSolved());

            // 2. Kafka 토픽 A로 이벤트 발행
            boolean sent = streamBridge.send(
                "equipmentDataOut-out-0", // 토픽 A 바인딩
                MessageBuilder
                    .withPayload(event)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .setHeader("type", "EquipmentDataReceived")
                    .build()
            );

            if (sent) {
                log.info("✅ Kafka 토픽 A로 이벤트 발행 성공");
                return ResponseEntity.ok("장비 데이터가 성공적으로 수신되어 처리 대기열에 추가되었습니다.");
            } else {
                log.error("❌ Kafka 토픽 A로 이벤트 발행 실패");
                return ResponseEntity.internalServerError().body("데이터 처리 중 오류가 발생했습니다.");
            }

        } catch (Exception e) {
            log.error("❌ 장비 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 시뮬레이터 데이터 요청 DTO
     */
    @Data
    public static class EquipmentDataRequest {
        private String machineId;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timeStamp;
        
        private Float thick;
        private Float voltage;
        private Float current;
        private Float temper;
        private String issue = "";
        private Boolean isSolved = false;
    }
}
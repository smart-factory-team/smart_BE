package carsmartfactory.infra;

import carsmartfactory.domain.*;
import carsmartfactory.dto.SensorDataRequest;
import carsmartfactory.dto.ApiResponse;
import carsmartfactory.services.WeldingDataService;

import java.util.Optional;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value = "/weldingMachineDefectDetectionLogs")
@Transactional
public class WeldingMachineDefectDetectionLogController {

    @Autowired
    WeldingMachineDefectDetectionLogRepository weldingMachineDefectDetectionLogRepository;

    @Autowired
    WeldingDataService weldingDataService;

    // ===== GET 메서드들 (조회 기능) =====

    /**
     * ✅ 모든 용접 결함 로그 조회 GET /weldingMachineDefectDetectionLogs
     */
    @GetMapping
    public ResponseEntity<List<WeldingMachineDefectDetectionLog>> getAllLogs() {
        try {
            List<WeldingMachineDefectDetectionLog> logs = weldingMachineDefectDetectionLogRepository.findAll();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ✅ 특정 ID로 용접 결함 로그 조회 GET /weldingMachineDefectDetectionLogs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WeldingMachineDefectDetectionLog> getLogById(@PathVariable String id) {
        try {
            Optional<WeldingMachineDefectDetectionLog> log = weldingMachineDefectDetectionLogRepository.findById(id);

            if (log.isPresent()) {
                return ResponseEntity.ok(log.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ✅ 최근 이상 데이터 조회 (제한된 개수) GET /weldingMachineDefectDetectionLogs/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<WeldingMachineDefectDetectionLog>> getRecentLogs(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<WeldingMachineDefectDetectionLog> allLogs = weldingMachineDefectDetectionLogRepository.findAll();

            // 최근 데이터 제한 (임시로 처음 N개, 실제로는 시간순 정렬 필요)
            List<WeldingMachineDefectDetectionLog> recentLogs = allLogs.stream()
                    .limit(limit)
                    .toList();

            return ResponseEntity.ok(recentLogs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ✅ 저장된 로그 개수 조회 GET /weldingMachineDefectDetectionLogs/count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getLogCount() {
        try {
            long count = weldingMachineDefectDetectionLogRepository.count();
            return ResponseEntity.ok(
                    ApiResponse.success("로그 개수 조회 성공", count)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("로그 개수 조회 실패: " + e.getMessage(), "COUNT_ERROR")
            );
        }
    }

    // ===== POST 메서드들 (기존 기능) =====

    /**
     * 시뮬레이터로부터 센서 데이터 수신 및 처리 POST /weldingMachineDefectDetectionLogs
     */
    @PostMapping
    public ResponseEntity<ApiResponse<WeldingMachineDefectDetectionLog>> processSensorData(
            @Valid @RequestBody SensorDataRequest request) {

        try {
            // 서비스에서 전체 플로우 처리 (모델 호출 + DB 저장 + 이벤트 발행)
            WeldingMachineDefectDetectionLog result = weldingDataService.processSensorData(request);

            return ResponseEntity.ok(
                    ApiResponse.success("센서 데이터 처리 완료", result)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("센서 데이터 처리 실패: " + e.getMessage(), "PROCESSING_ERROR")
            );
        }
    }

    /**
     * 기존 MSAez 스타일 API (호환성 유지) POST /weldingMachineDefectDetectionLogs/save
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<WeldingMachineDefectDetectionLog>> saveDefectLog(
            @RequestParam Long machineId,
            @RequestParam String issue) {

        try {
            WeldingMachineDefectDetectionLog log = new WeldingMachineDefectDetectionLog();
            log.setMachineId(machineId);
            log.setIssue(issue);
            log.setIsSolved(false);
            log.setTimeStamp(new java.util.Date());

            WeldingMachineDefectDetectionLog saved = weldingMachineDefectDetectionLogRepository.save(log);

            return ResponseEntity.ok(
                    ApiResponse.success("결함 로그 저장 완료", saved)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("결함 로그 저장 실패: " + e.getMessage(), "SAVE_ERROR")
            );
        }
    }

    // ===== 기타 유틸리티 메서드들 =====

    /**
     * ✅ 특정 로그 삭제 (관리용) DELETE /weldingMachineDefectDetectionLogs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLog(@PathVariable String id) {
        try {
            if (weldingMachineDefectDetectionLogRepository.existsById(id)) {
                weldingMachineDefectDetectionLogRepository.deleteById(id);
                return ResponseEntity.ok(
                        ApiResponse.success("로그 삭제 완료", id)
                );
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("로그 삭제 실패: " + e.getMessage(), "DELETE_ERROR")
            );
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
package carsmartfactory.infra;

import carsmartfactory.domain.*;
import carsmartfactory.domain.DefectDetectionResult;
import carsmartfactory.domain.PaintingSurfaceDefectDetectionService;
import carsmartfactory.domain.ImageReceivedEvent;
import carsmartfactory.domain.DefectDetectedEvent;
import carsmartfactory.infra.dto.PaintingSurfacePredictionResponseDto;
import java.util.Optional;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// javax → jakarta 패키지 변경
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.ArrayList;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/api/painting-surface")
@Transactional
public class PaintingSurfaceDefectDetectionLogController {

    @Autowired
    PaintingSurfaceDefectDetectionLogRepository paintingSurfaceDefectDetectionLogRepository;
    
    @Autowired
    PaintingSurfaceDefectDetectionService defectDetectionService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private PaintingSurfaceModelClient modelClient;
    
    // AI 모델 서비스 URL (포트 8002)
    @Value("${painting.surface.model.service.url:http://localhost:8002}")
    private String modelServiceUrl;
    
    // 시뮬레이터 서비스 URL (포트 8012)
    @Value("${painting.surface.simulator.service.url:http://localhost:8012}")
    private String simulatorServiceUrl;
    
    // CORS는 전역 설정으로 처리됨

    /**
     * 시뮬레이터로부터 이미지를 받아서 결함 감지 수행
     * @param image 업로드된 이미지 파일
     * @return 결함 감지 결과
     */
    @PostMapping("/defect-detection")
    public ResponseEntity<DefectDetectionResult> detectDefect(
            @RequestParam("image") MultipartFile image) {
        try {
            System.out.println("📥 시뮬레이터로부터 이미지 수신: " + image.getOriginalFilename());
            
            // 1. AI 모델 서비스를 직접 호출하여 결함 감지 수행
            PaintingSurfacePredictionResponseDto prediction = modelClient.predict(image, 0.5f);
            
            // 컨트롤러에서 AI 모델 응답 상세 로깅
            System.out.println("=== 컨트롤러 AI 모델 응답 처리 ===");
            System.out.println("prediction 객체: " + (prediction != null ? "존재" : "null"));
            System.out.println("predictions 리스트: " + (prediction != null && prediction.getPredictions() != null ? prediction.getPredictions() : "null"));
            System.out.println("predictions 크기: " + (prediction != null && prediction.getPredictions() != null ? prediction.getPredictions().size() : "N/A"));
            System.out.println("predictions 비어있음: " + (prediction != null && prediction.getPredictions() != null ? prediction.getPredictions().isEmpty() : "N/A"));
            
            if (prediction != null && prediction.getPredictions() != null && !prediction.getPredictions().isEmpty()) {
                // 결함 감지됨
                System.out.println("🚨 결함 감지됨: " + prediction.getPredictions().size() + "개 결함 발견");
                
                // 모든 결함 정보를 포함한 응답 생성
                StringBuilder defectMessage = new StringBuilder();
                defectMessage.append("결함 감지됨: ");
                
                for (int i = 0; i < prediction.getPredictions().size(); i++) {
                    Map<String, Object> defect = prediction.getPredictions().get(i);
                    String defectType = (String) defect.get("class_name");
                    Double confidence = (Double) defect.get("confidence");
                    
                    if (i > 0) defectMessage.append(", ");
                    defectMessage.append(defectType).append(" (신뢰도: ").append(String.format("%.3f", confidence)).append(")");
                }
                
                // 첫 번째 결함 정보로 기본 응답 생성 (하지만 메시지에는 모든 결함 포함)
                Map<String, Object> firstDefect = prediction.getPredictions().get(0);
                String defectType = (String) firstDefect.get("class_name");
                Double confidence = (Double) firstDefect.get("confidence");
                
                DefectDetectionResult result = new DefectDetectionResult(
                    "defect", 
                    defectType, 
                    confidence, 
                    defectMessage.toString()
                );
                
                // 2. 별도로 이벤트 발행하여 DB 저장 처리 (비동기)
                ImageReceivedEvent imageReceivedEvent = new ImageReceivedEvent(image, prediction);
                eventPublisher.publishEvent(imageReceivedEvent);
                System.out.println("📤 DB 저장을 위한 이벤트 발행 (AI 모델 결과 포함)");
                
                return ResponseEntity.ok(result);
                
            } else {
                // 정상 상태
                System.out.println("✅ 정상 상태: 결함 없음");
                
                DefectDetectionResult result = new DefectDetectionResult(
                    "normal", 
                    null, 
                    1.0, 
                    "정상 상태 - 결함 없음"
                );
                
                return ResponseEntity.ok(result);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 결함 감지 API 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DefectDetectionResult("error", e.getMessage()));
        }
    }
    
    /**
     * AI 모델 서비스 헬스 체크 (프록시)
     * @return 모델 서비스 상태
     */
    @GetMapping("/defect-detection/model-health")
    public ResponseEntity<?> getModelServiceHealth() {
        try {
            System.out.println("🔍 AI 모델 서비스 헬스 체크 요청");
            
            String healthUrl = modelServiceUrl + "/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            
            System.out.println("✅ AI 모델 서비스 헬스 체크 성공: " + response.getStatusCode());
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            System.err.println("❌ AI 모델 서비스 헬스 체크 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "unhealthy");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * 시뮬레이터 서비스 상태 조회 (프록시)
     * @return 시뮬레이터 상태
     */
    @GetMapping("/defect-detection/simulator-status")
    public ResponseEntity<?> getSimulatorStatus() {
        try {
            System.out.println("🔍 시뮬레이터 서비스 상태 조회 요청");
            
            String statusUrl = simulatorServiceUrl + "/simulator/status";
            ResponseEntity<Map> response = restTemplate.getForEntity(statusUrl, Map.class);
            
            System.out.println("✅ 시뮬레이터 상태 조회 성공: " + response.getStatusCode());
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            System.err.println("❌ 시뮬레이터 상태 조회 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("is_running", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * 시뮬레이터 시작 (프록시)
     * @return 시뮬레이터 시작 결과
     */
    @PostMapping("/defect-detection/simulator/start")
    public ResponseEntity<?> startSimulator() {
        try {
            System.out.println("🚀 시뮬레이터 시작 요청");
            
            String startUrl = simulatorServiceUrl + "/simulator/start";
            ResponseEntity<Map> response = restTemplate.postForEntity(startUrl, null, Map.class);
            
            System.out.println("✅ 시뮬레이터 시작 성공: " + response.getStatusCode());
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            System.err.println("❌ 시뮬레이터 시작 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 기계별 결함 통계 조회
     * @return 기계별 결함 통계
     */
    @GetMapping("/defect-detection/machine-statistics")
    public ResponseEntity<?> getMachineStatistics() {
        try {
            System.out.println("📊 기계별 도장 표면 결함 통계 조회 요청");
            
            // DB에서 모든 결함 로그 조회
            List<PaintingSurfaceDefectDetectionLog> logs = new ArrayList<>();
            paintingSurfaceDefectDetectionLogRepository.findAll().forEach(logs::add);
            
            // 기계별 그룹핑
            Map<String, List<PaintingSurfaceDefectDetectionLog>> machineGroups = logs.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    log -> log.getMachineName() != null ? log.getMachineName() : "알 수 없음"
                ));
            
            // 기계별 통계 계산
            List<Map<String, Object>> machineStats = new ArrayList<>();
            for (Map.Entry<String, List<PaintingSurfaceDefectDetectionLog>> entry : machineGroups.entrySet()) {
                String machineName = entry.getKey();
                List<PaintingSurfaceDefectDetectionLog> machineLogs = entry.getValue();
                
                // 결함 유형별 카운트
                Map<String, Long> defectTypeCount = machineLogs.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                        log -> log.getDefectType() != null ? log.getDefectType() : "기타",
                        java.util.stream.Collectors.counting()
                    ));
                
                Map<String, Object> stat = new HashMap<>();
                stat.put("machineName", machineName);
                stat.put("totalDefects", machineLogs.size());
                stat.put("defectTypes", defectTypeCount);
                
                // 최근 결함 발생 시간
                if (!machineLogs.isEmpty()) {
                    Date latestTime = machineLogs.stream()
                        .map(PaintingSurfaceDefectDetectionLog::getTimeStamp)
                        .filter(Objects::nonNull)
                        .max(Date::compareTo)
                        .orElse(null);
                    stat.put("lastDefectTime", latestTime);
                }
                
                machineStats.add(stat);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("machines", machineStats);
            response.put("totalMachines", machineStats.size());
            response.put("lastUpdated", new Date());
            
            System.out.println("✅ 기계별 통계 조회 성공: " + machineStats.size() + "개 기계");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 기계별 통계 조회 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "기계별 통계 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 시뮬레이터 중지 (프록시)
     * @return 시뮬레이터 중지 결과
     */
    @PostMapping("/defect-detection/simulator/stop")
    public ResponseEntity<?> stopSimulator() {
        try {
            System.out.println("🛑 시뮬레이터 중지 요청");
            
            String stopUrl = simulatorServiceUrl + "/simulator/stop";
            ResponseEntity<Map> response = restTemplate.postForEntity(stopUrl, null, Map.class);
            
            System.out.println("✅ 시뮬레이터 중지 성공: " + response.getStatusCode());
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            System.err.println("❌ 시뮬레이터 중지 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 도장 표면 결함 탐지 결과 조회 (프론트엔드용)
     * @param limit 조회할 결과 수
     * @param sort 정렬 기준
     * @return 결함 탐지 결과 목록
     */
    @GetMapping("/defect-detection-logs")
    public ResponseEntity<?> getDefectDetectionResults(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "timestamp,desc") String sort) {
        try {
            System.out.println("📋 도장 표면 결함 탐지 결과 조회 요청: limit=" + limit + ", sort=" + sort);
            
            // DB에서 결함 로그 조회
            List<PaintingSurfaceDefectDetectionLog> logs = new ArrayList<>();
            paintingSurfaceDefectDetectionLogRepository.findAll().forEach(logs::add);
            
            // 정렬 처리
            if (sort.contains("timestamp")) {
                if (sort.contains("desc")) {
                    logs.sort((a, b) -> {
                        if (a.getTimeStamp() == null) return 1;
                        if (b.getTimeStamp() == null) return -1;
                        return b.getTimeStamp().compareTo(a.getTimeStamp());
                    });
                } else {
                    logs.sort((a, b) -> {
                        if (a.getTimeStamp() == null) return -1;
                        if (b.getTimeStamp() == null) return 1;
                        return a.getTimeStamp().compareTo(b.getTimeStamp());
                    });
                }
            }
            
            // limit 적용
            if (limit > 0 && limit < logs.size()) {
                logs = logs.subList(0, limit);
            }
            
            // 프론트엔드가 기대하는 형태로 변환
            List<Map<String, Object>> frontendResults = new ArrayList<>();
            for (PaintingSurfaceDefectDetectionLog log : logs) {
                Map<String, Object> result = new HashMap<>();
                
                // 결함이 감지된 경우에만 DB에 저장되므로 status는 항상 'defect'
                result.put("status", "defect");
                result.put("machineName", log.getMachineName()); // 기계명 추가
                result.put("defectType", log.getDefectType());
                result.put("confidence", log.getPressTime() != null ? log.getPressTime() : 0.0);
                result.put("message", log.getIssue());
                result.put("timestamp", log.getTimeStamp());
                
                // 결함 위치 및 크기 정보
                result.put("defectX", log.getDefectX());
                result.put("defectY", log.getDefectY());
                result.put("defectWidth", log.getDefectWidth());
                result.put("defectHeight", log.getDefectHeight());
                result.put("defectArea", log.getDefectArea());
                result.put("defectBbox", log.getDefectBbox());
                result.put("defectIndex", log.getDefectIndex());
                
                frontendResults.add(result);
            }
            
            System.out.println("✅ 결함 탐지 결과 조회 성공: " + frontendResults.size() + "개 결과");
            return ResponseEntity.ok(frontendResults);
            
        } catch (Exception e) {
            System.err.println("❌ 결함 탐지 결과 조회 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 도장 표면 결함 통계 조회
     * @param timeRange 시간 범위 (24h, 7d, 30d)
     * @return 결함 통계 정보
     */
    @GetMapping("/defect-detection/statistics")
    public ResponseEntity<?> getDefectStatistics(@RequestParam(defaultValue = "24h") String timeRange) {
        try {
            System.out.println("📊 도장 표면 결함 통계 조회 요청: " + timeRange);
            
            // 시간 범위에 따른 시작 시간 계산
            final long currentTime = System.currentTimeMillis();
            final long startTime;
            
            switch (timeRange) {
                case "24h":
                    startTime = currentTime - (24 * 60 * 60 * 1000L);
                    break;
                case "7d":
                    startTime = currentTime - (7 * 24 * 60 * 60 * 1000L);
                    break;
                case "30d":
                    startTime = currentTime - (30L * 24 * 60 * 60 * 1000L);
                    break;
                default:
                    startTime = currentTime - (24 * 60 * 60 * 1000L); // 기본값: 24시간
            }
            
            // DB에서 해당 기간의 결함 로그 조회
            List<PaintingSurfaceDefectDetectionLog> logs = new ArrayList<>();
            paintingSurfaceDefectDetectionLogRepository.findAll().forEach(logs::add);
            
            // 시간 필터링
            logs = logs.stream()
                .filter(log -> log.getTimeStamp() != null && log.getTimeStamp().getTime() >= startTime)
                .collect(Collectors.toList());
            
            // 통계 계산
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("timeRange", timeRange);
            
            // DB에 저장된 로그는 모두 결함이 감지된 이미지
            int defectImageCount = logs.size(); // 결함이 있는 이미지 개수
            
            // 누적 통계 계산
            int totalImages = this.cumulativeTotalImages; // 누적 총 이미지 수
            
            statistics.put("totalCount", totalImages); // 총 처리된 이미지 (누적)
            statistics.put("defectImageCount", defectImageCount); // 결함 감지된 이미지 (누적)
            
            // 결함 비율 계산 (백분율로, 누적 기준)
            double defectRatio = totalImages > 0 ? (double) defectImageCount / totalImages * 100 : 0.0;
            statistics.put("defectRatio", defectRatio); // 결함 비율 (%)
            
            // 추가 누적 정보
            statistics.put("currentBatchImages", this.currentBatchTotalImages); // 현재 배치 이미지 수
            
            // 정상 이미지는 DB에 저장되지 않으므로 정확한 정상 비율 계산 불가능
            statistics.put("note", "정상 이미지는 DB에 저장되지 않아 정확한 정상 비율 계산 불가능");
            
            // 추가 정보
            statistics.put("note", "현재 시스템에서는 결함이 감지된 이미지만 DB에 저장됩니다.");
            statistics.put("systemInfo", "정확한 통계를 위해서는 시뮬레이터에서 총 이미지 수를 전송받아야 합니다.");
            
            // 결함 유형별 통계
            Map<String, Long> defectTypeCounts = logs.stream()
                .filter(log -> log.getDefectType() != null)
                .collect(Collectors.groupingBy(
                    PaintingSurfaceDefectDetectionLog::getDefectType,
                    Collectors.counting()
                ));
            statistics.put("defectTypeBreakdown", defectTypeCounts);
            
            // 이미지별 결함 개수 통계
            Map<String, Long> imageDefectCounts = logs.stream()
                .filter(log -> log.getItemNo() != null)
                .collect(Collectors.groupingBy(
                    PaintingSurfaceDefectDetectionLog::getItemNo,
                    Collectors.counting()
                ));
            statistics.put("imageDefectCounts", imageDefectCounts);
            
            // 총 결함 개수 (이미지당 여러 결함이 있을 수 있음)
            // 현재는 이미지당 1개씩만 저장되므로 logs.size()와 동일
            long totalDefectCount = logs.size();
            statistics.put("totalDefectCount", totalDefectCount);
            
            // 해결 여부별 통계
            long solvedCount = logs.stream()
                .filter(log -> log.getIsSolved() != null && log.getIsSolved())
                .count();
            long unsolvedCount = logs.size() - solvedCount;
            
            statistics.put("solvedDefects", solvedCount);
            statistics.put("unsolvedDefects", unsolvedCount);
            statistics.put("resolutionRate", logs.size() > 0 ? (double) solvedCount / logs.size() : 0.0);
            
            // 평균 신뢰도 (AI 모델 결과)
            double avgConfidence = logs.stream()
                .filter(log -> log.getPressTime() != null)
                .mapToDouble(log -> log.getPressTime())
                .average()
                .orElse(0.0);
            statistics.put("averageConfidence", avgConfidence);
            
            // 최근 업데이트 시간
            Date lastUpdated = logs.stream()
                .filter(log -> log.getTimeStamp() != null)
                .map(PaintingSurfaceDefectDetectionLog::getTimeStamp)
                .max(Date::compareTo)
                .orElse(new Date());
            statistics.put("lastUpdated", lastUpdated.getTime());
            
            System.out.println("✅ 통계 조회 성공: " + logs.size() + "개 결함 로그 분석");
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            System.err.println("❌ 통계 조회 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Azure Storage 연결 테스트 (프록시)
     * @return Azure Storage 연결 상태
     */
    @PostMapping("/defect-detection/test/azure-storage")
    public ResponseEntity<?> testAzureStorageConnection() {
        try {
            System.out.println("🔍 Azure Storage 연결 테스트 요청");
            
            String testUrl = simulatorServiceUrl + "/test/azure-storage-connection";
            ResponseEntity<Map> response = restTemplate.postForEntity(testUrl, null, Map.class);
            
            System.out.println("✅ Azure Storage 연결 테스트 성공: " + response.getStatusCode());
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            System.err.println("❌ Azure Storage 연결 테스트 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failed");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * 모델 서비스 연결 테스트 (프록시)
     * @return 모델 서비스 연결 상태
     */
    @PostMapping("/defect-detection/test/model-service")
    public ResponseEntity<?> testModelServiceConnection() {
        try {
            System.out.println("🔍 모델 서비스 연결 테스트 요청");
            
            String testUrl = simulatorServiceUrl + "/test/models-connection";
            ResponseEntity<Map> response = restTemplate.postForEntity(testUrl, null, Map.class);
            
            System.out.println("✅ 모델 서비스 연결 테스트 성공: " + response.getStatusCode());
            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            System.err.println("❌ 모델 서비스 연결 테스트 실패: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failed");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    // 배치 정보를 저장할 변수 (실제로는 Redis나 DB에 저장)
    private volatile Integer currentBatchTotalImages = 10; // 기본값
    private volatile Integer cumulativeTotalImages = 0; // 누적 총 이미지 수

    /**
     * 배치 시작 정보 수신
     * @param batchInfo 배치 시작 정보 (totalImages, batchStartTime)
     * @return 배치 시작 결과
     */
    @PostMapping("/defect-detection/batch-start")
    public ResponseEntity<?> startBatch(@RequestBody Map<String, Object> batchInfo) {
        try {
            Integer totalImages = (Integer) batchInfo.get("totalImages");
            String batchStartTime = (String) batchInfo.get("batchStartTime");
            
            // 배치 정보 저장
            this.currentBatchTotalImages = totalImages;
            this.cumulativeTotalImages += totalImages; // 누적 합계 업데이트
            
            System.out.println("📊 배치 시작: " + totalImages + "개 이미지, 누적: " + this.cumulativeTotalImages + "개, 시간: " + batchStartTime);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "배치 시작됨: " + totalImages + "개 이미지"
            ));
            
        } catch (Exception e) {
            System.err.println("❌ 배치 시작 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
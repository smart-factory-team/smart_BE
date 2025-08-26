// package carsmartfactory.application.controller;

// import carsmartfactory.application.controller.request.PressDefectDataRequestDto;
// import carsmartfactory.application.controller.response.PressDefectResultResponseDto;
// import carsmartfactory.domain.event.PressDefectDataReceivedEvent;
// import carsmartfactory.domain.event.PressDefectResultDetectedEvent;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.*;
// import jakarta.validation.Valid;
// import java.time.Instant;
// import java.util.List;
// import java.util.Map;
// import java.util.HashMap;

// /**
//  * 프레스 결함 탐지 REST API Controller
//  * 시뮬레이터와 모델 서비스 간의 데이터 전달 중개 역할
//  */
// @Slf4j
// @RestController
// @RequestMapping("/api/press-defect")
// @RequiredArgsConstructor
// @Validated
// public class PressDefectDetectionController {

//     /**
//      * 시뮬레이터로부터 원시 데이터 수신
//      * 
//      * @param request 검사 데이터 요청 DTO
//      * @return 수신 확인 응답
//      */
//     @PostMapping("/raw-data")
//     public ResponseEntity<Map<String, Object>> receiveRawData(
//             @Valid @RequestBody PressDefectDataRequestDto request) {
        
//         try {
//             log.info("🔄 시뮬레이터로부터 원시 데이터 수신: {}", request.getSummary());
            
//             // 1. 요청 유효성 검증
//             if (!request.isValid()) {
//                 log.warn("⚠️ 유효하지 않은 요청 데이터: {}", request.getInspectionId());
//                 return createErrorResponse("유효하지 않은 요청 데이터입니다", HttpStatus.BAD_REQUEST);
//             }
            
//             // 2. 도메인 이벤트 생성 및 발행
//             PressDefectDataReceivedEvent event = new PressDefectDataReceivedEvent(
//                 request.getInspectionId(),
//                 request.getImages(),
//                 request.getSource(),
//                 request.getClientInfo(),
//                 request.getMetadata()
//             );
            
//             // 3. 이벤트 유효성 검증
//             if (!event.validate()) {
//                 log.error("❌ 이벤트 유효성 검증 실패: {}", event.getInspectionId());
//                 return createErrorResponse("이벤트 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
//             }
            
//             // 4. Kafka 토픽으로 이벤트 발행
//             event.publishToRawDataTopic();
            
//             log.info("✅ 원시 데이터 이벤트 발행 완료: {}", event.getSummary());
            
//             // 5. 성공 응답 반환
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "원시 데이터가 성공적으로 수신되었습니다");
//             response.put("inspectionId", request.getInspectionId());
//             response.put("imageCount", request.getImages().size());
//             response.put("timestamp", Instant.now().toString());
//             response.put("eventId", event.getEventType());
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             log.error("💥 원시 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
//             return createErrorResponse(
//                 "원시 데이터 처리 중 오류가 발생했습니다: " + e.getMessage(), 
//                 HttpStatus.INTERNAL_SERVER_ERROR
//             );
//         }
//     }
    
//     /**
//      * 모델 서비스로부터 예측 결과 수신
//      * 
//      * @param request 예측 결과 요청 DTO
//      * @return 수신 확인 응답
//      */
//     @PostMapping("/prediction-result")
//     public ResponseEntity<Map<String, Object>> receivePredictionResult(
//             @Valid @RequestBody PressDefectResultResponseDto request) {
        
//         try {
//             log.info("🤖 모델 서비스로부터 예측 결과 수신: {}", 
//                     request.getInspectionInfo() != null ? request.getInspectionInfo().getInspectionId() : "unknown");
            
//             // 1. 요청 유효성 검증
//             if (!Boolean.TRUE.equals(request.getSuccess())) {
//                 log.warn("⚠️ 예측 실패 결과 수신: {}", request.getError());
//                 return createErrorResponse("모델 서비스 예측에 실패했습니다: " + request.getError(), HttpStatus.BAD_REQUEST);
//             }
            
//             if (request.getFinalJudgment() == null) {
//                 log.warn("⚠️ 최종 판정 데이터가 누락됨");
//                 return createErrorResponse("최종 판정 데이터가 누락되었습니다", HttpStatus.BAD_REQUEST);
//             }
            
//             // 2. 도메인 이벤트 생성 및 발행
//             PressDefectResultDetectedEvent event = new PressDefectResultDetectedEvent(
//                 request.getFinalJudgment().getInspectionId(),
//                 request.getFinalJudgment().getQualityStatus(),
//                 request.getFinalJudgment().getRecommendation(),
//                 request.getFinalJudgment().getIsComplete(),
//                 request.getFinalJudgment().getMissingHoles(),
//                 request
//             );
            
//             // 3. 이벤트 유효성 검증
//             if (!event.validate()) {
//                 log.error("❌ 이벤트 유효성 검증 실패: {}", event.getInspectionId());
//                 return createErrorResponse("이벤트 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
//             }
            
//             // 4. Kafka 토픽으로 이벤트 발행
//             event.publishToDefectDataTopic();
            
//             log.info("✅ 결함 탐지 결과 이벤트 발행 완료: {}", event.getSummary());
            
//             // 5. 결함 감지시 특별 로그
//             if (event.isDefective()) {
//                 log.warn("🚨 결함품 감지됨: {} - 누락된 구멍: {}", 
//                         event.getInspectionId(), 
//                         event.getMissingHoles());
//             } else {
//                 log.info("✅ 정상품 확인됨: {}", event.getInspectionId());
//             }
            
//             // 6. 성공 응답 반환
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "예측 결과가 성공적으로 수신되었습니다");
//             response.put("inspectionId", event.getInspectionId());
//             response.put("qualityStatus", event.getQualityStatus());
//             response.put("recommendation", event.getRecommendation());
//             response.put("isDefective", event.isDefective());
//             response.put("timestamp", Instant.now().toString());
//             response.put("eventId", event.getEventType());
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             log.error("💥 예측 결과 처리 중 오류 발생: {}", e.getMessage(), e);
//             return createErrorResponse(
//                 "예측 결과 처리 중 오류가 발생했습니다: " + e.getMessage(), 
//                 HttpStatus.INTERNAL_SERVER_ERROR
//             );
//         }
//     }
    
//     /**
//      * API 상태 확인 엔드포인트
//      * 
//      * @return API 상태 정보
//      */
//     @GetMapping("/status")
//     public ResponseEntity<Map<String, Object>> getApiStatus() {
//         Map<String, Object> status = new HashMap<>();
//         status.put("service", "Press Defect Detection API");
//         status.put("status", "running");
//         status.put("timestamp", Instant.now().toString());
//         status.put("endpoints", List.of(
//             "POST /api/press-defect/raw-data - 시뮬레이터 데이터 수신",
//             "POST /api/press-defect/prediction-result - 모델 서비스 결과 수신",
//             "GET /api/press-defect/status - API 상태 확인"
//         ));
        
//         return ResponseEntity.ok(status);
//     }
    
//     /**
//      * API 헬스체크 엔드포인트
//      * 
//      * @return 헬스체크 결과
//      */
//     @GetMapping("/health")
//     public ResponseEntity<Map<String, Object>> healthCheck() {
//         Map<String, Object> health = new HashMap<>();
//         health.put("status", "UP");
//         health.put("timestamp", Instant.now().toString());
//         health.put("service", "press-defect-detection-controller");
        
//         return ResponseEntity.ok(health);
//     }
    
//     /**
//      * 오류 응답 생성 유틸리티 메서드
//      * 
//      * @param message 오류 메시지
//      * @param status HTTP 상태 코드
//      * @return 오류 응답
//      */
//     private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
//         Map<String, Object> errorResponse = new HashMap<>();
//         errorResponse.put("success", false);
//         errorResponse.put("error", message);
//         errorResponse.put("timestamp", Instant.now().toString());
//         errorResponse.put("status", status.value());
        
//         return ResponseEntity.status(status).body(errorResponse);
//     }
    
//     /**
//      * 예외 처리 핸들러
//      * 
//      * @param e 예외
//      * @return 예외 응답
//      */
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<Map<String, Object>> handleException(Exception e) {
//         log.error("🔥 Controller 예외 발생: {}", e.getMessage(), e);
//         return createErrorResponse(
//             "서버 내부 오류가 발생했습니다: " + e.getMessage(), 
//             HttpStatus.INTERNAL_SERVER_ERROR
//         );
//     }
// }

package carsmartfactory.application.controller;

import carsmartfactory.application.controller.request.PressDefectDataRequestDto;
import carsmartfactory.application.controller.response.PressDefectResultResponseDto;
import carsmartfactory.domain.event.PressDefectDataReceivedEvent;
import carsmartfactory.domain.event.PressDefectResultDetectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 프레스 결함 탐지 REST API Controller
 * 시뮬레이터와 모델 서비스 간의 데이터 전달 중개 역할
 */
@Slf4j
@RestController
@RequestMapping("/api/press-defect")
@RequiredArgsConstructor
@Validated
public class PressDefectDetectionController {

    private final RestTemplate restTemplate;
    
    @Value("${app.model-service.url:http://localhost:8003}")
    private String modelServiceUrl;

    /**
     * 시뮬레이터로부터 원시 데이터 수신
     * 
     * @param request 검사 데이터 요청 DTO
     * @return 수신 확인 응답
     */
    @PostMapping("/raw-data")
    public ResponseEntity<Map<String, Object>> receiveRawData(
            @Valid @RequestBody PressDefectDataRequestDto request) {
        
        try {
            log.info("🔄 시뮬레이터로부터 원시 데이터 수신: {}", request.getSummary());
            
            // 1. 요청 유효성 검증
            if (!request.isValid()) {
                log.warn("⚠️ 유효하지 않은 요청 데이터: {}", request.getInspectionId());
                return createErrorResponse("유효하지 않은 요청 데이터입니다", HttpStatus.BAD_REQUEST);
            }
            
            // 2. 도메인 이벤트 생성 및 발행
            PressDefectDataReceivedEvent event = new PressDefectDataReceivedEvent(
                request.getInspectionId(),
                request.getImages(),
                request.getSource(),
                request.getClientInfo(),
                request.getMetadata()
            );
            
            // 3. 이벤트 유효성 검증
            if (!event.validate()) {
                log.error("❌ 이벤트 유효성 검증 실패: {}", event.getInspectionId());
                return createErrorResponse("이벤트 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            // 4. Kafka 토픽으로 이벤트 발행
            event.publishToRawDataTopic();
            log.info("✅ 원시 데이터 이벤트 발행 완료: {}", event.getSummary());
            
            // 5. 이벤트 발행 즉시 FastAPI 모델 서비스 호출
            callModelServiceForPrediction(request);
            
            // 6. 성공 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "원시 데이터가 성공적으로 수신되었습니다");
            response.put("inspectionId", request.getInspectionId());
            response.put("imageCount", request.getImages().size());
            response.put("timestamp", Instant.now().toString());
            response.put("eventId", event.getEventType());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("💥 원시 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            return createErrorResponse(
                "원시 데이터 처리 중 오류가 발생했습니다: " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * 모델 서비스로부터 예측 결과 수신
     * 
     * @param request 예측 결과 요청 DTO
     * @return 수신 확인 응답
     */
    @PostMapping("/prediction-result")
    public ResponseEntity<Map<String, Object>> receivePredictionResult(
            @Valid @RequestBody PressDefectResultResponseDto request) {
        
        try {
            log.info("🤖 모델 서비스로부터 예측 결과 수신: {}", 
                    request.getInspectionInfo() != null ? request.getInspectionInfo().getInspectionId() : "unknown");
            
            // 1. 요청 유효성 검증
            if (!Boolean.TRUE.equals(request.getSuccess())) {
                log.warn("⚠️ 예측 실패 결과 수신: {}", request.getError());
                return createErrorResponse("모델 서비스 예측에 실패했습니다: " + request.getError(), HttpStatus.BAD_REQUEST);
            }
            
            if (request.getFinalJudgment() == null) {
                log.warn("⚠️ 최종 판정 데이터가 누락됨");
                return createErrorResponse("최종 판정 데이터가 누락되었습니다", HttpStatus.BAD_REQUEST);
            }
            
            // 2. 결과에 따른 처리
            String inspectionId = request.getFinalJudgment().getInspectionId();
            boolean isDefective = request.isDefective();
            
            // 3. 웹소켓으로 프론트에 모니터링 데이터 전송 (정상/이상 모두)
            sendMonitoringDataToFrontend(request);
            
            if (isDefective) {
                // 4-1. 이상 감지시: 이상 이벤트 발행 및 DB 저장
                log.warn("🚨 결함품 감지됨: {} - 누락된 구멍: {}", 
                        inspectionId, 
                        request.getFinalJudgment().getMissingHoles());
                        
                // 이상 이벤트 생성 및 발행
                PressDefectResultDetectedEvent defectEvent = new PressDefectResultDetectedEvent(
                    request.getFinalJudgment().getInspectionId(),
                    request.getFinalJudgment().getQualityStatus(),
                    request.getFinalJudgment().getRecommendation(),
                    request.getFinalJudgment().getIsComplete(),
                    request.getFinalJudgment().getMissingHoles(),
                    request
                );
                
                // Kafka 토픽으로 이상 이벤트 발행
                defectEvent.publishToDefectDataTopic();
                log.info("✅ 결함 탐지 결과 이벤트 발행 완료: {}", defectEvent.getSummary());
                
                // DB에 저장
                saveDefectDataToDatabase(defectEvent);
                
            } else {
                // 4-2. 정상품인 경우
                log.info("✅ 정상품 확인됨: {}", inspectionId);
            }
            
            // 5. 성공 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "예측 결과가 성공적으로 처리되었습니다");
            response.put("inspectionId", inspectionId);
            response.put("qualityStatus", request.getFinalJudgment().getQualityStatus());
            response.put("recommendation", request.getFinalJudgment().getRecommendation());
            response.put("isDefective", isDefective);
            response.put("timestamp", Instant.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("💥 예측 결과 처리 중 오류 발생: {}", e.getMessage(), e);
            return createErrorResponse(
                "예측 결과 처리 중 오류가 발생했습니다: " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    /**
     * FastAPI 모델 서비스 호출
     */
    private void callModelServiceForPrediction(PressDefectDataRequestDto request) {
        try {
            log.info("🤖 모델 서비스 호출 시작: {}", request.getInspectionId());
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // FastAPI 요청 데이터 구성
            Map<String, Object> modelRequest = new HashMap<>();
            modelRequest.put("inspectionId", request.getInspectionId());
            modelRequest.put("images", request.getImages());
            modelRequest.put("source", request.getSource());
            
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(modelRequest, headers);
            
            // 모델 서비스 API 호출 (비동기적으로 처리)
            String modelApiUrl = modelServiceUrl + "/predict";
            
            log.info("📤 모델 서비스 API 호출: {} -> {}", request.getInspectionId(), modelApiUrl);
            
            // 백그라운드에서 비동기 호출 (결과는 /prediction-result로 받음)
            ResponseEntity<String> response = restTemplate.postForEntity(
                modelApiUrl, 
                httpEntity, 
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ 모델 서비스 호출 성공: {} - {}", request.getInspectionId(), response.getStatusCode());
            } else {
                log.warn("⚠️ 모델 서비스 응답 상태: {} - {}", response.getStatusCode(), response.getBody());
            }
            
        } catch (Exception e) {
            log.error("💥 모델 서비스 호출 실패: {} - {}", request.getInspectionId(), e.getMessage(), e);
        }
    }
    
    /**
     * 웹소켓으로 프론트엔드에 모니터링 데이터 전송
     */
    private void sendMonitoringDataToFrontend(PressDefectResultResponseDto result) {
        try {
            log.info("📡 프론트엔드로 모니터링 데이터 전송: {}", 
                    result.getFinalJudgment().getInspectionId());
            
            // TODO: 웹소켓 서비스 구현 필요
            // webSocketService.sendMonitoringData(result);
            
            log.info("✅ 모니터링 데이터 전송 완료: {}", 
                    result.getFinalJudgment().getInspectionId());
                    
        } catch (Exception e) {
            log.error("💥 모니터링 데이터 전송 실패: {} - {}", 
                    result.getFinalJudgment().getInspectionId(), e.getMessage(), e);
        }
    }
    
    /**
     * 결함 데이터를 데이터베이스에 저장
     */
    private void saveDefectDataToDatabase(PressDefectResultDetectedEvent event) {
        try {
            log.info("💾 결함 데이터 DB 저장 시작: {}", event.getInspectionId());
            
            // TODO: Repository를 통한 DB 저장 구현 필요
            // pressDefectDetectionLogRepository.save(event.toEntity());
            
            log.info("✅ 결함 데이터 DB 저장 완료: {}", event.getInspectionId());
            
        } catch (Exception e) {
            log.error("💥 결함 데이터 DB 저장 실패: {} - {}", event.getInspectionId(), e.getMessage(), e);
        }
    }
    
    /**
     * API 상태 확인 엔드포인트
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "Press Defect Detection API");
        status.put("status", "running");
        status.put("timestamp", Instant.now().toString());
        status.put("endpoints", List.of(
            "POST /api/press-defect/raw-data - 시뮬레이터 데이터 수신",
            "POST /api/press-defect/prediction-result - 모델 서비스 결과 수신",
            "GET /api/press-defect/status - API 상태 확인"
        ));
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * API 헬스체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", Instant.now().toString());
        health.put("service", "press-defect-detection-controller");
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * 오류 응답 생성 유틸리티 메서드
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("status", status.value());
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * 예외 처리 핸들러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("🔥 Controller 예외 발생: {}", e.getMessage(), e);
        return createErrorResponse(
            "서버 내부 오류가 발생했습니다: " + e.getMessage(), 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
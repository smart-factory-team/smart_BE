package carsmartfactory.application.controller;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import carsmartfactory.application.controller.request.PressDefectDataRequestDto;
import carsmartfactory.application.controller.response.PressDefectResultResponseDto;
import carsmartfactory.domain.event.PressDefectDataReceivedEvent;
import carsmartfactory.domain.event.PressDefectResultDetectedEvent;
import carsmartfactory.domain.model.PressDefectDetectionLog;
import carsmartfactory.domain.repository.PressDefectDetectionLogRepository;
import carsmartfactory.infra.websocket.WebSocketService;
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
    private final WebSocketService webSocketService;
    private final PressDefectDetectionLogRepository defectLogRepository;

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
            log.info("시뮬레이터로부터 원시 데이터 수신: {}", request.getSummary());

            // 1. 요청 유효성 검증
            if (!request.isValid()) {
                log.warn("유효하지 않은 요청 데이터: {}", request.getInspectionId());
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
                log.error("이벤트 유효성 검증 실패: {}", event.getInspectionId());
                return createErrorResponse("이벤트 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 4. Kafka 토픽으로 이벤트 발행
            event.publishToRawDataTopic();
            log.info("원시 데이터 이벤트 발행 완료: {}", event.getSummary());

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
            log.error("원시 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            return createErrorResponse(
                "원시 데이터 처리 중 오류가 발생했습니다: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * FastAPI 모델 서비스 호출
     */
    private void callModelServiceForPrediction(PressDefectDataRequestDto request) {
        try {
            log.info("모델 서비스 호출 시작: {}", request.getInspectionId());

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // FastAPI 요청 데이터 구성
            Map<String, Object> modelRequest = new HashMap<>();
            modelRequest.put("inspection_id", request.getInspectionId());

            // images 배열을 FastAPI가 기대하는 형식으로 변환
            java.util.List<Map<String, Object>> imageList = new java.util.ArrayList<>();
            for (var imageData : request.getImages()) {
                Map<String, Object> image = new HashMap<>();
                image.put("image", imageData.getImage());
                image.put("name", imageData.getName());
                imageList.add(image);
            }
            modelRequest.put("images", imageList);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(modelRequest, headers);

            // 모델 서비스 API 호출
            String modelApiUrl = modelServiceUrl + "/predict/inspection";

            log.info("모델 서비스 API 호출: {} -> {}", request.getInspectionId(), modelApiUrl);

            // 동기 호출 후 응답을 바로 처리
            ResponseEntity<PressDefectResultResponseDto> response = restTemplate.postForEntity(
                modelApiUrl,
                httpEntity,
                PressDefectResultResponseDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("모델 서비스 호출 성공: {} - {}", request.getInspectionId(), response.getStatusCode());

                PressDefectResultResponseDto result = response.getBody();

                // DB에서 총 생산량과 결함 수를 가져옴
                long totalProducts = defectLogRepository.count();
                long totalDefects = defectLogRepository.countDefective();

                // 웹소켓으로 프론트에 모니터링 데이터 전송 (인자 3개 전달)
                webSocketService.sendMonitoringData(result, totalProducts, totalDefects);

                // 결함인 경우 이상 이벤트 발행 및 DB 저장
                if (result.isDefective()) {
                    log.warn("결함품 감지됨: {} - 누락된 구멍: {}",
                        request.getInspectionId(),
                        result.getFinalJudgment().getMissingHoles());

                    // 이상 이벤트 생성 및 발행
                    PressDefectResultDetectedEvent defectEvent = new PressDefectResultDetectedEvent(
                        result.getFinalJudgment().getInspectionId(),
                        result.getFinalJudgment().getQualityStatus(),
                        result.getFinalJudgment().getRecommendation(),
                        result.getFinalJudgment().getIsComplete(),
                        result.getFinalJudgment().getMissingHoles(),
                        result
                    );

                    // Kafka 토픽으로 이상 이벤트 발행
                    defectEvent.publishToDefectDataTopic();
                    log.info("결함 탐지 결과 이벤트 발행 완료: {}", defectEvent.getSummary());

                    // DB에 저장
                    saveDefectDataToDatabase(defectEvent);
                } else {
                    log.info("정상품 확인됨: {}", request.getInspectionId());
                }

            } else {
                log.warn("모델 서비스 응답 상태: {} - {}", response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            log.error("모델 서비스 호출 실패: {} - {}", request.getInspectionId(), e.getMessage(), e);
        }
    }

    /**
     * 결함 데이터를 데이터베이스에 저장
     */
    private void saveDefectDataToDatabase(PressDefectResultDetectedEvent event) {
        try {
            log.info("결함 데이터 DB 저장 시작: {}", event.getInspectionId());

            // 이벤트를 엔티티로 변환
            PressDefectDetectionLog logEntity = PressDefectDetectionLog.fromEvent(event);

            // Repository를 통해 DB 저장
            defectLogRepository.save(logEntity);

            log.info("결함 데이터 DB 저장 완료: {}", event.getInspectionId());

        } catch (Exception e) {
            log.error("결함 데이터 DB 저장 실패: {} - {}", event.getInspectionId(), e.getMessage(), e);
            throw new RuntimeException("결함 데이터 저장에 실패했습니다", e);
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
        log.error("Controller 예외 발생: {}", e.getMessage(), e);
        return createErrorResponse(
            "서버 내부 오류가 발생했습니다: " + e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("요청 데이터 검증 실패: {}", e.getMessage());
        return createErrorResponse("요청 데이터가 유효하지 않습니다: " + e.getBindingResult().getFieldError().getDefaultMessage(),
                                 HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonException(HttpMessageNotReadableException e) {
        log.error("JSON 파싱 실패: {}", e.getMessage());
        return createErrorResponse("JSON 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST);
    }
}
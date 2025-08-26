package carsmartfactory.domain.event;

import carsmartfactory.common.event.AbstractEvent;
import carsmartfactory.application.controller.response.PressDefectResultResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 모델 서비스로부터 결함 탐지 결과를 수신했을 때 발행되는 도메인 이벤트
 * Kafka 토픽: defect-data-detected
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PressDefectResultDetectedEvent extends AbstractEvent {
    
    /**
     * 검사 ID (예: "inspection_001")
     */
    private String inspectionId;
    
    /**
     * 품질 상태 ("정상품" or "결함품")
     */
    private String qualityStatus;
    
    /**
     * 최종 권장사항 ("Pass" or "Reject")
     */
    private String recommendation;
    
    /**
     * 결함 완성도 여부 (7개 구멍이 모두 탐지되었는지)
     */
    private Boolean isComplete;
    
    /**
     * 누락된 구멍 목록 (결함인 경우)
     */
    private List<String> missingHoles;
    
    /**
     * 모델 서비스 전체 예측 결과
     */
    private PressDefectResultResponseDto predictionResult;
    
    /**
     * 결과 수신 시간 (ISO 8601 형태)
     */
    private String detectedTimestamp;
    
    /**
     * 데이터 소스 ("model-service")
     */
    private String source;
    
    /**
     * 처리 시간 (초)
     */
    private Double processingTimeSeconds;
    
    /**
     * 추가 메타데이터 (옵션)
     */
    private String metadata;
    
    /**
     * 생성자 (기본)
     */
    public PressDefectResultDetectedEvent(String inspectionId, String qualityStatus, 
                                        String recommendation, Boolean isComplete, 
                                        List<String> missingHoles, PressDefectResultResponseDto predictionResult) {
        super();
        this.inspectionId = inspectionId;
        this.qualityStatus = qualityStatus;
        this.recommendation = recommendation;
        this.isComplete = isComplete;
        this.missingHoles = missingHoles;
        this.predictionResult = predictionResult;
        this.source = "model-service";
        this.detectedTimestamp = java.time.Instant.now().toString();
    }
    
    /**
     * 생성자 (전체 필드)
     */
    public PressDefectResultDetectedEvent(String inspectionId, String qualityStatus, 
                                        String recommendation, Boolean isComplete, 
                                        List<String> missingHoles, PressDefectResultResponseDto predictionResult,
                                        Double processingTimeSeconds, String metadata) {
        this(inspectionId, qualityStatus, recommendation, isComplete, missingHoles, predictionResult);
        this.processingTimeSeconds = processingTimeSeconds;
        this.metadata = metadata;
    }
    
    /**
     * 이벤트 유효성 검증
     */
    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        
        if (inspectionId == null || inspectionId.trim().isEmpty()) {
            return false;
        }
        
        if (qualityStatus == null || (!qualityStatus.equals("정상품") && !qualityStatus.equals("결함품"))) {
            return false;
        }
        
        if (recommendation == null || (!recommendation.equals("Pass") && !recommendation.equals("Reject"))) {
            return false;
        }
        
        if (isComplete == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 이벤트 요약 정보
     */
    public String getSummary() {
        return String.format("PressDefectResultDetected[inspectionId=%s, qualityStatus=%s, recommendation=%s, complete=%s, missingHoles=%d]", 
                           inspectionId, 
                           qualityStatus, 
                           recommendation,
                           isComplete,
                           missingHoles != null ? missingHoles.size() : 0);
    }
    
    /**
     * 결함 여부 확인
     */
    public boolean isDefective() {
        return "결함품".equals(qualityStatus) || "Reject".equals(recommendation) || !Boolean.TRUE.equals(isComplete);
    }
    
    /**
     * 정상품 여부 확인  
     */
    public boolean isNormal() {
        return "정상품".equals(qualityStatus) && "Pass".equals(recommendation) && Boolean.TRUE.equals(isComplete);
    }
    
    /**
     * 특정 토픽으로 발행 (결함 데이터 토픽)
     */
    public void publishToDefectDataTopic() {
        super.publishToDefectDataTopic(); 
    }
}
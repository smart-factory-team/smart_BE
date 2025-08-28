package carsmartfactory.application.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * 모델 서비스에서 반환하는 예측 결과 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PressDefectResultResponseDto {
    
    /**
     * 예측 성공 여부
     */
    private Boolean success;
    
    /**
     * 처리 시간 (ISO 8601 형태)
     */
    private String timestamp;
    
    /**
     * 검사 정보
     */
    @JsonProperty("inspection_info")
    private InspectionInfoDto inspectionInfo;
    
    /**
     * 품질 검사 결과
     */
    @JsonProperty("quality_inspection")
    private QualityInspectionDto qualityInspection;
    
    /**
     * 최종 판정 결과
     */
    @JsonProperty("final_judgment")
    private FinalJudgmentDto finalJudgment;
    
    /**
     * 처리 요약
     */
    @JsonProperty("processing_summary")
    private ProcessingSummaryDto processingSummary;
    
    /**
     * 상세 탐지 결과 (옵션)
     */
    @JsonProperty("detailed_detections")
    private Map<String, Object> detailedDetections;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 오류 정보 (실패시)
     */
    private String error;
    
    // Inner Classes
    
    /**
     * 검사 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspectionInfoDto {
        @JsonProperty("inspection_id")
        private String inspectionId;
        
        @JsonProperty("expected_images")
        private Integer expectedImages;
        
        @JsonProperty("actual_images")
        private Integer actualImages;
        
        @JsonProperty("is_complete_dataset")
        private Boolean isCompleteDataset;
    }
    
    /**
     * 품질 검사 결과 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualityInspectionDto {
        @JsonProperty("is_complete")
        private Boolean isComplete;
        
        @JsonProperty("quality_status")
        private String qualityStatus;  // "정상품" or "결함품"
        
        @JsonProperty("existing_categories")
        private List<Integer> existingCategories;
        
        @JsonProperty("missing_categories")
        private List<Integer> missingCategories;
        
        @JsonProperty("missing_category_names")
        private List<String> missingCategoryNames;
        
        @JsonProperty("processed_images")
        private Integer processedImages;
        
        @JsonProperty("category_results")
        private Map<String, Object> categoryResults;
    }
    
    /**
     * 최종 판정 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinalJudgmentDto {
        @JsonProperty("inspection_id")
        private String inspectionId;
        
        @JsonProperty("quality_status")
        private String qualityStatus;  // "정상품" or "결함품"
        
        @JsonProperty("is_complete")
        private Boolean isComplete;
        
        @JsonProperty("missing_holes")
        private List<String> missingHoles;
        
        private String recommendation;  // "Pass" or "Reject" - camelCase 그대로
    }
    
    /**
     * 처리 요약 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingSummaryDto {
        @JsonProperty("total_images")
        private Integer totalImages;
        
        @JsonProperty("processed_images")
        private Integer processedImages;
        
        @JsonProperty("failed_images")
        private Integer failedImages;
        
        @JsonProperty("failed_details")
        private List<FailedImageDto> failedDetails;
    }
    
    /**
     * 실패한 이미지 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedImageDto {
        private String name;
        private String error;
    }
    
    // 정적 팩토리 메서드들
    
    /**
     * 성공 응답 생성
     */
    public static PressDefectResultResponseDto success(String message) {
        PressDefectResultResponseDto response = new PressDefectResultResponseDto();
        response.setSuccess(true);
        response.setTimestamp(java.time.Instant.now().toString());
        response.setMessage(message);
        return response;
    }
    
    /**
     * 실패 응답 생성
     */
    public static PressDefectResultResponseDto failure(String error) {
        PressDefectResultResponseDto response = new PressDefectResultResponseDto();
        response.setSuccess(false);
        response.setTimestamp(java.time.Instant.now().toString());
        response.setError(error);
        return response;
    }
    
    /**
     * 결함 여부 확인
     */
    public boolean isDefective() {
        if (finalJudgment == null) return false;
        return "결함품".equals(finalJudgment.getQualityStatus()) || 
               "Reject".equals(finalJudgment.getRecommendation()) || 
               !Boolean.TRUE.equals(finalJudgment.getIsComplete());
    }
    
    /**
     * 정상품 여부 확인  
     */
    public boolean isNormal() {
        if (finalJudgment == null) return false;
        return "정상품".equals(finalJudgment.getQualityStatus()) && 
               "Pass".equals(finalJudgment.getRecommendation()) && 
               Boolean.TRUE.equals(finalJudgment.getIsComplete());
    }
}
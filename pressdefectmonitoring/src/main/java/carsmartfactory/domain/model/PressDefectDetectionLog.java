package carsmartfactory.domain.model;

import carsmartfactory.domain.event.PressDefectResultDetectedEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 프레스 결함 탐지 로그 엔티티
 */
@Entity
@Table(name = "press_defect_detection_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PressDefectDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 검사 ID (비즈니스 키)
     */
    @Column(name = "inspection_id", nullable = false, unique = true, length = 100)
    private String inspectionId;

    /**
     * 품질 상태 ("정상품" 또는 "결함품")
     */
    @Column(name = "quality_status", nullable = false, length = 50)
    private String qualityStatus;

    /**
     * 추천 결과 ("Pass" 또는 "Reject")
     */
    @Column(name = "recommendation", nullable = false, length = 50)
    private String recommendation;

    /**
     * 검사 완료 여부
     */
    @Column(name = "is_complete", nullable = false)
    private Boolean isComplete;

    /**
     * 누락된 구멍 목록 (JSON 형태로 저장)
     */
    @Type(JsonType.class)
    @Column(name = "missing_holes", columnDefinition = "jsonb")
    private List<String> missingHoles;

    /**
     * 결함 여부 (계산된 필드)
     */
    @Column(name = "is_defective", nullable = false)
    private Boolean isDefective;

    /**
     * 정상품 여부 (계산된 필드)
     */
    @Column(name = "is_normal", nullable = false)
    private Boolean isNormal;

    /**
     * 탐지 일시
     */
    @Column(name = "detected_timestamp", nullable = false)
    private LocalDateTime detectedTimestamp;

    /**
     * 데이터 소스
     */
    @Column(name = "source", length = 100)
    private String source;

    /**
     * 처리 시간 (초)
     */
    @Column(name = "processing_time_seconds")
    private Double processingTimeSeconds;

    /**
     * 추가 메타데이터 (JSON 형태로 저장)
     */
    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    /**
     * 예측 결과 전체 데이터 (JSON 형태로 저장)
     */
    @Type(JsonType.class)
    @Column(name = "prediction_result", columnDefinition = "jsonb")
    private Object predictionResult;

    /**
     * 생성 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 이벤트로부터 엔티티 생성
     */
    public static PressDefectDetectionLog fromEvent(PressDefectResultDetectedEvent event) {
        return PressDefectDetectionLog.builder()
                .inspectionId(event.getInspectionId())
                .qualityStatus(event.getQualityStatus())
                .recommendation(event.getRecommendation())
                .isComplete(event.getIsComplete())
                .missingHoles(event.getMissingHoles())
                .isDefective(event.isDefective())
                .isNormal(event.isNormal())
                .detectedTimestamp(event.getDetectedTimestamp() != null ? 
                    parseTimestamp(event.getDetectedTimestamp()) : 
                    LocalDateTime.now())
                .source(event.getSource())
                .processingTimeSeconds(event.getProcessingTimeSeconds())
                .metadata(event.getMetadata())
                .predictionResult(event.getPredictionResult())
                .build();
    }

    /**
     * ISO 8601 타임스탬프를 LocalDateTime으로 변환
     */
    private static LocalDateTime parseTimestamp(String timestamp) {
        try {
            // ISO 8601 형식을 다양하게 처리
            if (timestamp.contains("T")) {
                // 2024-01-01T10:00:00Z 또는 2024-01-01T10:00:00.123Z 형태
                if (timestamp.endsWith("Z")) {
                    // UTC 시간을 LocalDateTime으로 변환 (Z 제거)
                    String localTimeString = timestamp.substring(0, timestamp.length() - 1);
                    return LocalDateTime.parse(localTimeString);
                } else if (timestamp.contains("+") || timestamp.lastIndexOf('-') > 10) {
                    // 타임존 정보가 있는 경우 (2024-01-01T10:00:00+09:00)
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(timestamp);
                    return offsetDateTime.toLocalDateTime();
                } else {
                    // 이미 LocalDateTime 형식
                    return LocalDateTime.parse(timestamp);
                }
            } else {
                // 기본 LocalDateTime 형식으로 시도
                return LocalDateTime.parse(timestamp);
            }
        } catch (DateTimeParseException e) {
            // 파싱 실패시 현재 시간 반환
            return LocalDateTime.now();
        }
    }

    /**
     * 결함 상태 업데이트 (저장 전 호출)
     */
    @PrePersist
    @PreUpdate
    public void updateDefectStatus() {
        this.isDefective = "결함품".equals(this.qualityStatus) || 
                          "Reject".equals(this.recommendation) || 
                          !Boolean.TRUE.equals(this.isComplete);
        this.isNormal = !this.isDefective;
    }

    /**
     * 요약 정보 생성
     */
    public String getSummary() {
        return String.format("PressDefectLog[id=%s, status=%s, defective=%s, holes=%d]",
                inspectionId,
                qualityStatus,
                isDefective,
                missingHoles != null ? missingHoles.size() : 0);
    }
}
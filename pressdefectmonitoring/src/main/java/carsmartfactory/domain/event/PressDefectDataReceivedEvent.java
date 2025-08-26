package carsmartfactory.domain.event;

import carsmartfactory.common.event.AbstractEvent;
import carsmartfactory.application.controller.request.PressDefectDataRequestDto.ImageDataDto;  // 수정됨
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 시뮬레이터로부터 원시 데이터를 수신했을 때 발행되는 도메인 이벤트
 * Kafka 토픽: raw-data-received
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PressDefectDataReceivedEvent extends AbstractEvent {
    
    /**
     * 검사 ID (예: "inspection_001")
     */
    private String inspectionId;
    
    /**
     * 이미지 데이터 리스트 (21장)
     */
    private List<ImageDataDto> images;
    
    /**
     * 데이터 수신 시간 (ISO 8601 형태)
     */
    private String receivedTimestamp;
    
    /**
     * 데이터 소스 ("simulator")
     */
    private String source;
    
    /**
     * 요청한 클라이언트 정보 (옵션)
     */
    private String clientInfo;
    
    /**
     * 추가 메타데이터 (옵션)
     */
    private String metadata;
    
    /**
     * 생성자
     */
    public PressDefectDataReceivedEvent(String inspectionId, List<ImageDataDto> images, String source) {
        super();
        this.inspectionId = inspectionId;
        this.images = images;
        this.source = source;
        this.receivedTimestamp = java.time.Instant.now().toString();
    }
    
    /**
     * 생성자 (전체 필드)
     */
    public PressDefectDataReceivedEvent(String inspectionId, List<ImageDataDto> images, 
                                      String source, String clientInfo, String metadata) {
        this(inspectionId, images, source);
        this.clientInfo = clientInfo;
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
        
        if (images == null || images.isEmpty()) {
            return false;
        }
        
        if (source == null || source.trim().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 이벤트 요약 정보
     */
    public String getSummary() {
        return String.format("PressDefectDataReceived[inspectionId=%s, imageCount=%d, source=%s]", 
                           inspectionId, 
                           images != null ? images.size() : 0, 
                           source);
    }
    
    /**
     * 특정 토픽으로 발행 (원시 데이터 토픽)
     */
    public void publishToRawDataTopic() {
        super.publishToRawDataTopic();
    }
    
    /**
     * Getter 메서드 (이벤트에서 필요한 메서드들 추가)
     */
    public String getInspectionId() {
        return inspectionId;
    }
}
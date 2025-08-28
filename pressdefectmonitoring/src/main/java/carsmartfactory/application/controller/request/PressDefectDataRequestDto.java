package carsmartfactory.application.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 시뮬레이터로부터 원시 데이터를 수신하는 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PressDefectDataRequestDto {
    
    /**
     * 검사 ID (예: "inspection_001")
     */
    @NotBlank(message = "검사 ID는 필수입니다")
    private String inspectionId;
    
    /**
     * 이미지 데이터 리스트 (21장)
     */
    @NotEmpty(message = "이미지 데이터는 최소 1개 이상이어야 합니다")
    private List<ImageDataDto> images;
    
    /**
     * 데이터 소스 ("simulator")
     */
    private String source = "simulator";
    
    /**
     * 요청한 클라이언트 정보 (옵션)
     */
    private String clientInfo;
    
    /**
     * 추가 메타데이터 (옵션)
     */
    private String metadata;
    
    /**
     * 이미지 데이터 DTO (Inner Class)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDataDto {
        
        /**
         * Base64 인코딩된 이미지 데이터 (data:image/jpeg;base64,... 형태)
         */
        @NotBlank(message = "이미지 데이터는 필수입니다")
        private String image;
        
        /**
         * 이미지 이름/카메라명 (예: cam_0, cam_1, ...)
         */
        @NotBlank(message = "이미지 이름은 필수입니다")
        private String name;
        
        /**
         * 이미지 크기 (옵션)
         */
        private Long size;
        
        /**
         * 추가 메타데이터 (옵션)
         */
        private String metadata;
    }
    
    /**
     * 요청 유효성 검증
     */
    public boolean isValid() {
        if (inspectionId == null || inspectionId.trim().isEmpty()) {
            return false;
        }
        
        if (images == null || images.isEmpty()) {
            return false;
        }
        
        // 각 이미지 데이터 유효성 검증
        for (ImageDataDto imageData : images) {
            if (imageData.getImage() == null || imageData.getImage().trim().isEmpty()) {
                return false;
            }
            if (imageData.getName() == null || imageData.getName().trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 요청 요약 정보
     */
    public String getSummary() {
        return String.format("PressDefectDataRequest[inspectionId=%s, imageCount=%d, source=%s]", 
                           inspectionId, 
                           images != null ? images.size() : 0, 
                           source);
    }
}
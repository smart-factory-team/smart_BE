package carsmartfactory.dto.external;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AIAnalysisRequest {
    private String imageBase64;  // Base64 인코딩된 이미지
    private String partType;
    private String analysisType;
    private Integer imageWidth;
    private Integer imageHeight;
}
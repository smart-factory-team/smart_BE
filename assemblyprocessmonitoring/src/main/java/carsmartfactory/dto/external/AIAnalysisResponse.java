package carsmartfactory.dto.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResponse {
    private boolean success;
    private AnalysisResult result;
    private String message;
    private String modelVersion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisResult {
        private boolean isDefective;
        private String defectType;
        private Double confidence;
        private List<DefectLocation> locations;
        private String severity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefectLocation {
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
    }
}
package carsmartfactory.infra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaintingSurfacePredictionResponseDto {
    private List<Map<String, Object>> predictions;
    private List<Integer> imageShape;
    private Float confidenceThreshold;
    private String timestamp;
    private String modelSource;
    private Map<String, Object> modelConfig;
}

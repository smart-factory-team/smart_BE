package carsmartfactory.infra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaintingSurfacePredictionRequestDto {
    private String imageName;
    private byte[] imageData;
    private Float confidenceThreshold;
}

package carsmartfactory.infra.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaintingSurfaceImageDto {
    private String imageName;
    private byte[] imageData;
    private String timestamp;
    private String source;
    private Float confidenceThreshold;
}

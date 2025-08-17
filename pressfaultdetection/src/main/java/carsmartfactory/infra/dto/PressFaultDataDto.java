package carsmartfactory.infra.dto;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class PressFaultDataDto {
    @JsonProperty("AI0_Vibration")
    private List<Double> AI0_Vibration;
    
    @JsonProperty("AI1_Vibration")
    private List<Double> AI1_Vibration;
    
    @JsonProperty("AI2_Current")
    private List<Double> AI2_Current;
    
    private String timestamp;
    private String source;
    
    @JsonProperty("data_length")
    private Integer data_length;
}

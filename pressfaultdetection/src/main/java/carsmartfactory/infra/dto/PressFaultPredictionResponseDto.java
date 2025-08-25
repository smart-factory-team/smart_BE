package carsmartfactory.infra.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PressFaultPredictionResponseDto {
    private String prediction;
    private Double reconstruction_error;
    private Boolean is_fault;
    private Double fault_probability;
    private Map<String, Double> attribute_errors;
}
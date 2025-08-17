package carsmartfactory.domain;

import carsmartfactory.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
public class PressFaultDataReceivedEvent extends AbstractEvent {
    
    private List<Double> AI0_Vibration;
    private List<Double> AI1_Vibration;
    private List<Double> AI2_Current;
    private String dataTimestamp;
    private String source;
    private Integer data_length;
}
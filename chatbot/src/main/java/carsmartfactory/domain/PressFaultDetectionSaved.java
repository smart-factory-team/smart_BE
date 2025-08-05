package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PressFaultDetectionSaved extends AbstractEvent {

    private String id;
    private Long machineId;
    private Date timeStamp;
    private Float ai0Vibration;
    private Float ai1Vibration;
    private Float ai2Current;
    private String issue;
    private Boolean isSolved;
}

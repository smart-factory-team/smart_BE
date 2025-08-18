package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PressFaultDetectionSaved extends AbstractEvent {

    private String id;
    private Long machineId;
    private Date timeStamp;
    private String ai0Vibration;
    private String ai1Vibration;
    private String ai2Current;
    private String issue;
    private Boolean isSolved;

    public PressFaultDetectionSaved(PressFaultDetectionLog aggregate) {
        super(aggregate);
    }

    public PressFaultDetectionSaved() {
        super();
    }
}
//>>> DDD / Domain Event

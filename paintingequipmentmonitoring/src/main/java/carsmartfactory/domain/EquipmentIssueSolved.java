package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class EquipmentIssueSolved extends AbstractEvent {

    private String id;
    private String issue;
    private Boolean isSolved;

    public EquipmentIssueSolved(
        PaintingProcessEquipmentDefectDetectionLog aggregate
    ) {
        super(aggregate);
    }

    public EquipmentIssueSolved() {
        super();
    }
}
//>>> DDD / Domain Event

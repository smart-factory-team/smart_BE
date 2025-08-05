package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class IssueSolved extends AbstractEvent {

    private String id;
    private String issue;
    private Boolean isSolved;

    public IssueSolved(VehicleAssemblyProcessDefectDetectionLog aggregate) {
        super(aggregate);
    }

    public IssueSolved() {
        super();
    }
}
//>>> DDD / Domain Event

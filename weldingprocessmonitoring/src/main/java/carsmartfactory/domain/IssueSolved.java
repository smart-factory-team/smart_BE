package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper = false)  // ✅ 경고 제거
public class IssueSolved extends AbstractEvent {

    private String id;
    private String issue;
    private Boolean isSolved;

    public IssueSolved(WeldingMachineDefectDetectionLog aggregate) {
        super(aggregate);
    }

    public IssueSolved() {
        super();
    }
}
//>>> DDD / Domain Event
package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class SurfaceIssueSolved extends AbstractEvent {

    private String id;
    private String issue;
    private Boolean isSolved;

    public SurfaceIssueSolved(PaintingSurfaceDefectDetectionLog aggregate) {
        super(aggregate);
    }

    public SurfaceIssueSolved() {
        super();
    }
}
//>>> DDD / Domain Event

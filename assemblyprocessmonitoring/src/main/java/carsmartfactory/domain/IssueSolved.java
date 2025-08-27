package carsmartfactory.domain;

import carsmartfactory.infra.AbstractEvent;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class IssueSolved extends AbstractEvent {

    private String id;
    private String issue;
    private Boolean isSolved;

    public IssueSolved(DefectDetectionLog aggregate) {
        super(aggregate);
    }

    public IssueSolved() {
        super();
    }
}
//>>> DDD / Domain Event

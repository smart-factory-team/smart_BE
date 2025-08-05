package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class IssueStored extends AbstractEvent {

    private String issue;
    private Enum processType;
    private Enum modelType;
    private String modelLogId;

    public IssueStored(Issue aggregate) {
        super(aggregate);
    }

    public IssueStored() {
        super();
    }
}
//>>> DDD / Domain Event

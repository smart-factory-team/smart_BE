package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class EndTimeEnrolled extends AbstractEvent {

    private Long id;

    public EndTimeEnrolled(AgentSession aggregate) {
        super(aggregate);
    }

    public EndTimeEnrolled() {
        super();
    }
}
//>>> DDD / Domain Event

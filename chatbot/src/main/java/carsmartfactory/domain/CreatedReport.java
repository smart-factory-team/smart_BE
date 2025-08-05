package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class CreatedReport extends AbstractEvent {

    private Long id;

    public CreatedReport(AgentSession aggregate) {
        super(aggregate);
    }

    public CreatedReport() {
        super();
    }
}
//>>> DDD / Domain Event

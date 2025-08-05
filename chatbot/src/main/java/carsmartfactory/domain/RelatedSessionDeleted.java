package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class RelatedSessionDeleted extends AbstractEvent {

    private String chatbotSessionId;
    private String issue;
    private String userId;
    private Boolean isTerminated;

    public RelatedSessionDeleted(AgentSession aggregate) {
        super(aggregate);
    }

    public RelatedSessionDeleted() {
        super();
    }
}
//>>> DDD / Domain Event

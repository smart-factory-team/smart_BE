package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class DialogHistoryStored extends AbstractEvent {

    private String chatbotSessionId;
    private String issue;
    private String userId;
    private Date startedAt;
    private Date endedAt;
    private Boolean isReported;
    private Boolean isTerminated;

    public DialogHistoryStored(AgentSession aggregate) {
        super(aggregate);
    }

    public DialogHistoryStored() {
        super();
    }
}
//>>> DDD / Domain Event

package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ChatbotSessionCreated extends AbstractEvent {

    private String chatbotSessionId;
    private String issue;
    private String userId;
    private Date startedAt;
    private Boolean isReported;
    private Boolean isTerminated;
    private Date endedAt;

    public ChatbotSessionCreated(AgentSession aggregate) {
        super(aggregate);
    }

    public ChatbotSessionCreated() {
        super();
    }
}
//>>> DDD / Domain Event

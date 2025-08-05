package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ReplyCreated extends AbstractEvent {

    private String id;
    private String postId;
    private String userId;
    private String parentId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;

    public ReplyCreated(Comment aggregate) {
        super(aggregate);
    }

    public ReplyCreated() {
        super();
    }
}
//>>> DDD / Domain Event

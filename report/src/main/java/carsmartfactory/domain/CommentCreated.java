package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class CommentCreated extends AbstractEvent {

    private String id;
    private String postId;
    private String userId;
    private String parentId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;

    public CommentCreated(Comment aggregate) {
        super(aggregate);
    }

    public CommentCreated() {
        super();
    }
}
//>>> DDD / Domain Event

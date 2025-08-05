package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PostUpdated extends AbstractEvent {

    private String id;
    private String userId;
    private String title;
    private String content;
    private Category category;
    private Date createdAt;
    private Date updatedAt;
    private String issue;
    private Boolean isSolved;

    public PostUpdated(Post aggregate) {
        super(aggregate);
    }

    public PostUpdated() {
        super();
    }
}
//>>> DDD / Domain Event

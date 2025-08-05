package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class UserProfileUpdated extends AbstractEvent {

    private String id;
    private String email;
    private String name;
    private String department;
    private UserRole role;
    private Boolean isApproved;
    private Date updatedAt;

    public UserProfileUpdated(User aggregate) {
        super(aggregate);
    }

    public UserProfileUpdated() {
        super();
    }
}
//>>> DDD / Domain Event

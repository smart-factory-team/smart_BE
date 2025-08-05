package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class UserRegistrationApproved extends AbstractEvent {

    private String id;
    private String name;
    private String email;
    private String password;
    private String department;
    private Date createdAt;
    private UserRole role;

    public UserRegistrationApproved(UserApproval aggregate) {
        super(aggregate);
    }

    public UserRegistrationApproved() {
        super();
    }
}
//>>> DDD / Domain Event

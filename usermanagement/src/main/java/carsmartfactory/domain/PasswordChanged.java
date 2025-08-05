package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PasswordChanged extends AbstractEvent {

    private String id;
    private String password;

    public PasswordChanged(User aggregate) {
        super(aggregate);
    }

    public PasswordChanged() {
        super();
    }
}
//>>> DDD / Domain Event

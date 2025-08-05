package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class UserRegistered extends AbstractEvent {

    private String id;
    private String name;
    private String email;
    private String password;
    private String department;
    private Date createdAt;
    private Object role;
}

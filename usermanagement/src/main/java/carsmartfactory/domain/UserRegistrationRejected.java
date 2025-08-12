package carsmartfactory.domain;

import carsmartfactory.infra.AbstractEvent;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRegistrationRejected extends AbstractEvent {

    private String id;
    private String name;
    private String email;
    private String password;
    private String department;
    private Date createdAt;
    private UserRole role;  // Object → UserRole 타입 수정
    private String rejectedBy;
    private Date rejectedAt;
    private String rejectionReason;
}
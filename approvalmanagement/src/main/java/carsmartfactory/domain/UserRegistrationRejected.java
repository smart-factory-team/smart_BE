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
    private UserRole role;
    private String rejectedBy;
    private Date rejectedAt;
    private String rejectionReason;

    public UserRegistrationRejected(UserApproval aggregate) {
        super(aggregate);
        this.rejectedBy = aggregate.getRejectedBy();
        this.rejectedAt = aggregate.getRejectedAt();
        this.rejectionReason = aggregate.getRejectionReason();
    }

    public UserRegistrationRejected() {
        super();
    }
}
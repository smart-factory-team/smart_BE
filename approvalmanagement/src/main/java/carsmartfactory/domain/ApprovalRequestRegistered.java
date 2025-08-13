package carsmartfactory.domain;

import carsmartfactory.infra.AbstractEvent;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApprovalRequestRegistered extends AbstractEvent {

    private String id;
    private String name;
    private String email;
    private String department;
    private Date createdAt;
    private UserRole role;
    private String status; // PENDING, APPROVED, REJECTED

    public ApprovalRequestRegistered(UserApproval aggregate) {
        super(aggregate);
        this.status = aggregate.getStatus();
    }

    public ApprovalRequestRegistered() {
        super();
    }
}
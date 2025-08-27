package carsmartfactory.domain;

import carsmartfactory.infra.AbstractEvent;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

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
    private String approvedBy;
    private Date approvedAt;
    private String approvalReason;

    public UserRegistrationApproved(UserApproval aggregate) {
        super(aggregate);
        this.approvedBy = aggregate.getApprovedBy();
        this.approvedAt = aggregate.getApprovedAt();
        this.approvalReason = aggregate.getApprovalReason();
    }

    public UserRegistrationApproved() {
        super();
    }
}
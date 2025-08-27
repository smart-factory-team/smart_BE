package carsmartfactory.domain;

import carsmartfactory.ApprovalmanagementApplication;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "UserApproval_table")
@Data
public class UserApproval {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private String department;

    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String status; // PENDING, APPROVED, REJECTED

    // 승인 관련 필드
    private String approvedBy;
    private Date approvedAt;
    private String approvalReason;

    // 거절 관련 필드
    private String rejectedBy;
    private Date rejectedAt;
    private String rejectionReason;

    @PostPersist
    public void onPostPersist() {
        // 승인 요청 등록 이벤트 발행
        ApprovalRequestRegistered approvalRequestRegistered = new ApprovalRequestRegistered(this);
        approvalRequestRegistered.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        // 상태에 따라 이벤트 발행
        if ("APPROVED".equals(this.status)) {
            UserRegistrationApproved userRegistrationApproved = new UserRegistrationApproved(this);
            userRegistrationApproved.publishAfterCommit();
        } else if ("REJECTED".equals(this.status)) {
            UserRegistrationRejected userRegistrationRejected = new UserRegistrationRejected(this);
            userRegistrationRejected.publishAfterCommit();
        }
    }

    public static UserApprovalRepository repository() {
        UserApprovalRepository userApprovalRepository = ApprovalmanagementApplication.applicationContext.getBean(
                UserApprovalRepository.class
        );
        return userApprovalRepository;
    }

    // 명시적 Getter 메서드들 (Lombok @Data가 작동하지 않을 경우 대비)
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDepartment() {
        return department;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserRole getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public Date getApprovedAt() {
        return approvedAt;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public Date getRejectedAt() {
        return rejectedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    // 명시적 Setter 메서드들
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setApprovedAt(Date approvedAt) {
        this.approvedAt = approvedAt;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public void setRejectedAt(Date rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    // 비즈니스 로직 메서드들
    public void approve(String approvedBy, String reason) {
        this.status = "APPROVED";
        this.approvedBy = approvedBy;
        this.approvedAt = new Date();
        this.approvalReason = reason;
    }

    public void reject(String rejectedBy, String reason) {
        this.status = "REJECTED";
        this.rejectedBy = rejectedBy;
        this.rejectedAt = new Date();
        this.rejectionReason = reason;
    }

    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }

    // UserRegistered 이벤트 수신 시 처리 로직
    public static void handleUserRegistration(UserRegistered userRegistered) {
        System.out.println("##### Handling UserRegistered event: " + userRegistered.getEmail() + " #####");

        try {
            // UserRegistered 이벤트를 UserApproval로 변환
            UserApproval userApproval = new UserApproval();
            userApproval.setId(userRegistered.getId());
            userApproval.setName(userRegistered.getName());
            userApproval.setEmail(userRegistered.getEmail());
            userApproval.setPassword(userRegistered.getPassword());
            userApproval.setDepartment(userRegistered.getDepartment());
            userApproval.setCreatedAt(userRegistered.getCreatedAt());

            // role 처리 (Object -> UserRole)
            if (userRegistered.getRole() instanceof String) {
                userApproval.setRole(UserRole.valueOf((String) userRegistered.getRole()));
            } else if (userRegistered.getRole() instanceof UserRole) {
                userApproval.setRole((UserRole) userRegistered.getRole());
            } else {
                userApproval.setRole(UserRole.EMPLOYEE); // 기본값
            }

            userApproval.setStatus("PENDING"); // 기본 상태는 대기

            // 저장 (ApprovalRequestRegistered 이벤트 자동 발행)
            repository().save(userApproval);

            System.out.println("##### UserApproval created successfully for: " + userApproval.getEmail() + " #####");

        } catch (Exception e) {
            System.err.println("##### Error handling UserRegistered event: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }
}
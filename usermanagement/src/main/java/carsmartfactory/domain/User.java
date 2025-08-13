package carsmartfactory.domain;

import carsmartfactory.UsermanagementApplication;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "User_table")
@Data
//<<< DDD / Aggregate Root
public class User {

    @Id
    private String id;

    private String email;

    private String password;

    private String name;

    private String department;

    @Enumerated(EnumType.STRING)  // UserRole Enum 매핑 추가
    private UserRole role;

    private Boolean isApproved;

    private Date createdAt;

    private Date updatedAt;

    @PostPersist
    public void onPostPersist() {
        UserProfileUpdated userProfileUpdated = new UserProfileUpdated(this);
        userProfileUpdated.publishAfterCommit();

        PasswordChanged passwordChanged = new PasswordChanged(this);
        passwordChanged.publishAfterCommit();
    }

    public static UserRepository repository() {
        UserRepository userRepository = UsermanagementApplication.applicationContext.getBean(
                UserRepository.class
        );
        return userRepository;
    }

    //<<< Clean Arch / Port Method
    public static void enableUserAccount(
            UserRegistrationApproved userRegistrationApproved
    ) {
        System.out.println(
                "##### [User] enableUserAccount called for: " + userRegistrationApproved.getEmail() + " #####");

        try {
            // 이미 User 테이블에 존재하는지 확인
            boolean userExists = repository().existsByEmail(userRegistrationApproved.getEmail());

            if (userExists) {
                System.out.println(
                        "##### [User] User already exists in User table: " + userRegistrationApproved.getEmail()
                                + " #####");
                return;
            }

            // 승인된 사용자를 User 테이블에 추가
            User newUser = new User();
            newUser.setId(userRegistrationApproved.getId());
            newUser.setEmail(userRegistrationApproved.getEmail());
            newUser.setPassword(userRegistrationApproved.getPassword());
            newUser.setName(userRegistrationApproved.getName());
            newUser.setDepartment(userRegistrationApproved.getDepartment());
            newUser.setRole(userRegistrationApproved.getRole());
            newUser.setIsApproved(true);  // 승인된 상태
            newUser.setCreatedAt(userRegistrationApproved.getCreatedAt());
            newUser.setUpdatedAt(new java.util.Date());

            // User 테이블에 저장
            repository().save(newUser);

            System.out.println("##### [User] User account enabled successfully: " + newUser.getEmail() + " #####");

        } catch (Exception e) {
            System.err.println("##### [User] Error enabling user account: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void disableUseAccount(
            UserRegistrationRejected userRegistrationRejected
    ) {
        System.out.println(
                "##### [User] disableUseAccount called for: " + userRegistrationRejected.getEmail() + " #####");

        try {
            // 거절된 사용자는 User 테이블에서 제거 (만약 존재한다면)
            repository().findByEmail(userRegistrationRejected.getEmail())
                    .ifPresent(user -> {
                        repository().delete(user);
                        System.out.println(
                                "##### [User] User account disabled and removed: " + user.getEmail() + " #####");
                    });

            // 거절된 경우에는 User 테이블에 추가하지 않음
            System.out.println("##### [User] User registration rejected, account not created: " +
                    userRegistrationRejected.getEmail() + " reason: " + userRegistrationRejected.getRejectionReason()
                    + " #####");

        } catch (Exception e) {
            System.err.println("##### [User] Error disabling user account: " + e.getMessage() + " #####");
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
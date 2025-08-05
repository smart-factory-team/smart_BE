package carsmartfactory.domain;

import carsmartfactory.ApprovalmanagementApplication;
import carsmartfactory.domain.ApprovalRequestRegistered;
import carsmartfactory.domain.UserRegistrationApproved;
import carsmartfactory.domain.UserRegistrationRejected;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserApproval_table")
@Data
//<<< DDD / Aggregate Root
public class UserApproval {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private String department;

    private Date createdAt;

    private UserRole role;

    @PostPersist
    public void onPostPersist() {
        UserRegistrationApproved userRegistrationApproved = new UserRegistrationApproved(
            this
        );
        userRegistrationApproved.publishAfterCommit();

        UserRegistrationRejected userRegistrationRejected = new UserRegistrationRejected(
            this
        );
        userRegistrationRejected.publishAfterCommit();

        ApprovalRequestRegistered approvalRequestRegistered = new ApprovalRequestRegistered(
            this
        );
        approvalRequestRegistered.publishAfterCommit();
    }

    public static UserApprovalRepository repository() {
        UserApprovalRepository userApprovalRepository = ApprovalmanagementApplication.applicationContext.getBean(
            UserApprovalRepository.class
        );
        return userApprovalRepository;
    }

    //<<< Clean Arch / Port Method
    public static void approveUserRegistration(UserRegistered userRegistered) {
        //implement business logic here:

        /** Example 1:  new item 
        UserApproval userApproval = new UserApproval();
        repository().save(userApproval);

        ApprovalRequestRegistered approvalRequestRegistered = new ApprovalRequestRegistered(userApproval);
        approvalRequestRegistered.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(userRegistered.get???()).ifPresent(userApproval->{
            
            userApproval // do something
            repository().save(userApproval);

            ApprovalRequestRegistered approvalRequestRegistered = new ApprovalRequestRegistered(userApproval);
            approvalRequestRegistered.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

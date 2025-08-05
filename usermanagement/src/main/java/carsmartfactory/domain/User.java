package carsmartfactory.domain;

import carsmartfactory.UsermanagementApplication;
import carsmartfactory.domain.PasswordChanged;
import carsmartfactory.domain.UserProfileUpdated;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
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
        //implement business logic here:

        /** Example 1:  new item 
        User user = new User();
        repository().save(user);

        */

        /** Example 2:  finding and process
        

        repository().findById(userRegistrationApproved.get???()).ifPresent(user->{
            
            user // do something
            repository().save(user);


         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void disableUseAccount(
        UserRegistrationRejected userRegistrationRejected
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        User user = new User();
        repository().save(user);

        */

        /** Example 2:  finding and process
        

        repository().findById(userRegistrationRejected.get???()).ifPresent(user->{
            
            user // do something
            repository().save(user);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

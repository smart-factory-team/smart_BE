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
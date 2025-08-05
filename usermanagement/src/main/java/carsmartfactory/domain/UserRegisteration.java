package carsmartfactory.domain;

import carsmartfactory.UsermanagementApplication;
import carsmartfactory.domain.UserRegistered;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserRegisteration_table")
@Data
//<<< DDD / Aggregate Root
public class UserRegisteration {

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
        UserRegistered userRegistered = new UserRegistered(this);
        userRegistered.publishAfterCommit();
    }

    public static UserRegisterationRepository repository() {
        UserRegisterationRepository userRegisterationRepository = UsermanagementApplication.applicationContext.getBean(
            UserRegisterationRepository.class
        );
        return userRegisterationRepository;
    }
}
//>>> DDD / Aggregate Root

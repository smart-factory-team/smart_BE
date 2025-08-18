package carsmartfactory.domain;

import carsmartfactory.UsermanagementApplication;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "UserRegisteration_table")
@Data
//<<< DDD / Aggregate Root
public class UserRegisteration {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String email;

    private String password;

    private String department;

    private Date createdAt;

    @Enumerated(EnumType.STRING)  // UserRole Enum 매핑 추가
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
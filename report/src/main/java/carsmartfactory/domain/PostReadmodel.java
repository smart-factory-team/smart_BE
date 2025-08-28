package carsmartfactory.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "PostReadmodel_table")
@Data
public class PostReadmodel {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String title;
    private String postId;
    private String createdAt;
    private String username;
}

package carsmartfactory.domain;

import carsmartfactory.ReportApplication;
import carsmartfactory.domain.IssueSolved;
import carsmartfactory.domain.PostCreated;
import carsmartfactory.domain.PostUpdated;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Post_table")
@Data
//<<< DDD / Aggregate Root
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private String title;

    private String content;

    private Category category;

    private Date createdAt;

    // private Date updatedAt;

    // private String issue;

    @Column(columnDefinition = "boolean default false")
    private Boolean isSolved = false;

    @PostPersist
    public void onPostPersist() {
        PostCreated postCreated = new PostCreated(this);
        postCreated.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        PostUpdated postUpdated = new PostUpdated(this);
        postUpdated.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate() {
        IssueSolved issueSolved = new IssueSolved(this);
        issueSolved.publishAfterCommit();
    }

    public static PostRepository repository() {
        PostRepository postRepository = ReportApplication.applicationContext.getBean(
            PostRepository.class
        );
        return postRepository;
    }
}
//>>> DDD / Aggregate Root

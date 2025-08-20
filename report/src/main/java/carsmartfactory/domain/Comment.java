package carsmartfactory.domain;

import carsmartfactory.ReportApplication;
import carsmartfactory.domain.CommentCreated;
import carsmartfactory.domain.ReplyCreated;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Comment_table")
@Data
//<<< DDD / Aggregate Root
public class Comment {

    @Id
    private String id;

    private String postId;

    private String userId;

    private String parentId;

    private String content;

    private Date createdAt;

    private Date updatedAt;

    private Boolean isDeleted;

    @PostPersist
    public void onPostPersist() {
        CommentCreated commentCreated = new CommentCreated(this);
        commentCreated.publishAfterCommit();

        ReplyCreated replyCreated = new ReplyCreated(this);
        replyCreated.publishAfterCommit();
    }

    public static CommentRepository repository() {
        CommentRepository commentRepository = ReportApplication.applicationContext.getBean(
            CommentRepository.class
        );
        return commentRepository;
    }
}
//>>> DDD / Aggregate Root

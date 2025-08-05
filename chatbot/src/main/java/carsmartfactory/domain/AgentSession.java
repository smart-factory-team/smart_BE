package carsmartfactory.domain;

import carsmartfactory.ChatbotApplication;
import carsmartfactory.domain.ChatbotSessionCreated;
import carsmartfactory.domain.CreatedReport;
import carsmartfactory.domain.DialogHistoryStored;
import carsmartfactory.domain.EndTimeEnrolled;
import carsmartfactory.domain.RelatedSessionDeleted;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "AgentSession_table")
@Data
//<<< DDD / Aggregate Root
public class AgentSession {

    @Id
    private String chatbotSessionId;

    private String issue;

    private String userId;

    private Date startedAt;

    private Date endedAt;

    private Boolean isReported;

    private Boolean isTerminated;

    @PostPersist
    public void onPostPersist() {
        ChatbotSessionCreated chatbotSessionCreated = new ChatbotSessionCreated(
            this
        );
        chatbotSessionCreated.publishAfterCommit();

        DialogHistoryStored dialogHistoryStored = new DialogHistoryStored(this);
        dialogHistoryStored.publishAfterCommit();

        EndTimeEnrolled endTimeEnrolled = new EndTimeEnrolled(this);
        endTimeEnrolled.publishAfterCommit();

        RelatedSessionDeleted relatedSessionDeleted = new RelatedSessionDeleted(
            this
        );
        relatedSessionDeleted.publishAfterCommit();

        CreatedReport createdReport = new CreatedReport(this);
        createdReport.publishAfterCommit();
    }

    public static AgentSessionRepository repository() {
        AgentSessionRepository agentSessionRepository = ChatbotApplication.applicationContext.getBean(
            AgentSessionRepository.class
        );
        return agentSessionRepository;
    }

    //<<< Clean Arch / Port Method
    public static void solvedIssue(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item 
        AgentSession agentSession = new AgentSession();
        repository().save(agentSession);

        RelatedSessionDeleted relatedSessionDeleted = new RelatedSessionDeleted(agentSession);
        relatedSessionDeleted.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(issueSolved.get???()).ifPresent(agentSession->{
            
            agentSession // do something
            repository().save(agentSession);

            RelatedSessionDeleted relatedSessionDeleted = new RelatedSessionDeleted(agentSession);
            relatedSessionDeleted.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

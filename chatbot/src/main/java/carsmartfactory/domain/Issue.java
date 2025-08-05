package carsmartfactory.domain;

import carsmartfactory.ChatbotApplication;
import carsmartfactory.domain.IssueStored;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Issue_table")
@Data
//<<< DDD / Aggregate Root
public class Issue {

    @Id
    private String issue;

    private Enum processType;

    private Enum modelType;

    private String modelLogId;

    public static IssueRepository repository() {
        IssueRepository issueRepository = ChatbotApplication.applicationContext.getBean(
            IssueRepository.class
        );
        return issueRepository;
    }

    //<<< Clean Arch / Port Method
    public static void occuredIssue(
        DefectDetectionLogCreated defectDetectionLogCreated
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Issue issue = new Issue();
        repository().save(issue);

        IssueStored issueStored = new IssueStored(issue);
        issueStored.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(defectDetectionLogCreated.get???()).ifPresent(issue->{
            
            issue // do something
            repository().save(issue);

            IssueStored issueStored = new IssueStored(issue);
            issueStored.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void occuredIssue(
        PressFaultDetectionSaved pressFaultDetectionSaved
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Issue issue = new Issue();
        repository().save(issue);

        IssueStored issueStored = new IssueStored(issue);
        issueStored.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(pressFaultDetectionSaved.get???()).ifPresent(issue->{
            
            issue // do something
            repository().save(issue);

            IssueStored issueStored = new IssueStored(issue);
            issueStored.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void occuredIssue(
        PressDefectPredictionSaved pressDefectPredictionSaved
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Issue issue = new Issue();
        repository().save(issue);

        IssueStored issueStored = new IssueStored(issue);
        issueStored.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(pressDefectPredictionSaved.get???()).ifPresent(issue->{
            
            issue // do something
            repository().save(issue);

            IssueStored issueStored = new IssueStored(issue);
            issueStored.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void occuredIssue(
        WeldingMachineDefectSaved weldingMachineDefectSaved
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Issue issue = new Issue();
        repository().save(issue);

        IssueStored issueStored = new IssueStored(issue);
        issueStored.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(weldingMachineDefectSaved.get???()).ifPresent(issue->{
            
            issue // do something
            repository().save(issue);

            IssueStored issueStored = new IssueStored(issue);
            issueStored.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void occuredIssue(
        PaintingProcessEquipmentDefectSaved paintingProcessEquipmentDefectSaved
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Issue issue = new Issue();
        repository().save(issue);

        IssueStored issueStored = new IssueStored(issue);
        issueStored.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(paintingProcessEquipmentDefectSaved.get???()).ifPresent(issue->{
            
            issue // do something
            repository().save(issue);

            IssueStored issueStored = new IssueStored(issue);
            issueStored.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void occuredIssue(
        PaintingSurfaceDefectSaved paintingSurfaceDefectSaved
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Issue issue = new Issue();
        repository().save(issue);

        IssueStored issueStored = new IssueStored(issue);
        issueStored.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(paintingSurfaceDefectSaved.get???()).ifPresent(issue->{
            
            issue // do something
            repository().save(issue);

            IssueStored issueStored = new IssueStored(issue);
            issueStored.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

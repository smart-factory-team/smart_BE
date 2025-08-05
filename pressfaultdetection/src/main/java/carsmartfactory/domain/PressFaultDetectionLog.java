package carsmartfactory.domain;

import carsmartfactory.PressfaultdetectionApplication;
import carsmartfactory.domain.IssueSolved;
import carsmartfactory.domain.PressFaultDetectionSaved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PressFaultDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class PressFaultDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private Long machineId;

    private Date timeStamp;

    private Float ai0Vibration;

    private Float ai1Vibration;

    private Float ai2Current;

    private String issue;

    private Boolean isSolved;

    @PostPersist
    public void onPostPersist() {
        PressFaultDetectionSaved pressFaultDetectionSaved = new PressFaultDetectionSaved(
            this
        );
        pressFaultDetectionSaved.publishAfterCommit();
    }

    public static PressFaultDetectionLogRepository repository() {
        PressFaultDetectionLogRepository pressFaultDetectionLogRepository = PressfaultdetectionApplication.applicationContext.getBean(
            PressFaultDetectionLogRepository.class
        );
        return pressFaultDetectionLogRepository;
    }

    //<<< Clean Arch / Port Method
    public static void issueSolvedPolicy(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item 
        PressFaultDetectionLog pressFaultDetectionLog = new PressFaultDetectionLog();
        repository().save(pressFaultDetectionLog);

        IssueSolved issueSolved = new IssueSolved(pressFaultDetectionLog);
        issueSolved.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(issueSolved.get???()).ifPresent(pressFaultDetectionLog->{
            
            pressFaultDetectionLog // do something
            repository().save(pressFaultDetectionLog);

            IssueSolved issueSolved = new IssueSolved(pressFaultDetectionLog);
            issueSolved.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

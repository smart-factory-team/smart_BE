package carsmartfactory.domain;

import carsmartfactory.PressfaultdetectionApplication;
import carsmartfactory.domain.IssueSolved;
import carsmartfactory.domain.PressDefectPredictionSaved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PressDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class PressDefectDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private Long machineId;

    private Date timeStamp;

    private String machineName;

    private String itemNo;

    private Float pressTime;

    private Float pressure1;

    private Float pressure2;

    private Float pressure3;

    private Integer defectCluster;

    private String defectType;

    private String issue;

    private Boolean isSolved;

    @PostPersist
    public void onPostPersist() {
        PressDefectPredictionSaved pressDefectPredictionSaved = new PressDefectPredictionSaved(
                this
        );
        pressDefectPredictionSaved.publishAfterCommit();
    }

    public static PressDefectDetectionLogRepository repository() {
        PressDefectDetectionLogRepository pressDefectDetectionLogRepository = PressfaultdetectionApplication.applicationContext.getBean(
                PressDefectDetectionLogRepository.class
        );
        return pressDefectDetectionLogRepository;
    }

    //<<< Clean Arch / Port Method
    public static void issueSolvedPolicy(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item 
         PressDefectDetectionLog pressDefectDetectionLog = new PressDefectDetectionLog();
         repository().save(pressDefectDetectionLog);

         IssueSolved issueSolved = new IssueSolved(pressDefectDetectionLog);
         issueSolved.publishAfterCommit();
         */

        /** Example 2:  finding and process


         repository().findById(issueSolved.get???()).ifPresent(pressDefectDetectionLog->{

         pressDefectDetectionLog // do something
         repository().save(pressDefectDetectionLog);

         IssueSolved issueSolved = new IssueSolved(pressDefectDetectionLog);
         issueSolved.publishAfterCommit();

         });
         */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

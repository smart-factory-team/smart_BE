package carsmartfactory.domain;

import carsmartfactory.WeldingprocessmonitoringApplication;
import carsmartfactory.domain.IssueSolved;
import carsmartfactory.domain.WeldingMachineDefectSaved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "WeldingMachineDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class WeldingMachineDefectDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private Long machineId;

    private Date timeStamp;

    private Float sensorValue0Ms;

    private Float sensorValue312Ms;

    private Float sensorValue625Ms;

    private Float sensorValue938Ms;

    private Float sensorValue125Ms;

    private Float sensorValue1562Ms;

    private Float sensorValue1875Ms;

    private Float sensorValue2188Ms;

    private Float sensorValue25Ms;

    private Float sensorValue2812Ms;

    private Float sensorValue3125Ms;

    private Float sensorValue3438Ms;

    private Float sensorValue375Ms;

    private Float sensorValue4062Ms;

    private String issue;

    private Boolean isSolved;

    @PostPersist
    public void onPostPersist() {
        WeldingMachineDefectSaved weldingMachineDefectSaved = new WeldingMachineDefectSaved(
            this
        );
        weldingMachineDefectSaved.publishAfterCommit();
    }

    public static WeldingMachineDefectDetectionLogRepository repository() {
        WeldingMachineDefectDetectionLogRepository weldingMachineDefectDetectionLogRepository = WeldingprocessmonitoringApplication.applicationContext.getBean(
            WeldingMachineDefectDetectionLogRepository.class
        );
        return weldingMachineDefectDetectionLogRepository;
    }

    //<<< Clean Arch / Port Method
    public static void issueSolvedPolicy(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item 
        WeldingMachineDefectDetectionLog weldingMachineDefectDetectionLog = new WeldingMachineDefectDetectionLog();
        repository().save(weldingMachineDefectDetectionLog);

        IssueSolved issueSolved = new IssueSolved(weldingMachineDefectDetectionLog);
        issueSolved.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(issueSolved.get???()).ifPresent(weldingMachineDefectDetectionLog->{
            
            weldingMachineDefectDetectionLog // do something
            repository().save(weldingMachineDefectDetectionLog);

            IssueSolved issueSolved = new IssueSolved(weldingMachineDefectDetectionLog);
            issueSolved.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

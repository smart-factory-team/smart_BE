package carsmartfactory.domain;

import carsmartfactory.PaintingprocessmonitoringApplication;
import carsmartfactory.domain.EquipmentIssueSolved;
import carsmartfactory.domain.PaintingProcessEquipmentDefectSaved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PaintingProcessEquipmentDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class PaintingProcessEquipmentDefectDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String machineId;

    private Date timeStamp;

    private Float thick;

    private Float voltage;

    private Float current;

    private Float temper;

    private String issue;

    private Boolean isSolved;

    @PostPersist
    public void onPostPersist() {
        PaintingProcessEquipmentDefectSaved paintingProcessEquipmentDefectSaved = new PaintingProcessEquipmentDefectSaved(
            this
        );
        paintingProcessEquipmentDefectSaved.publishAfterCommit();
    }

    public static PaintingProcessEquipmentDefectDetectionLogRepository repository() {
        PaintingProcessEquipmentDefectDetectionLogRepository paintingProcessEquipmentDefectDetectionLogRepository = PaintingprocessmonitoringApplication.applicationContext.getBean(
            PaintingProcessEquipmentDefectDetectionLogRepository.class
        );
        return paintingProcessEquipmentDefectDetectionLogRepository;
    }

    //<<< Clean Arch / Port Method
    public static void equipmentIssueSolvedPolicy(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item 
        PaintingProcessEquipmentDefectDetectionLog paintingProcessEquipmentDefectDetectionLog = new PaintingProcessEquipmentDefectDetectionLog();
        repository().save(paintingProcessEquipmentDefectDetectionLog);

        EquipmentIssueSolved equipmentIssueSolved = new EquipmentIssueSolved(paintingProcessEquipmentDefectDetectionLog);
        equipmentIssueSolved.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(issueSolved.get???()).ifPresent(paintingProcessEquipmentDefectDetectionLog->{
            
            paintingProcessEquipmentDefectDetectionLog // do something
            repository().save(paintingProcessEquipmentDefectDetectionLog);

            EquipmentIssueSolved equipmentIssueSolved = new EquipmentIssueSolved(paintingProcessEquipmentDefectDetectionLog);
            equipmentIssueSolved.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

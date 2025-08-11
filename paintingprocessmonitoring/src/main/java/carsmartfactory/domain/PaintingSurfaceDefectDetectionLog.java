package carsmartfactory.domain;

import carsmartfactory.PaintingprocessmonitoringApplication;
import carsmartfactory.domain.PaintingSurfaceDefectSaved;
import carsmartfactory.domain.SurfaceIssueSolved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PaintingSurfaceDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class PaintingSurfaceDefectDetectionLog {

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
        PaintingSurfaceDefectSaved paintingSurfaceDefectSaved = new PaintingSurfaceDefectSaved(
            this
        );
        paintingSurfaceDefectSaved.publishAfterCommit();
    }

    public static PaintingSurfaceDefectDetectionLogRepository repository() {
        PaintingSurfaceDefectDetectionLogRepository paintingSurfaceDefectDetectionLogRepository = PaintingprocessmonitoringApplication.applicationContext.getBean(
            PaintingSurfaceDefectDetectionLogRepository.class
        );
        return paintingSurfaceDefectDetectionLogRepository;
    }

    //<<< Clean Arch / Port Method
    public static void surfaceIssueSolvedPolicy(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item 
        PaintingSurfaceDefectDetectionLog paintingSurfaceDefectDetectionLog = new PaintingSurfaceDefectDetectionLog();
        repository().save(paintingSurfaceDefectDetectionLog);

        SurfaceIssueSolved surfaceIssueSolved = new SurfaceIssueSolved(paintingSurfaceDefectDetectionLog);
        surfaceIssueSolved.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(issueSolved.get???()).ifPresent(paintingSurfaceDefectDetectionLog->{
            
            paintingSurfaceDefectDetectionLog // do something
            repository().save(paintingSurfaceDefectDetectionLog);

            SurfaceIssueSolved surfaceIssueSolved = new SurfaceIssueSolved(paintingSurfaceDefectDetectionLog);
            surfaceIssueSolved.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

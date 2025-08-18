package carsmartfactory.domain;

import carsmartfactory.AssemblyprocessmonitoringApplication;
import carsmartfactory.domain.DefectDetectionLogCreated;
import carsmartfactory.domain.IssueSolved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
// javax → jakarta 패키지 변경
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "VehicleAssemblyProcessDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class VehicleAssemblyProcessDefectDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private Long machineId;

    private Date timeStamp;

    private String part;

    private String work;

    private String category;

    private String imageUrl;

    private String imageName;

    private Long imageWidth;

    private Long imageHeight;

    private String issue;

    private Boolean isSolved;

    @PostPersist
    public void onPostPersist() {
        DefectDetectionLogCreated defectDetectionLogCreated = new DefectDetectionLogCreated(
                this
        );
        defectDetectionLogCreated.publishAfterCommit();
    }

    public static VehicleAssemblyProcessDefectDetectionLogRepository repository() {
        VehicleAssemblyProcessDefectDetectionLogRepository vehicleAssemblyProcessDefectDetectionLogRepository = AssemblyprocessmonitoringApplication.applicationContext.getBean(
                VehicleAssemblyProcessDefectDetectionLogRepository.class
        );
        return vehicleAssemblyProcessDefectDetectionLogRepository;
    }

    //<<< Clean Arch / Port Method
    public static void issueSolvedPolicy(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item
         VehicleAssemblyProcessDefectDetectionLog vehicleAssemblyProcessDefectDetectionLog = new VehicleAssemblyProcessDefectDetectionLog();
         repository().save(vehicleAssemblyProcessDefectDetectionLog);

         IssueSolved issueSolved = new IssueSolved(vehicleAssemblyProcessDefectDetectionLog);
         issueSolved.publishAfterCommit();
         */

        /** Example 2:  finding and process


         repository().findById(issueSolved.get???()).ifPresent(vehicleAssemblyProcessDefectDetectionLog->{

         vehicleAssemblyProcessDefectDetectionLog // do something
         repository().save(vehicleAssemblyProcessDefectDetectionLog);

         IssueSolved issueSolved = new IssueSolved(vehicleAssemblyProcessDefectDetectionLog);
         issueSolved.publishAfterCommit();

         });
         */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
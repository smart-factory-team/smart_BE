package carsmartfactory.domain;

import carsmartfactory.AssemblyprocessmonitoringApplication;

import java.util.Date;
// javax → jakarta 패키지 변경
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "VehicleAssemblyProcessDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class DefectDetectionLog {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private Long machineId;

    @Column(nullable = false)
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
        //defectDetectionLogCreated.publishAfterCommit();
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
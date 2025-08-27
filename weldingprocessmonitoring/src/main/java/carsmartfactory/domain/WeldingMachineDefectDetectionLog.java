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
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "WeldingMachineDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class WeldingMachineDefectDetectionLog {

    @Id
    // ✅ String ID를 위한 UUID 생성 전략
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

    // ✅ ID 자동 생성 (저장 전)
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

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

    // ✅ 기본 setter 메서드들
    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setIsSolved(Boolean isSolved) {
        this.isSolved = isSolved;
    }

    // ✅ 센서 값 setter 메서드들
    public void setSensorValue0Ms(Float sensorValue0Ms) {
        this.sensorValue0Ms = sensorValue0Ms;
    }

    public void setSensorValue25Ms(Float sensorValue25Ms) {
        this.sensorValue25Ms = sensorValue25Ms;
    }

    public void setSensorValue125Ms(Float sensorValue125Ms) {
        this.sensorValue125Ms = sensorValue125Ms;
    }

    public void setSensorValue312Ms(Float sensorValue312Ms) {
        this.sensorValue312Ms = sensorValue312Ms;
    }

    public void setSensorValue375Ms(Float sensorValue375Ms) {
        this.sensorValue375Ms = sensorValue375Ms;
    }

    public void setSensorValue625Ms(Float sensorValue625Ms) {
        this.sensorValue625Ms = sensorValue625Ms;
    }

    public void setSensorValue938Ms(Float sensorValue938Ms) {
        this.sensorValue938Ms = sensorValue938Ms;
    }

    public void setSensorValue1562Ms(Float sensorValue1562Ms) {
        this.sensorValue1562Ms = sensorValue1562Ms;
    }

    public void setSensorValue1875Ms(Float sensorValue1875Ms) {
        this.sensorValue1875Ms = sensorValue1875Ms;
    }

    public void setSensorValue2188Ms(Float sensorValue2188Ms) {
        this.sensorValue2188Ms = sensorValue2188Ms;
    }

    public void setSensorValue2812Ms(Float sensorValue2812Ms) {
        this.sensorValue2812Ms = sensorValue2812Ms;
    }

    public void setSensorValue3125Ms(Float sensorValue3125Ms) {
        this.sensorValue3125Ms = sensorValue3125Ms;
    }

    public void setSensorValue3438Ms(Float sensorValue3438Ms) {
        this.sensorValue3438Ms = sensorValue3438Ms;
    }

    public void setSensorValue4062Ms(Float sensorValue4062Ms) {
        this.sensorValue4062Ms = sensorValue4062Ms;
    }

    // ✅ getter 메서드도 추가
    public String getId() {
        return id;
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
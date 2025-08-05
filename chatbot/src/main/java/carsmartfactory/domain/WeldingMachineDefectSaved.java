package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class WeldingMachineDefectSaved extends AbstractEvent {

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
}

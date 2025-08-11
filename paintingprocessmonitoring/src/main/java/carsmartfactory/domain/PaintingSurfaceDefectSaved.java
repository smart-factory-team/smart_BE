package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PaintingSurfaceDefectSaved extends AbstractEvent {

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

    public PaintingSurfaceDefectSaved(
        PaintingSurfaceDefectDetectionLog aggregate
    ) {
        super(aggregate);
    }

    public PaintingSurfaceDefectSaved() {
        super();
    }
}
//>>> DDD / Domain Event

package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PaintingProcessEquipmentDefectSaved extends AbstractEvent {

    private String id;
    private Long machineId;
    private Date timeStamp;
    private Float thick;
    private Float voltage;
    private Float ampere;
    private Float temper;
    private String issue;
    private Boolean isSolved;

    public PaintingProcessEquipmentDefectSaved(
        PaintingProcessEquipmentDefectDetectionLog aggregate
    ) {
        super(aggregate);
    }

    public PaintingProcessEquipmentDefectSaved() {
        super();
    }
}
//>>> DDD / Domain Event

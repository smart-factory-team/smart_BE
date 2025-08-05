package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.util.*;
import lombok.*;

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
}

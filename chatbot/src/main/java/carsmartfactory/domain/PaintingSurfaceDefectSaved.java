package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.util.*;
import lombok.*;

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
}

package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class DefectDetectionLogCreated extends AbstractEvent {

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
}

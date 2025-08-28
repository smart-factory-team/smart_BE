package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class IssueSolved extends AbstractEvent {

    private String id;
    private String issue;
    private Boolean isSolved;
}

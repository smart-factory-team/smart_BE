package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ReportUploaded extends AbstractEvent {

    private String id;
    private String postId;
    private String reportUrl;

    public ReportUploaded(Report aggregate) {
        super(aggregate);
    }

    public ReportUploaded() {
        super();
    }
}
//>>> DDD / Domain Event

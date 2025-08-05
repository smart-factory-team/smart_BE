package carsmartfactory.domain;

import carsmartfactory.domain.*;
import carsmartfactory.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ReportDownloaded extends AbstractEvent {

    private String id;
    private String reportUrl;

    public ReportDownloaded(Report aggregate) {
        super(aggregate);
    }

    public ReportDownloaded() {
        super();
    }
}
//>>> DDD / Domain Event

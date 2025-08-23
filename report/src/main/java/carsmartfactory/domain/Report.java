package carsmartfactory.domain;

import carsmartfactory.ReportApplication;
import carsmartfactory.domain.ReportDownloaded;
import carsmartfactory.domain.ReportUploaded;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Report_table")
@Data
//<<< DDD / Aggregate Root
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String postId;

    private String reportUrl;

    @PostPersist
    public void onPostPersist() {
        ReportUploaded reportUploaded = new ReportUploaded(this);
        reportUploaded.publishAfterCommit();

        ReportDownloaded reportDownloaded = new ReportDownloaded(this);
        reportDownloaded.publishAfterCommit();
    }

    public static ReportRepository repository() {
        ReportRepository reportRepository = ReportApplication.applicationContext.getBean(
            ReportRepository.class
        );
        return reportRepository;
    }
}
//>>> DDD / Aggregate Root

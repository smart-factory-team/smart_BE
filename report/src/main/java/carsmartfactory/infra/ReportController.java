package carsmartfactory.infra;

import carsmartfactory.domain.*;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/reports")
@Transactional
public class ReportController {


    // 테스트용 하드코딩된 경로
    private static final String UPLOAD_PATH = "C:/Users/User/BigProject/reports";

    @Autowired
    ReportRepository reportRepository;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadReport(
            @RequestParam("file") MultipartFile file,           // 파일
            @RequestParam("postId") String postId) {              // 일반 필드
        
        try {
            // 파일 저장 로직
            String fileName = "report_" + postId + "_" + System.currentTimeMillis() + ".pdf";
            
            file.transferTo(new File(UPLOAD_PATH + "/" + fileName));
            
            // DB 저장
            String fileUrl = "/reports/" + fileName;
            Report report = new Report();
            report.setPostId(postId);
            report.setReportUrl(fileUrl);
            reportRepository.save(report);
            
            return ResponseEntity.ok("Upload successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Resource> getReportByPostId(@PathVariable String postId) {
        List<Report> reports = reportRepository.findByPostId(postId);
        if (reports.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 1:1 매칭이므로 첫 번째 리포트를 사용
        Report report = reports.get(0);
        String fileName = report.getReportUrl().replace("/reports/", "");

        try {
            Path filePath = Paths.get(UPLOAD_PATH).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteReportByPostId(@PathVariable String postId) {
        List<Report> reports = reportRepository.findByPostId(postId);
        if (reports.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            for (Report report : reports) {
                String fileName = report.getReportUrl().replace("/reports/", "");
                Path filePath = Paths.get(UPLOAD_PATH).resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
            }
            
            reportRepository.deleteAll(reports);
            
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
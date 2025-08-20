package carsmartfactory.infra;

import carsmartfactory.domain.*;
import carsmartfactory.domain.DefectDetectionResult;
import carsmartfactory.domain.PaintingSurfaceDefectDetectionService;
import carsmartfactory.domain.ImageReceivedEvent;
import carsmartfactory.domain.DefectDetectedEvent;
import java.util.Optional;

// javax → jakarta 패키지 변경
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/api/painting-surface")
@CrossOrigin(origins = "*")
@Transactional
public class PaintingSurfaceDefectDetectionLogController {

    @Autowired
    PaintingSurfaceDefectDetectionLogRepository paintingSurfaceDefectDetectionLogRepository;
    
    @Autowired
    PaintingSurfaceDefectDetectionService defectDetectionService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * 시뮬레이터로부터 이미지를 받아서 결함 감지 수행
     * @param image 업로드된 이미지 파일
     * @return 결함 감지 결과
     */
    @PostMapping("/defect-detection")
    public ResponseEntity<DefectDetectionResult> detectDefect(
            @RequestParam("image") MultipartFile image) {
        try {
            System.out.println("📥 시뮬레이터로부터 이미지 수신: " + image.getOriginalFilename());
            
            // 1. Spring 내부 이벤트 발행 (이벤트 리스너가 처리)
            ImageReceivedEvent imageReceivedEvent = new ImageReceivedEvent(image);
            eventPublisher.publishEvent(imageReceivedEvent);
            System.out.println("📤 Spring 내부 이미지 수신 이벤트 발행");
            
            // 2. 이벤트 리스너에서 AI 모델 호출 및 DB 저장을 처리하므로
            // 여기서는 즉시 응답만 반환
            DefectDetectionResult result = new DefectDetectionResult(
                "processing", 
                null, 
                1.0, 
                "이미지 수신됨 - AI 모델 처리 중"
            );
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ 결함 감지 API 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DefectDetectionResult("error", e.getMessage()));
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
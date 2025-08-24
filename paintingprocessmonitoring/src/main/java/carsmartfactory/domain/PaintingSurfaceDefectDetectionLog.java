package carsmartfactory.domain;

import carsmartfactory.PaintingprocessmonitoringApplication;
import carsmartfactory.domain.PaintingSurfaceDefectSaved;
import carsmartfactory.domain.SurfaceIssueSolved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
// javax → jakarta 패키지 변경
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PaintingSurfaceDefectDetectionLog_table")
@Data
//<<< DDD / Aggregate Root
public class PaintingSurfaceDefectDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long machineId;

    private Date timeStamp;

    private String machineName;

    private String itemNo;

    // 결함 탐지 관련 핵심 필드들
    private String defectType;        // 결함 유형 (dirt, scratch, water_mark, flow)
    private String issue;             // 이슈 설명
    private Boolean isSolved;         // 해결 여부
    
    // AI 모델 결과 정보
    private Double pressTime;          // AI 모델 신뢰도 (기존 필드명 유지)
    
    // 결함 위치 및 크기 정보 (Double 타입으로 변경)
    private Double defectX;            // 결함 중심점 X 좌표
    private Double defectY;            // 결함 중심점 Y 좌표
    private Double defectWidth;        // 결함 너비
    private Double defectHeight;       // 결함 높이
    private Double defectArea;         // 결함 영역 크기
    private List<Double> defectBbox;        // 바운딩 박스 좌표 (List<Double>로 변경)
    
    // 여러 결함 구분을 위한 필드
    private Integer defectIndex;      // 이미지 내 결함 순서 (0부터 시작)
    private String originalImageName; // 원본 이미지 파일명 (여러 결함을 연결하는 키)

    @PostPersist
    public void onPostPersist() {
        // Kafka 이벤트 발행 활성화
        PaintingSurfaceDefectSaved paintingSurfaceDefectSaved = new PaintingSurfaceDefectSaved(
                this
        );
        paintingSurfaceDefectSaved.publishAfterCommit();
    }

    public static PaintingSurfaceDefectDetectionLogRepository repository() {
        PaintingSurfaceDefectDetectionLogRepository paintingSurfaceDefectDetectionLogRepository = PaintingprocessmonitoringApplication.applicationContext.getBean(
                PaintingSurfaceDefectDetectionLogRepository.class
        );
        return paintingSurfaceDefectDetectionLogRepository;
    }

    //<<< Clean Arch / Port Method
    public static void surfaceIssueSolvedPolicy(IssueSolved issueSolved) {
        //implement business logic here:

        /** Example 1:  new item
         PaintingSurfaceDefectDetectionLog paintingSurfaceDefectDetectionLog = new PaintingSurfaceDefectDetectionLog();
         repository().save(paintingSurfaceDefectDetectionLog);

         SurfaceIssueSolved surfaceIssueSolved = new SurfaceIssueSolved(paintingSurfaceDefectDetectionLog);
         surfaceIssueSolved.publishAfterCommit();
         */

        /** Example 2:  finding and process


         repository().findById(issueSolved.get???()).ifPresent(paintingSurfaceDefectDetectionLog->{

         paintingSurfaceDefectDetectionLog // do something
         repository().save(paintingSurfaceDefectDetectionLog);

         SurfaceIssueSolved surfaceIssueSolved = new SurfaceIssueSolved(paintingSurfaceDefectDetectionLog);
         surfaceIssueSolved.publishAfterCommit();

         });
         */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
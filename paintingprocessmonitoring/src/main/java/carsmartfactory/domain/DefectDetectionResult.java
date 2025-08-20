package carsmartfactory.domain;

import lombok.Data;

@Data
public class DefectDetectionResult {
    private String status;        // "normal", "defect", "error"
    private String defectType;    // "dust", "scratch", "water_mark", "flow"
    private Double confidence;    // 신뢰도 (0.0 ~ 1.0)
    private String message;       // 오류 메시지
    
    // 결함 위치 및 크기 정보 추가
    private Float defectX;        // 결함 중심점 X 좌표
    private Float defectY;        // 결함 중심점 Y 좌표
    private Float defectWidth;    // 결함 너비
    private Float defectHeight;   // 결함 높이
    private Float defectArea;     // 결함 영역 크기
    private String defectBbox;    // 바운딩 박스 좌표
    
    // 여러 결함 구분을 위한 필드
    private Integer defectIndex;  // 이미지 내 결함 순서 (0부터 시작)

    // 기본 생성자
    public DefectDetectionResult() {}
    
    // 간단한 생성자
    public DefectDetectionResult(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // 전체 생성자
    public DefectDetectionResult(String status, String defectType, Double confidence, String message) {
        this.status = status;
        this.defectType = defectType;
        this.confidence = confidence;
        this.message = message;
    }
    
    // 결함 위치 및 크기 정보를 포함하는 생성자 추가
    public DefectDetectionResult(String status, String defectType, Double confidence, String message,
                                Float defectX, Float defectY, Float defectWidth, Float defectHeight, 
                                Float defectArea, String defectBbox) {
        this.status = status;
        this.defectType = defectType;
        this.confidence = confidence;
        this.message = message;
        this.defectX = defectX;
        this.defectY = defectY;
        this.defectWidth = defectWidth;
        this.defectHeight = defectHeight;
        this.defectArea = defectArea;
        this.defectBbox = defectBbox;
    }
    
    // 결함 순서 정보를 포함하는 생성자 추가
    public DefectDetectionResult(String status, String defectType, Double confidence, String message,
                                Float defectX, Float defectY, Float defectWidth, Float defectHeight, 
                                Float defectArea, String defectBbox, Integer defectIndex) {
        this.status = status;
        this.defectType = defectType;
        this.confidence = confidence;
        this.message = message;
        this.defectX = defectX;
        this.defectY = defectY;
        this.defectWidth = defectWidth;
        this.defectHeight = defectHeight;
        this.defectArea = defectArea;
        this.defectBbox = defectBbox;
        this.defectIndex = defectIndex;
    }
}

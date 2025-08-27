package carsmartfactory.infra;

import carsmartfactory.domain.*;
import carsmartfactory.infra.dto.PaintingSurfacePredictionResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PaintingSurfaceEventListener {

    @Autowired
    private PaintingSurfaceModelClient modelClient;

    @Autowired
    private PaintingSurfaceDefectDetectionLogRepository repository;



    @EventListener
    @Transactional
    public void handleImageReceived(ImageReceivedEvent event) {
        
        System.out.println("=== 1단계: 도장표면 이미지 수신 이벤트 처리 ===");
        System.out.println("이미지명: " + event.getImage().getOriginalFilename());
        System.out.println("이미지 크기: " + event.getImage().getSize() + " bytes");
        
        try {
            // 컨트롤러에서 이미 AI 모델을 호출했으므로, 여기서는 DB 저장만 수행
            System.out.println("=== 컨트롤러에서 이미 AI 모델 호출 완료 - DB 저장만 수행 ===");
            
            // 이벤트에서 AI 모델 결과를 가져와서 DB에 저장
            PaintingSurfacePredictionResponseDto prediction = event.getAiModelResult();
            
            if (prediction != null) {
                System.out.println("=== AI 모델 결과 수신 성공 ===");
                
                if (prediction.getPredictions() != null && !prediction.getPredictions().isEmpty()) {
                    System.out.println("=== 결함 감지: " + prediction.getPredictions().size() + "개 결함 발견 - DB 저장 시작 ===");
                    
                    // 모든 결함을 개별 레코드로 저장
                    for (int i = 0; i < prediction.getPredictions().size(); i++) {
                        Map<String, Object> singlePrediction = prediction.getPredictions().get(i);
                        DefectDetectionResult result = convertSinglePrediction(singlePrediction, i);
                        saveDefectDetectionLog(event.getImage(), result, i);
                    }
                    
                    System.out.println("=== 모든 결함 DB 저장 완료 ===");
                    
                } else {
                    System.out.println("=== 정상 상태 - DB 저장 없음 ===");
                    // 정상 이미지는 별도 테이블에 저장하거나 통계에 반영
                    // 현재는 결함 이미지만 DB에 저장됨
                }
                
            } else {
                System.out.println("=== AI 모델 결과 없음 - DB 저장 건너뜀 ===");
            }
            
        } catch (Exception e) {
            System.err.println("=== 이미지 처리 중 오류: " + e.getMessage() + " ===");
            e.printStackTrace();
        }
    }

    /**
     * AI 모델 예측 결과를 DefectDetectionResult로 변환
     */
    private DefectDetectionResult convertPredictionToDefectResult(PaintingSurfacePredictionResponseDto prediction) {
        try {
            if (prediction.getPredictions() == null || prediction.getPredictions().isEmpty()) {
                return new DefectDetectionResult("normal", null, 1.0, "정상 상태 - 결함 없음");
            }
            
            // 첫 번째 결함 정보 추출
            Map<String, Object> firstPrediction = prediction.getPredictions().get(0);
            if (firstPrediction != null) {
                String defectType = (String) firstPrediction.get("class_name");
                Double confidence = (Double) firstPrediction.get("confidence");
                
                // 결함 위치 및 크기 정보 추출
                Double defectX = extractDoubleValue(firstPrediction, "center_x");
                Double defectY = extractDoubleValue(firstPrediction, "center_y");
                Double defectWidth = extractDoubleValue(firstPrediction, "width");
                Double defectHeight = extractDoubleValue(firstPrediction, "height");
                Double defectArea = extractDoubleValue(firstPrediction, "area");
                List<Double> defectBbox = extractBboxList(firstPrediction);
                
                return new DefectDetectionResult(
                    "defect",           // status: 결함 감지
                    defectType,         // defectType: 결함 유형
                    confidence,         // confidence: 신뢰도
                    "결함 감지됨: " + defectType,  // message: 결함 정보
                    defectX, defectY, defectWidth, defectHeight, defectArea, defectBbox  // 위치/크기 정보
                );
            }
            
            return new DefectDetectionResult("normal", null, 1.0, "정상 상태");
            
        } catch (Exception e) {
            System.err.println("=== 예측 결과 변환 중 오류: " + e.getMessage() + " ===");
            return new DefectDetectionResult("error", null, 0.0, "예측 결과 변환 실패: " + e.getMessage());
        }
    }
    
    /**
     * 개별 결함 예측 결과를 DefectDetectionResult로 변환
     */
    private DefectDetectionResult convertSinglePrediction(Map<String, Object> prediction, int index) {
        try {
            String defectType = (String) prediction.get("class_name");
            Double confidence = (Double) prediction.get("confidence");
            
            // bbox에서 위치 및 크기 정보 계산
            Double defectX = null;
            Double defectY = null;
            Double defectWidth = null;
            Double defectHeight = null;
            Double defectArea = extractDoubleValue(prediction, "area");
            List<Double> defectBbox = extractBboxList(prediction);
            
            // bbox가 있으면 center_x, center_y, width, height 계산
            if (defectBbox != null && defectBbox.size() >= 4) {
                try {
                    // bbox = [x1, y1, x2, y2] 형태의 List<Double>
                    Double x1 = defectBbox.get(0);
                    Double y1 = defectBbox.get(1);
                    Double x2 = defectBbox.get(2);
                    Double y2 = defectBbox.get(3);
                    
                    // 중심점 계산
                    defectX = (x1 + x2) / 2.0;
                    defectY = (y1 + y2) / 2.0;
                    
                    // 너비와 높이 계산
                    defectWidth = x2 - x1;
                    defectHeight = y2 - y1;
                } catch (Exception e) {
                    System.out.println("=== bbox 계산 중 오류: " + e.getMessage() + " ===");
                }
            }
            
            return new DefectDetectionResult(
                "defect",           // status: 결함 감지
                defectType,         // defectType: 결함 유형
                confidence,         // confidence: 신뢰도
                "결함 #" + (index + 1) + " 감지됨: " + defectType,  // message: 결함 번호 포함
                defectX, defectY, defectWidth, defectHeight, defectArea, defectBbox,  // 위치/크기 정보
                index               // 결함 순서
            );
            
        } catch (Exception e) {
            System.err.println("=== 개별 결함 변환 중 오류: " + e.getMessage() + " ===");
            return new DefectDetectionResult("error", null, 0.0, "개별 결함 변환 실패: " + e.getMessage());
        }
    }
    
    /**
     * Map에서 Double 값을 안전하게 추출
     */
    private Double extractDoubleValue(Map<String, Object> prediction, String key) {
        try {
            Object value = prediction.get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
        } catch (Exception e) {
            System.out.println("=== " + key + " 값 추출 실패: " + e.getMessage() + " ===");
        }
        return null;
    }
    
    /**
     * 바운딩 박스 정보를 List<Double>로 추출
     */
    @SuppressWarnings("unchecked")
    private List<Double> extractBboxList(Map<String, Object> prediction) {
        try {
            Object bbox = prediction.get("bbox");
            if (bbox instanceof List) {
                List<?> bboxList = (List<?>) bbox;
                if (bboxList.size() >= 4) {
                    // 모든 요소를 Double로 변환
                    List<Double> doubleList = new ArrayList<>();
                    for (Object item : bboxList) {
                        if (item instanceof Number) {
                            doubleList.add(((Number) item).doubleValue());
                        } else if (item instanceof String) {
                            doubleList.add(Double.parseDouble((String) item));
                        }
                    }
                    return doubleList.size() >= 4 ? doubleList : null;
                }
            }
        } catch (Exception e) {
            System.out.println("=== 바운딩 박스 추출 실패: " + e.getMessage() + " ===");
        }
        return null;
    }

    /**
     * 결함 감지 로그를 DB에 저장
     */
    private void saveDefectDetectionLog(MultipartFile image, DefectDetectionResult result, int index) {
        try {
            // 새로운 결함 감지 로그 엔티티 생성
            PaintingSurfaceDefectDetectionLog log = new PaintingSurfaceDefectDetectionLog();
            
            // 기본 정보 설정 (기계별 구분)
            String[] machines = {"도장라인-A", "도장라인-B"};
            String selectedMachine = machines[(int)(Math.random() * machines.length)];
            log.setMachineName(selectedMachine);
            log.setItemNo(image.getOriginalFilename());
            log.setTimeStamp(new Date());
            log.setDefectType(result.getDefectType());
            log.setIssue("Surface defect detected");
            log.setIsSolved(false);
            
            // AI 모델 결과 정보 설정
            if (result.getConfidence() != null) {
                log.setPressTime(result.getConfidence().doubleValue());
            }
            
            // 결함 위치 및 크기 정보 설정
            log.setDefectX(result.getDefectX());
            log.setDefectY(result.getDefectY());
            log.setDefectWidth(result.getDefectWidth());
            log.setDefectHeight(result.getDefectHeight());
            log.setDefectArea(result.getDefectArea());
            log.setDefectBbox(result.getDefectBbox());
            
            // 여러 결함 구분을 위한 정보 설정
            log.setDefectIndex(result.getDefectIndex());
            log.setOriginalImageName(image.getOriginalFilename());
            
            // DB에 저장
            repository.save(log);
            
            System.out.println("=== 로그 저장 완료: " + log.getId() + " ===");
            
        } catch (Exception e) {
            System.err.println("=== 로그 저장 중 오류: " + e.getMessage() + " ===");
            throw new RuntimeException("로그 저장 중 오류: " + e.getMessage());
        }
    }
}

package carsmartfactory.domain.repository;

import carsmartfactory.domain.model.PressDefectDetectionLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 프레스 결함 탐지 로그 Repository 인터페이스
 */
public interface PressDefectDetectionLogRepository {

    /**
     * 결함 탐지 로그 저장
     * 
     * @param log 저장할 로그
     */
    void save(PressDefectDetectionLog log);

    /**
     * 검사 ID로 로그 조회
     * 
     * @param inspectionId 검사 ID
     * @return 결함 탐지 로그
     */
    PressDefectDetectionLog findByInspectionId(String inspectionId);

    /**
     * ID로 로그 조회
     * 
     * @param id 로그 ID
     * @return 결함 탐지 로그 (Optional)
     */
    Optional<PressDefectDetectionLog> findById(Long id);

    /**
     * 모든 결함품 로그 조회
     * 
     * @return 결함품 로그 목록
     */
    List<PressDefectDetectionLog> findAllDefective();

    /**
     * 기간별 결함품 로그 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 해당 기간의 결함품 로그 목록
     */
    List<PressDefectDetectionLog> findDefectiveByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 최근 N개 로그 조회
     * 
     * @param limit 조회할 개수
     * @return 최근 로그 목록
     */
    List<PressDefectDetectionLog> findRecentLogs(int limit);

    /**
     * 검사 ID 존재 여부 확인
     * 
     * @param inspectionId 검사 ID
     * @return 존재 여부
     */
    boolean existsByInspectionId(String inspectionId);

    /**
     * 로그 삭제
     * 
     * @param id 로그 ID
     */
    void deleteById(Long id);

    /**
     * 전체 로그 개수
     * 
     * @return 로그 총 개수
     */
    long count();

    /**
     * 결함품 로그 개수
     * 
     * @return 결함품 로그 개수
     */
    long countDefective();
}
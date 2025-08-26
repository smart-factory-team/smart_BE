package carsmartfactory.infra.repository;

import carsmartfactory.domain.repository.PressDefectDetectionLogRepository;
import carsmartfactory.domain.model.PressDefectDetectionLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프레스 결함 탐지 로그 Repository 구현체
 */
@Slf4j
@Repository
@Transactional
public class PressDefectDetectionLogRepositoryImpl implements PressDefectDetectionLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(PressDefectDetectionLog logEntity) {
        try {
            log.info("DB 저장 시작: {}", logEntity.getInspectionId());
            
            if (logEntity.getId() == null) {
                entityManager.persist(logEntity);
                log.info("새로운 결함 탐지 로그 저장 완료: {}", logEntity.getInspectionId());
            } else {
                entityManager.merge(logEntity);
                log.info("결함 탐지 로그 업데이트 완료: {}", logEntity.getInspectionId());
            }
            
            entityManager.flush();
            
        } catch (Exception e) {
            log.error("결함 탐지 로그 저장 실패: {} - {}", logEntity.getInspectionId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PressDefectDetectionLog findByInspectionId(String inspectionId) {
        try {
            return entityManager.createQuery(
                "SELECT p FROM PressDefectDetectionLog p WHERE p.inspectionId = :inspectionId", 
                PressDefectDetectionLog.class)
                .setParameter("inspectionId", inspectionId)
                .getSingleResult();
        } catch (Exception e) {
            log.debug("결함 탐지 로그 조회 실패: {} - {}", inspectionId, e.getMessage());
            return null;
        }
    }

    @Override
    public java.util.Optional<PressDefectDetectionLog> findById(Long id) {
        try {
            PressDefectDetectionLog log = entityManager.find(PressDefectDetectionLog.class, id);
            return java.util.Optional.ofNullable(log);
        } catch (Exception e) {
            log.debug("결함 탐지 로그 ID 조회 실패: {} - {}", id, e.getMessage());
            return java.util.Optional.empty();
        }
    }

    @Override
    public java.util.List<PressDefectDetectionLog> findAllDefective() {
        try {
            return entityManager.createQuery(
                "SELECT p FROM PressDefectDetectionLog p WHERE p.isDefective = true ORDER BY p.createdAt DESC", 
                PressDefectDetectionLog.class)
                .getResultList();
        } catch (Exception e) {
            log.error("결함품 로그 조회 실패: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public java.util.List<PressDefectDetectionLog> findDefectiveByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        try {
            return entityManager.createQuery(
                "SELECT p FROM PressDefectDetectionLog p WHERE p.isDefective = true AND p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC", 
                PressDefectDetectionLog.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        } catch (Exception e) {
            log.error("기간별 결함품 로그 조회 실패: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public java.util.List<PressDefectDetectionLog> findRecentLogs(int limit) {
        try {
            return entityManager.createQuery(
                "SELECT p FROM PressDefectDetectionLog p ORDER BY p.createdAt DESC", 
                PressDefectDetectionLog.class)
                .setMaxResults(limit)
                .getResultList();
        } catch (Exception e) {
            log.error("최근 로그 조회 실패: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public boolean existsByInspectionId(String inspectionId) {
        try {
            Long count = entityManager.createQuery(
                "SELECT COUNT(p) FROM PressDefectDetectionLog p WHERE p.inspectionId = :inspectionId", 
                Long.class)
                .setParameter("inspectionId", inspectionId)
                .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            log.error("검사 ID 존재 여부 확인 실패: {} - {}", inspectionId, e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            PressDefectDetectionLog logEntity = entityManager.find(PressDefectDetectionLog.class, id);
            if (logEntity != null) {
                entityManager.remove(logEntity);
                entityManager.flush();
                log.info("결함 탐지 로그 삭제 완료: {}", id);
            }
        } catch (Exception e) {
            log.error("결함 탐지 로그 삭제 실패: {} - {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            return entityManager.createQuery("SELECT COUNT(p) FROM PressDefectDetectionLog p", Long.class)
                .getSingleResult();
        } catch (Exception e) {
            log.error("전체 로그 개수 조회 실패: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public long countDefective() {
        try {
            return entityManager.createQuery(
                "SELECT COUNT(p) FROM PressDefectDetectionLog p WHERE p.isDefective = true", 
                Long.class)
                .getSingleResult();
        } catch (Exception e) {
            log.error("결함품 로그 개수 조회 실패: {}", e.getMessage());
            return 0;
        }
    }
}
package carsmartfactory.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "userApprovals", path = "userApprovals")
public interface UserApprovalRepository
        extends PagingAndSortingRepository<UserApproval, String>, JpaRepository<UserApproval, String> {

    /**
     * 이메일로 승인 요청 찾기 REST API: /userApprovals/search/findByEmail?email={email}
     */
    Optional<UserApproval> findByEmail(@Param("email") String email);

    /**
     * 부서별 승인 요청 조회 REST API: /userApprovals/search/findByDepartment?department={department}
     */
    List<UserApproval> findByDepartment(@Param("department") String department);

    /**
     * 상태별 승인 요청 조회 REST API: /userApprovals/search/findByStatus?status={status}
     */
    List<UserApproval> findByStatus(@Param("status") String status);

    /**
     * 승인 대기 중인 요청들 조회 (관리자 대시보드용) REST API: /userApprovals/search/findPendingApprovals
     */
    @Query("SELECT ua FROM UserApproval ua WHERE ua.status = 'PENDING' ORDER BY ua.createdAt ASC")
    List<UserApproval> findPendingApprovals();

    /**
     * 승인된 요청들 조회 REST API: /userApprovals/search/findApprovedRequests
     */
    @Query("SELECT ua FROM UserApproval ua WHERE ua.status = 'APPROVED' ORDER BY ua.approvedAt DESC")
    List<UserApproval> findApprovedRequests();

    /**
     * 거절된 요청들 조회 REST API: /userApprovals/search/findRejectedRequests
     */
    @Query("SELECT ua FROM UserApproval ua WHERE ua.status = 'REJECTED' ORDER BY ua.rejectedAt DESC")
    List<UserApproval> findRejectedRequests();

    /**
     * 특정 부서의 승인 대기 요청들 조회
     */
    @Query("SELECT ua FROM UserApproval ua WHERE ua.department = :department AND ua.status = 'PENDING' ORDER BY ua.createdAt ASC")
    List<UserApproval> findPendingByDepartment(@Param("department") String department);

    /**
     * 특정 승인자가 처리한 요청들 조회
     */
    @Query("SELECT ua FROM UserApproval ua WHERE ua.approvedBy = :approvedBy ORDER BY ua.approvedAt DESC")
    List<UserApproval> findByApprovedBy(@Param("approvedBy") String approvedBy);

    /**
     * 특정 거절자가 처리한 요청들 조회
     */
    @Query("SELECT ua FROM UserApproval ua WHERE ua.rejectedBy = :rejectedBy ORDER BY ua.rejectedAt DESC")
    List<UserApproval> findByRejectedBy(@Param("rejectedBy") String rejectedBy);

    /**
     * 역할별 승인 요청 조회 REST API: /userApprovals/search/findByRole?role={role}
     */
    List<UserApproval> findByRole(@Param("role") UserRole role);

    /**
     * 생성일순 정렬 조회 (최신순) REST API: /userApprovals/search/findAllByOrderByCreatedAtDesc
     */
    List<UserApproval> findAllByOrderByCreatedAtDesc();

    /**
     * 이메일 중복 체크
     */
    boolean existsByEmail(String email);

    /**
     * 특정 기간 내 승인 요청 통계
     */
    @Query("SELECT COUNT(ua) FROM UserApproval ua WHERE ua.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(@Param("startDate") java.util.Date startDate,
                                 @Param("endDate") java.util.Date endDate);

    /**
     * 상태별 요청 수 조회
     */
    @Query("SELECT COUNT(ua) FROM UserApproval ua WHERE ua.status = :status")
    long countByStatus(@Param("status") String status);
}
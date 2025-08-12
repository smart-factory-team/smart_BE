package carsmartfactory.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "userRegisterations", path = "userRegisterations")
public interface UserRegisterationRepository
        extends PagingAndSortingRepository<UserRegisteration, String>, JpaRepository<UserRegisteration, String> {

    /**
     * 이메일로 회원가입 신청 찾기 REST API: /userRegisterations/search/findByEmail?email={email}
     */
    Optional<UserRegisteration> findByEmail(@Param("email") String email);

    /**
     * 부서별 회원가입 신청 조회 REST API: /userRegisterations/search/findByDepartment?department={department}
     */
    List<UserRegisteration> findByDepartment(@Param("department") String department);

    /**
     * 생성일순 정렬 조회 (최신순) REST API: /userRegisterations/search/findAllByOrderByCreatedAtDesc
     */
    List<UserRegisteration> findAllByOrderByCreatedAtDesc();

    /**
     * 이메일 중복 체크 (회원가입 시 사용)
     */
    boolean existsByEmail(String email);

    /**
     * 승인 대기 중인 신청들 조회 (관리자 대시보드용)
     */
    @Query("SELECT ur FROM UserRegisteration ur ORDER BY ur.createdAt ASC")
    List<UserRegisteration> findPendingRegistrations();

    /**
     * 특정 부서의 최근 신청들 조회
     */
    @Query("SELECT ur FROM UserRegisteration ur WHERE ur.department = :department ORDER BY ur.createdAt DESC")
    List<UserRegisteration> findRecentByDepartment(@Param("department") String department);
}
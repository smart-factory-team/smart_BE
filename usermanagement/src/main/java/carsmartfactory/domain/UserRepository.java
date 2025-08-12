package carsmartfactory.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository
        extends PagingAndSortingRepository<User, String>, JpaRepository<User, String> {

    /**
     * 이메일로 사용자 찾기 REST API: /users/search/findByEmail?email={email}
     */
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 부서별 사용자 조회 REST API: /users/search/findByDepartment?department={department}
     */
    List<User> findByDepartment(@Param("department") String department);

    /**
     * 승인된 사용자만 조회 REST API: /users/search/findByIsApprovedTrue
     */
    List<User> findByIsApprovedTrue();

    /**
     * 역할별 사용자 조회 REST API: /users/search/findByRole?role={role}
     */
    List<User> findByRole(@Param("role") UserRole role);

    /**
     * 이메일 중복 체크 (내부 로직용)
     */
    boolean existsByEmail(String email);

    /**
     * 승인된 활성 사용자 조회 (로그인 시 사용)
     */
    @Query("SELECT u FROM User u WHERE u.isApproved = true AND u.email = :email")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    /**
     * 관리자 권한 사용자 조회 (승인 프로세스용)
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.isApproved = true")
    List<User> findActiveAdmins();
}
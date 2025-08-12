package carsmartfactory.infra;

import carsmartfactory.domain.UserRegisteration;
import carsmartfactory.domain.UserRegisterationRepository;
import carsmartfactory.domain.UserRepository;
import carsmartfactory.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
public class UserRegisterationController {

    @Autowired
    UserRegisterationRepository userRegisterationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 회원가입 신청 생성
     */
    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8"
    )
    public ResponseEntity<UserRegisteration> createRegistration(
            @RequestBody UserRegisteration userRegisteration,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### /register POST called #####");

        try {
            // 이메일 중복 체크 (User 테이블과 UserRegisteration 테이블 모두)
            if (userRepository.existsByEmail(userRegisteration.getEmail()) ||
                    userRegisterationRepository.existsByEmail(userRegisteration.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
            }

            // ID 자동 생성
            if (userRegisteration.getId() == null) {
                userRegisteration.setId(UUID.randomUUID().toString());
            }

            // 생성 시간 설정
            if (userRegisteration.getCreatedAt() == null) {
                userRegisteration.setCreatedAt(new Date());
            }

            // 기본 역할 설정
            if (userRegisteration.getRole() == null) {
                userRegisteration.setRole(UserRole.EMPLOYEE);
            }

            // 비밀번호 암호화
            if (userRegisteration.getPassword() != null) {
                String encodedPassword = passwordEncoder.encode(userRegisteration.getPassword());
                userRegisteration.setPassword(encodedPassword);
            }

            // 저장 (UserRegistered 이벤트 자동 발행)
            UserRegisteration saved = userRegisterationRepository.save(userRegisteration);

            System.out.println("##### User registration created - Email: " + saved.getEmail() +
                    ", UserRegistered event will be published #####");

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            System.err.println("##### Error creating user registration: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 회원가입 신청 수정
     */
    @RequestMapping(
            value = "/userRegisterations/{id}",
            method = RequestMethod.PUT,
            produces = "application/json;charset=UTF-8"
    )
    public ResponseEntity<UserRegisteration> updateRegistration(
            @PathVariable(value = "id") String id,
            @RequestBody UserRegisteration userRegisteration,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### /userRegisterations PUT called #####");

        try {
            UserRegisteration existingRegistration = userRegisterationRepository.findById(id).orElse(null);
            if (existingRegistration == null) {
                return ResponseEntity.notFound().build();
            }

            // 업데이트 가능한 필드들
            if (userRegisteration.getName() != null) {
                existingRegistration.setName(userRegisteration.getName());
            }
            if (userRegisteration.getDepartment() != null) {
                existingRegistration.setDepartment(userRegisteration.getDepartment());
            }
            if (userRegisteration.getRole() != null) {
                existingRegistration.setRole(userRegisteration.getRole());
            }

            // 비밀번호 변경 시 암호화
            if (userRegisteration.getPassword() != null) {
                String encodedPassword = passwordEncoder.encode(userRegisteration.getPassword());
                existingRegistration.setPassword(encodedPassword);
            }

            // 이메일은 중복 체크 후 변경
            if (userRegisteration.getEmail() != null &&
                    !userRegisteration.getEmail().equals(existingRegistration.getEmail())) {
                if (userRepository.existsByEmail(userRegisteration.getEmail()) ||
                        userRegisterationRepository.existsByEmail(userRegisteration.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                existingRegistration.setEmail(userRegisteration.getEmail());
            }

            UserRegisteration updated = userRegisterationRepository.save(existingRegistration);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            System.err.println("##### Error updating user registration: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 회원가입 신청 조회 (본인 또는 관리자)
     */
    @RequestMapping(
            value = "/userRegisterations/{id}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8"
    )
    public ResponseEntity<UserRegisteration> getRegistration(
            @PathVariable(value = "id") String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### /userRegisterations/{id} GET called #####");

        try {
            UserRegisteration registration = userRegisterationRepository.findById(id).orElse(null);
            if (registration == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(registration);

        } catch (Exception e) {
            System.err.println("##### Error getting user registration: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 본인 회원가입 신청 삭제 (취소)
     */
    @RequestMapping(
            value = "/userRegisterations/{id}",
            method = RequestMethod.DELETE,
            produces = "application/json;charset=UTF-8"
    )
    public ResponseEntity<String> cancelRegistration(
            @PathVariable(value = "id") String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### /userRegisterations/{id} DELETE called #####");

        try {
            UserRegisteration registration = userRegisterationRepository.findById(id).orElse(null);
            if (registration == null) {
                return ResponseEntity.notFound().build();
            }

            userRegisterationRepository.delete(registration);

            System.out.println("##### User registration cancelled - Email: " + registration.getEmail() + " #####");
            return ResponseEntity.ok("Registration cancelled successfully");

        } catch (Exception e) {
            System.err.println("##### Error cancelling user registration: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error cancelling registration");
        }
    }

}
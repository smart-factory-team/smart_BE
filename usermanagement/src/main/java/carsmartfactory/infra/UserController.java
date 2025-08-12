package carsmartfactory.infra;

import carsmartfactory.domain.User;
import carsmartfactory.domain.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Date;
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
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    /**
     * 사용자 정보 수정
     */
    @RequestMapping(
            value = "/users/{id}/",
            method = RequestMethod.PUT,
            produces = "application/json;charset=UTF-8"
    )
    public User update(
            @PathVariable(value = "id") String id,
            @RequestBody User user,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /user/update called #####");

        // 기존 사용자 조회
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new Exception("User not found with id: " + id);
        }

        // 업데이트 로직
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getDepartment() != null) {
            existingUser.setDepartment(user.getDepartment());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getIsApproved() != null) {
            existingUser.setIsApproved(user.getIsApproved());
        }

        // 업데이트 시간 설정
        existingUser.setUpdatedAt(new Date());

        // 저장 (UserProfileUpdated 이벤트 자동 발행)
        userRepository.save(existingUser);
        return existingUser;
    }

    /**
     * 비밀번호 변경 API
     */
    @RequestMapping(
            value = "/users/{id}/password",
            method = RequestMethod.PUT,
            produces = "application/json;charset=UTF-8"
    )
    public ResponseEntity<String> changePassword(
            @PathVariable(value = "id") String id,
            @RequestBody PasswordChangeRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        System.out.println("##### /users/{id}/password called #####");

        try {
            // 사용자 조회
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // 현재 비밀번호 확인 (선택사항)
            if (request.getCurrentPassword() != null) {
                if (!passwordEncoder.matches(request.getCurrentPassword(), existingUser.getPassword())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Current password is incorrect");
                }
            }

            // 새 비밀번호 암호화 및 저장
            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
            existingUser.setPassword(encodedNewPassword);
            existingUser.setUpdatedAt(new Date());

            // 저장 (PasswordChanged 이벤트 자동 발행)
            userRepository.save(existingUser);

            return ResponseEntity.ok("Password changed successfully");

        } catch (Exception e) {
            System.err.println("##### Error changing password: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error changing password");
        }
    }

    /**
     * 사용자 승인 처리 (관리자용) - approvalmanagement로 이동 예정
     */
    @RequestMapping(
            value = "/users/{id}/approve",
            method = RequestMethod.PUT,
            produces = "application/json;charset=UTF-8"
    )
    public User approveUser(
            @PathVariable(value = "id") String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /user/approve called #####");

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new Exception("User not found with id: " + id);
        }

        user.setIsApproved(true);
        user.setUpdatedAt(new Date());
        userRepository.save(user);

        return user;
    }

    /**
     * 사용자 목록 조회 (관리자용)
     */
    @RequestMapping(
            value = "/users/management/list",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8"
    )
    public Iterable<User> getUserList(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### /users/management/list called #####");
        return userRepository.findAll();
    }

    /**
     * 비밀번호 변경 요청 DTO
     */
    public static class PasswordChangeRequest {
        private String currentPassword;  // 현재 비밀번호 (선택사항)
        private String newPassword;      // 새 비밀번호

        // Getters and Setters
        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
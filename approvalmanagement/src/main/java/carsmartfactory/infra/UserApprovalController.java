package carsmartfactory.infra;

import carsmartfactory.domain.UserApproval;
import carsmartfactory.domain.UserApprovalRepository;
import carsmartfactory.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approvals")
@Transactional
public class UserApprovalController {

    @Autowired
    UserApprovalRepository userApprovalRepository;

    @Autowired
    PolicyHandler policyHandler;

    /**
     * 승인 대기 목록 조회 (관리자용)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<UserApproval>> getPendingApprovals(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### GET /api/approvals/pending called #####");

        try {
            List<UserApproval> pendingApprovals = userApprovalRepository.findPendingApprovals();
            return ResponseEntity.ok(pendingApprovals);

        } catch (Exception e) {
            System.err.println("##### Error getting pending approvals: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 승인 요청 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserApproval> getApprovalDetail(
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### GET /api/approvals/{id} called - ID: " + id + " #####");

        try {
            UserApproval userApproval = userApprovalRepository.findById(id).orElse(null);
            if (userApproval == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(userApproval);

        } catch (Exception e) {
            System.err.println("##### Error getting approval detail: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 사용자 승인 처리
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApprovalResponse> approveUser(
            @PathVariable("id") String id,
            @Valid @RequestBody ApprovalRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        System.out.println("##### PUT /api/approvals/{id}/approve called - ID: " + id + " #####");

        try {
            UserApproval userApproval = userApprovalRepository.findById(id).orElse(null);
            if (userApproval == null) {
                return ResponseEntity.notFound().build();
            }

            // 이미 처리된 요청인지 확인
            if (!userApproval.isPending()) {
                return ResponseEntity.badRequest()
                        .body(new ApprovalResponse(false, "This request has already been processed", userApproval));
            }

            // 승인 처리
            userApproval.approve(request.getProcessedBy(), request.getReason());
            userApprovalRepository.save(userApproval);

            System.out.println("##### User approved successfully: " + userApproval.getEmail() +
                    " by " + request.getProcessedBy() + " #####");

            return ResponseEntity.ok(new ApprovalResponse(true, "User approved successfully", userApproval));

        } catch (Exception e) {
            System.err.println("##### Error approving user: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApprovalResponse(false, "Error processing approval: " + e.getMessage(), null));
        }
    }

    /**
     * 사용자 거절 처리
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApprovalResponse> rejectUser(
            @PathVariable("id") String id,
            @Valid @RequestBody ApprovalRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        System.out.println("##### PUT /api/approvals/{id}/reject called - ID: " + id + " #####");

        try {
            UserApproval userApproval = userApprovalRepository.findById(id).orElse(null);
            if (userApproval == null) {
                return ResponseEntity.notFound().build();
            }

            // 이미 처리된 요청인지 확인
            if (!userApproval.isPending()) {
                return ResponseEntity.badRequest()
                        .body(new ApprovalResponse(false, "This request has already been processed", userApproval));
            }

            // 거절 사유 필수 체크
            if (request.getReason() == null || request.getReason().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApprovalResponse(false, "Rejection reason is required", null));
            }

            // 거절 처리
            userApproval.reject(request.getProcessedBy(), request.getReason());
            userApprovalRepository.save(userApproval);

            System.out.println("##### User rejected successfully: " + userApproval.getEmail() +
                    " by " + request.getProcessedBy() + " reason: " + request.getReason() + " #####");

            return ResponseEntity.ok(new ApprovalResponse(true, "User rejected successfully", userApproval));

        } catch (Exception e) {
            System.err.println("##### Error rejecting user: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApprovalResponse(false, "Error processing rejection: " + e.getMessage(), null));
        }
    }

    /**
     * 승인 내역 조회 (관리자용)
     */
    @GetMapping("/approved")
    public ResponseEntity<List<UserApproval>> getApprovedRequests(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### GET /api/approvals/approved called #####");

        try {
            List<UserApproval> approvedRequests = userApprovalRepository.findApprovedRequests();
            return ResponseEntity.ok(approvedRequests);

        } catch (Exception e) {
            System.err.println("##### Error getting approved requests: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 거절 내역 조회 (관리자용)
     */
    @GetMapping("/rejected")
    public ResponseEntity<List<UserApproval>> getRejectedRequests(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### GET /api/approvals/rejected called #####");

        try {
            List<UserApproval> rejectedRequests = userApprovalRepository.findRejectedRequests();
            return ResponseEntity.ok(rejectedRequests);

        } catch (Exception e) {
            System.err.println("##### Error getting rejected requests: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 부서별 승인 대기 목록 조회
     */
    @GetMapping("/pending/department/{department}")
    public ResponseEntity<List<UserApproval>> getPendingByDepartment(
            @PathVariable("department") String department,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println(
                "##### GET /api/approvals/pending/department/{department} called - Dept: " + department + " #####");

        try {
            List<UserApproval> pendingByDept = userApprovalRepository.findPendingByDepartment(department);
            return ResponseEntity.ok(pendingByDept);

        } catch (Exception e) {
            System.err.println("##### Error getting pending requests by department: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 승인 요청 통계 조회 (관리자 대시보드용)
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApprovalStatistics> getApprovalStatistics(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        System.out.println("##### GET /api/approvals/statistics called #####");

        try {
            long pendingCount = userApprovalRepository.countByStatus("PENDING");
            long approvedCount = userApprovalRepository.countByStatus("APPROVED");
            long rejectedCount = userApprovalRepository.countByStatus("REJECTED");

            ApprovalStatistics statistics = new ApprovalStatistics(
                    pendingCount, approvedCount, rejectedCount, pendingCount + approvedCount + rejectedCount
            );

            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            System.err.println("##### Error getting approval statistics: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 일괄 승인 처리 (선택된 여러 요청 한번에 승인)
     */
    @PutMapping("/batch/approve")
    public ResponseEntity<BatchApprovalResponse> batchApprove(
            @Valid @RequestBody BatchApprovalRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        System.out.println("##### PUT /api/approvals/batch/approve called #####");

        try {
            int successCount = 0;
            int failCount = 0;

            for (String id : request.getIds()) {
                try {
                    UserApproval userApproval = userApprovalRepository.findById(id).orElse(null);
                    if (userApproval != null && userApproval.isPending()) {
                        userApproval.approve(request.getProcessedBy(), request.getReason());
                        userApprovalRepository.save(userApproval);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    System.err.println("##### Error in batch approval for ID " + id + ": " + e.getMessage() + " #####");
                }
            }

            BatchApprovalResponse batchResponse = new BatchApprovalResponse(
                    successCount, failCount, "Batch approval completed"
            );

            return ResponseEntity.ok(batchResponse);

        } catch (Exception e) {
            System.err.println("##### Error in batch approval: " + e.getMessage() + " #####");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BatchApprovalResponse(0, request.getIds().size(),
                            "Batch approval failed: " + e.getMessage()));
        }
    }

    // DTO 클래스들
    public static class ApprovalRequest {
        private String processedBy;
        private String reason;

        public String getProcessedBy() {
            return processedBy;
        }

        public void setProcessedBy(String processedBy) {
            this.processedBy = processedBy;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class ApprovalResponse {
        private boolean success;
        private String message;
        private UserApproval data;

        public ApprovalResponse(boolean success, String message, UserApproval data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public UserApproval getData() {
            return data;
        }
    }

    public static class ApprovalStatistics {
        private long pendingCount;
        private long approvedCount;
        private long rejectedCount;
        private long totalCount;

        public ApprovalStatistics(long pendingCount, long approvedCount, long rejectedCount, long totalCount) {
            this.pendingCount = pendingCount;
            this.approvedCount = approvedCount;
            this.rejectedCount = rejectedCount;
            this.totalCount = totalCount;
        }

        public long getPendingCount() {
            return pendingCount;
        }

        public long getApprovedCount() {
            return approvedCount;
        }

        public long getRejectedCount() {
            return rejectedCount;
        }

        public long getTotalCount() {
            return totalCount;
        }
    }

    public static class BatchApprovalRequest {
        private List<String> ids;
        private String processedBy;
        private String reason;

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }

        public String getProcessedBy() {
            return processedBy;
        }

        public void setProcessedBy(String processedBy) {
            this.processedBy = processedBy;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class BatchApprovalResponse {
        private int successCount;
        private int failCount;
        private String message;

        public BatchApprovalResponse(int successCount, int failCount, String message) {
            this.successCount = successCount;
            this.failCount = failCount;
            this.message = message;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailCount() {
            return failCount;
        }

        public String getMessage() {
            return message;
        }
    }
}
package carsmartfactory.domain;

/**
 * 사용자 역할 정의 - EMPLOYEE: 일반 직원 (승인 후 사용 가능) - ADMIN: 관리자 (승인 권한 보유)
 */
public enum UserRole {
    /**
     * 일반 직원
     */
    EMPLOYEE("EMPLOYEE", "일반 직원"),

    /**
     * 관리자 (승인 권한)
     */
    ADMIN("ADMIN", "관리자");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole code: " + code);
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean canApproveUsers() {
        return this == ADMIN;
    }
}
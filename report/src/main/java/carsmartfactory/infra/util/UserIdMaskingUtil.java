package carsmartfactory.infra.util;

import org.springframework.stereotype.Component;

@Component
public class UserIdMaskingUtil {
    //문자열 마스킹
    public String maskString(String userId) {
        if (userId == null || userId.length() <= 2) {
            return userId;
        }
        
        int length = userId.length();
        if (length <= 4) {
            return userId.charAt(0) + "*".repeat(length - 1);
        }
        
        return userId.charAt(0) + "*".repeat(length - 2) + userId.charAt(length - 1);
    }
}

package carsmartfactory.domain;

import carsmartfactory.infra.AbstractEvent;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRegistered extends AbstractEvent {

    private String id;
    private String name;
    private String email;
    private String password;
    private String department;
    private Date createdAt;
    private Object role;

    // Getter 메서드들은 @Data 어노테이션으로 자동 생성됩니다
    // 하지만 명시적으로 추가해도 됩니다

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDepartment() {
        return department;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Object getRole() {
        return role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setRole(Object role) {
        this.role = role;
    }
}
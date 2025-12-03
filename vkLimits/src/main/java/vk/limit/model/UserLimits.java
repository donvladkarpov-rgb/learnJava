package vk.limit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USER_LIMITS")
public class UserLimits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_LIMITS_ID")
    private Long userLimitsId;

    @Column(name = "USER_LIMITS_USER", nullable = false, length = 32, unique = true)
    private String userLimitsUser;

    @Column(name = "USER_LIMIT", nullable = false)
    private Long userLimit;

    @OneToOne(mappedBy = "userLimits", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserLimitsRollback rollback;

    // Конструкторы
    public UserLimits() {
    }

    public UserLimits(String userLimitsUser, Long userLimit) {
        this.userLimitsUser = userLimitsUser;
        this.userLimit = userLimit;
    }

    // Геттеры и сеттеры
    public Long getUserLimitsId() {
        return userLimitsId;
    }

    public void setUserLimitsId(Long userLimitsId) {
        this.userLimitsId = userLimitsId;
    }

    public String getUserLimitsUser() {
        return userLimitsUser;
    }

    public void setUserLimitsUser(String userLimitsUser) {
        this.userLimitsUser = userLimitsUser;
    }

    public Long getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(Long userLimit) {
        this.userLimit = userLimit;
    }

    public UserLimitsRollback getRollback() {
        return rollback;
    }

    public void setRollback(UserLimitsRollback rollback) {
        this.rollback = rollback;
    }
}
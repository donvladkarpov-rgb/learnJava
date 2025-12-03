package vk.limit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USER_LIMITS_ROLLBACK")
public class UserLimitsRollback {

    @Id
    @Column(name = "USER_LIMITS_ID")
    private Long userLimitsId;

    @Column(name = "USER_LIMIT", nullable = false)
    private Long userLimit;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "USER_LIMITS_ID")
    private UserLimits userLimits;

    // Конструкторы
    public UserLimitsRollback() {
    }

    public UserLimitsRollback(UserLimits userLimits, Long userLimit) {
        this.userLimits = userLimits;
        this.userLimitsId = userLimits.getUserLimitsId();
        this.userLimit = userLimit;
    }

    // Геттеры и сеттеры
    public Long getUserLimitsId() {
        return userLimitsId;
    }

    public void setUserLimitsId(Long userLimitsId) {
        this.userLimitsId = userLimitsId;
    }

    public Long getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(Long userLimit) {
        this.userLimit = userLimit;
    }

    public UserLimits getUserLimits() {
        return userLimits;
    }

    public void setUserLimits(UserLimits userLimits) {
        this.userLimits = userLimits;
        this.userLimitsId = userLimits.getUserLimitsId();
    }

}
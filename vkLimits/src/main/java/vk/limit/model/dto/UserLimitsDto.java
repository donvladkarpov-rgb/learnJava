package vk.limit.model.dto;

public class UserLimitsDto {
    private Long userLimitsId;
    private String userLimitsUser;
    private Long userLimit;
    private Long previousLimit; // Значение из rollback

    // Конструкторы
    public UserLimitsDto() {}

    public UserLimitsDto(String userLimitsUser, Long userLimit) {
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

    public Long getPreviousLimit() {
        return previousLimit;
    }

    public void setPreviousLimit(Long previousLimit) {
        this.previousLimit = previousLimit;
    }
}
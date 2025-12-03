package vk.limit.model.dto;

public class UserLimitUpdateDto {
    private Long userLimit;

    // Конструкторы
    public UserLimitUpdateDto() {}

    public UserLimitUpdateDto(Long userLimit) {
        this.userLimit = userLimit;
    }

    // Геттеры и сеттеры
    public Long getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(Long userLimit) {
        this.userLimit = userLimit;
    }

}
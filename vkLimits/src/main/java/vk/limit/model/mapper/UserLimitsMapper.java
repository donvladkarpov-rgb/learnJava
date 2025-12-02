package vk.limit.model.mapper;

import vk.limit.model.Transaction;
import vk.limit.model.dto.TransactionInfo;
import vk.limit.model.dto.UserLimitsDto;
import vk.limit.model.UserLimits;
import vk.limit.model.UserLimitsRollback;
import org.springframework.stereotype.Component;

@Component
public class UserLimitsMapper {

    // Entity -> DTO
    public UserLimitsDto toDto(UserLimits entity) {
        if (entity == null) {
            return null;
        }

        UserLimitsDto dto = new UserLimitsDto();
        dto.setUserLimitsId(entity.getUserLimitsId());
        dto.setUserLimitsUser(entity.getUserLimitsUser());
        dto.setUserLimit(entity.getUserLimit());

        // Добавляем значение из rollback, если оно существует
        if (entity.getRollback() != null) {
            dto.setPreviousLimit(entity.getRollback().getUserLimit());
        }

        return dto;
    }

    // DTO -> Entity (без ID, для создания)
    public UserLimits toEntity(UserLimitsDto dto) {
        if (dto == null) {
            return null;
        }

        UserLimits entity = new UserLimits();
        entity.setUserLimitsUser(dto.getUserLimitsUser());
        entity.setUserLimit(dto.getUserLimit());
        return entity;
    }

    // DTO -> Entity (с ID, для обновления)
    public UserLimits toEntityWithId(UserLimitsDto dto) {
        if (dto == null) {
            return null;
        }

        UserLimits entity = toEntity(dto);
        entity.setUserLimitsId(dto.getUserLimitsId());
        return entity;
    }

    // Создание rollback из entity и значения
    public UserLimitsRollback createRollback(UserLimits entity, Long oldLimit) {
        if (entity == null || oldLimit == null) {
            return null;
        }

        UserLimitsRollback rollback = new UserLimitsRollback();
        rollback.setUserLimits(entity);
        rollback.setUserLimit(oldLimit);
        return rollback;
    }

    // Обновление entity из DTO (частичное обновление)
    public void updateEntityFromDto(UserLimits entity, UserLimitsDto dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getUserLimitsUser() != null) {
            entity.setUserLimitsUser(dto.getUserLimitsUser());
        }

        if (dto.getUserLimit() != null) {
            entity.setUserLimit(dto.getUserLimit());
        }
    }

    public static Transaction toEntity(TransactionInfo dto) {
        return new Transaction(
                dto.getTransactionId(),
                dto.getUsername(),
                dto.getAmount(),
                dto.getPreviousLimit(),
                dto.getCreatedAt(),
                dto.getStatus()
        );
    }

    public static TransactionInfo toDto(Transaction entity) {
        return new TransactionInfo(
                entity.getTransactionId(),
                entity.getUsername(),
                entity.getAmount(),
                entity.getPreviousLimit(),
                entity.getCreatedAt(),
                entity.getStatus()
        );
    }

}
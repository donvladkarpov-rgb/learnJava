package vk.limit.repository;

import vk.limit.model.UserLimitsRollback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLimitsRollbackRepository extends JpaRepository<UserLimitsRollback, Long> {
    Optional<UserLimitsRollback> findByUserLimits_UserLimitsUser(String username);
}
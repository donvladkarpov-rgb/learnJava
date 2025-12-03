package vk.limit.repository;

import vk.limit.model.UserLimits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLimitsRepository extends JpaRepository<UserLimits, Long> {
    Optional<UserLimits> findByUserLimitsUser(String userLimitsUser);
    boolean existsByUserLimitsUser(String userLimitsUser);
}
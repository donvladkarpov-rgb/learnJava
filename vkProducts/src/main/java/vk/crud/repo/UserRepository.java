package vk.crud.repo;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vk.crud.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"products"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"products"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"products"})
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"products"})
    boolean existsByEmail(String email);

}
package vk.crud.service;

import vk.crud.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    User saveUser(User user);
    Optional<User> updateUser(Long id, User userDetails);
    void deleteUser(Long id);
}
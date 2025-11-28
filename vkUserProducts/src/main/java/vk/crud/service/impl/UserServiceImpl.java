package vk.crud.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vk.crud.model.User;
import vk.crud.model.dto.UserRequest;
import vk.crud.model.dto.UserResponse;
import vk.crud.model.dto.mappers.DtoMapper;
import vk.crud.repo.UserRepository;
import vk.crud.service.UserService;
import vk.crud.web.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(DtoMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return DtoMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return DtoMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return DtoMapper.toUserResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = DtoMapper.toUserEntity(userRequest);
        User saved = userRepository.save(user);
        return DtoMapper.toUserResponse(saved);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Обновляем только разрешённые поля
        if (userRequest.getUsername() != null) {
            existing.setUsername(userRequest.getUsername());
        }
        if (userRequest.getEmail() != null) {
            existing.setEmail(userRequest.getEmail());
        }
        // Другие поля (если нужно)

        User updated = userRepository.save(existing);
        return DtoMapper.toUserResponse(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
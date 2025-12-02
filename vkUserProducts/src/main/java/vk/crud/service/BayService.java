package vk.crud.service;

import jakarta.validation.Valid;
import vk.crud.model.dto.UserDto;

public interface BayService {
    UserDto bay(@Valid UserDto request);
}

package ru.practicum.ewmmainservice.service;

import ru.practicum.ewmmainservice.dto.user.NewUserRequest;
import ru.practicum.ewmmainservice.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);

    Boolean checkUser(Long userId);
}
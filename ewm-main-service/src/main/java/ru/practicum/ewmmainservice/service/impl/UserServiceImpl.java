package ru.practicum.ewmmainservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmainservice.dto.user.NewUserRequest;
import ru.practicum.ewmmainservice.dto.user.UserDto;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ConflictException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.NotFoundException;
import ru.practicum.ewmmainservice.mapper.UserMapper;
import ru.practicum.ewmmainservice.model.User;
import ru.practicum.ewmmainservice.repository.UserRepository;
import ru.practicum.ewmmainservice.service.UserService;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        UserDto userDto;
        try {
            userDto = UserMapper.toUserDto(userRepository.save(UserMapper.toUserCreate(newUserRequest)));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("User was successfully created -> {}", userDto);
        return userDto;
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageRequest).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, pageRequest);
        }
        log.info("Number of users= {}", users.size());
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
        log.info("User by ID= {} was deleted", userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
            return UserMapper.toUserDto(userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found")));
    }

    @Override
    public Boolean checkUser(Long userId) {
        if (userRepository.existsById(userId)) {
            return true;
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }
}
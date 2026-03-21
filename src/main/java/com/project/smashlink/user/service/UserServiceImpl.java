package com.project.smashlink.user.service;

import com.project.smashlink.user.dto.request.RegisterRequestDTO;
import com.project.smashlink.user.dto.response.UserResponseDTO;
import com.project.smashlink.user.entity.User;
import com.project.smashlink.user.enums.Role;
import com.project.smashlink.user.enums.UserStatus;
import com.project.smashlink.user.exception.CustomUserException;
import com.project.smashlink.user.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserResponseDTO registerUser(RegisterRequestDTO requestDTO) {
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new CustomUserException("Email already exists" + requestDTO.getEmail());
        }

        User user =  User.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .password(requestDTO.getPassword()) // plain text for now
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return  UserResponseDTO.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .status(savedUser.getStatus())
                .createdAt(savedUser.getCreatedAt())
                .build();


    }
}

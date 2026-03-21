package com.project.smashlink.user.service;

import com.project.smashlink.user.dto.request.RegisterRequestDTO;
import com.project.smashlink.user.dto.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(RegisterRequestDTO requestDTO);
}

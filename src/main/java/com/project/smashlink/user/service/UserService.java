package com.project.smashlink.user.service;

import com.project.smashlink.auth.dto.AdminRegisterRequestDTO;
import com.project.smashlink.user.dto.request.RegisterRequestDTO;
import com.project.smashlink.user.dto.request.UpdateUserStatusDTO;
import com.project.smashlink.user.dto.response.UserResponseDTO;
import com.project.smashlink.user.enums.UserStatus;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponseDTO registerUser(RegisterRequestDTO requestDTO);
    UserResponseDTO registerAdmin(AdminRegisterRequestDTO requestDTO);
    Page<UserResponseDTO> getAllUsers(int page, int size, String sortBy);
    void deleteUser(Long userId);
    UserResponseDTO updateUserStatus(Long userId, UpdateUserStatusDTO dto);
}

package com.project.smashlink.user.service;

import com.project.smashlink.auth.dto.AdminRegisterRequestDTO;
import com.project.smashlink.exception.AppException;
import com.project.smashlink.user.dto.request.RegisterRequestDTO;
import com.project.smashlink.user.dto.request.UpdateUserStatusDTO;
import com.project.smashlink.user.dto.response.UserResponseDTO;
import com.project.smashlink.user.entity.User;
import com.project.smashlink.user.enums.Role;
import com.project.smashlink.user.enums.UserStatus;
import com.project.smashlink.exception.CustomUserException;
import com.project.smashlink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${admin.registration.secret}")
    private String adminSecret;



    @Override
    public UserResponseDTO registerUser(RegisterRequestDTO requestDTO) {
        log.info("Registering new user with email: {}", requestDTO.getEmail());
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            log.warn("Registration failed — email already exists: {}", requestDTO.getEmail());
            throw new CustomUserException("Email already exists : " + requestDTO.getEmail());
        }

        User user =  User.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully — id: {}, email: {}",
                savedUser.getId(), savedUser.getEmail());
        return  mapToDTO(savedUser);
    }


    @Override
    public UserResponseDTO registerAdmin(AdminRegisterRequestDTO requestDTO) {
        log.info("Admin registration attempt for email: {}", requestDTO.getEmail());
       if(!adminSecret.equals(requestDTO.getAdminSecret())) {
           log.warn("Admin registration failed — invalid secret for email: {}",
                   requestDTO.getEmail());
           throw new AppException("Invalid Admin secret", HttpStatus.FORBIDDEN);
       }

       if(userRepository.existsByEmail(requestDTO.getEmail())) {
           log.warn("Admin registration failed — email already exists: {}",
                   requestDTO.getEmail());
           throw new AppException("Email already exists : " + requestDTO.getEmail(), HttpStatus.CONFLICT);
       }

       User admin = User.builder()
               .name(requestDTO.getName())
               .email(requestDTO.getEmail())
               .password(passwordEncoder.encode(requestDTO.getPassword()))
               .role(Role.ROLE_ADMIN)
               .status(UserStatus.ACTIVE)
               .build();
       User savedUser = userRepository.save(admin);
        log.info("Admin registered successfully — id: {}, email: {}",
                savedUser.getId(), savedUser.getEmail());
        return mapToDTO(savedUser);
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(int page, int size, String sortBy) {
        log.info("Admin fetching all users — page: {}, size: {}, sortBy: {}",
                page, size, sortBy);
        Pageable pageable = PageRequest.of(page,size, Sort.by(sortBy));
        return  userRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Admin deleting user — id: {}", userId);
        User user = findUserById(userId);
        userRepository.delete(user);
        log.info("User deleted successfully — id: {}", userId);

    }

    @Override
    public UserResponseDTO updateUserStatus(Long userId, UpdateUserStatusDTO dto) {
        log.info("Admin updating status — userId: {}, newStatus: {}",
                userId, dto.getStatus());
       User user = findUserById(userId);
       user.setStatus(dto.getStatus());
       User savedUser = userRepository.save(user);
        log.info("User status updated — userId: {}, status: {}",
                savedUser.getId(), savedUser.getStatus());
       return mapToDTO(savedUser);

    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found — id: {}", userId);
                    return new AppException(
                            "User not found with id: " + userId, HttpStatus.NOT_FOUND);
                });
    }



    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

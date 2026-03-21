package com.project.smashlink.auth.controller;


import com.project.smashlink.auth.dto.AdminRegisterRequestDTO;
import com.project.smashlink.auth.dto.LoginRequestDTO;
import com.project.smashlink.auth.dto.LoginResponseDTO;
import com.project.smashlink.exception.AppException;
import com.project.smashlink.exception.CustomUserException;
import com.project.smashlink.security.JwtUtil;
import com.project.smashlink.user.dto.response.UserResponseDTO;
import com.project.smashlink.user.entity.User;
import com.project.smashlink.user.repository.UserRepository;
import com.project.smashlink.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private  final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );

        }catch (BadCredentialsException e){
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);

        }

        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new CustomUserException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(LoginResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build());
    }

    @PostMapping("/admin/register")
    public ResponseEntity<UserResponseDTO> registerAdmin(@Valid @RequestBody AdminRegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerAdmin(request));
    }
}

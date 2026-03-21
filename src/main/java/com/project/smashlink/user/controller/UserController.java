package com.project.smashlink.user.controller;

import com.project.smashlink.user.dto.request.RegisterRequestDTO;
import com.project.smashlink.user.dto.response.UserResponseDTO;
import com.project.smashlink.user.entity.User;
import com.project.smashlink.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        UserResponseDTO response = userService.registerUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

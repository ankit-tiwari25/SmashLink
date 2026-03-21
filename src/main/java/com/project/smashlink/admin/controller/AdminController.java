package com.project.smashlink.admin.controller;


import com.project.smashlink.user.dto.request.UpdateUserStatusDTO;
import com.project.smashlink.user.dto.response.UserResponseDTO;
import com.project.smashlink.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers(page, size, sortBy));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(@PathVariable Long userId, @Valid @RequestBody UpdateUserStatusDTO updateUserStatusDTO) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, updateUserStatusDTO));
    }


}

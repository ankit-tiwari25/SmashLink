package com.project.smashlink.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponseDTO {
    private String token;
    private String email;
    private String role;
}



package com.project.smashlink.url.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UrlResponseDTO {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private Long hitCount;
    private Long hitLimit;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String ownerEmail;
}

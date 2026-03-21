package com.project.smashlink.url.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShortenRequestDTO {

    @NotBlank(message = "Original URL is required")
    @Pattern(
            regexp = "^(https?://).*",
            message = "URL must start with http:// or https://"
    )
    private String originalUrl;

    private Long hitLimit;

    private LocalDateTime expiresAt;
}

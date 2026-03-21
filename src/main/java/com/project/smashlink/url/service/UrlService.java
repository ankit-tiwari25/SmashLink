package com.project.smashlink.url.service;

import com.project.smashlink.url.dto.ShortenRequestDTO;
import com.project.smashlink.url.dto.UrlResponseDTO;
import org.springframework.data.domain.Page;

public interface UrlService {

    UrlResponseDTO shortenUrl(ShortenRequestDTO request, String email);
    String resolveShortCode(String shortCode);
    UrlResponseDTO getUrlStats(String shortCode, String email);
    Page<UrlResponseDTO> getUserUrls(String email, int page, int size);
    void deleteUrl(String shortCode, String email);
    Page<UrlResponseDTO> getUserUrlsByAdmin(Long userId, int page, int size);
}

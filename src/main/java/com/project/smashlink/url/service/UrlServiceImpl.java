package com.project.smashlink.url.service;

import com.project.smashlink.exception.AppException;
import com.project.smashlink.url.dto.ShortenRequestDTO;
import com.project.smashlink.url.dto.UrlResponseDTO;
import com.project.smashlink.url.entity.Url;
import com.project.smashlink.url.repository.UrlRepository;
import com.project.smashlink.user.entity.User;
import com.project.smashlink.user.repository.UserRepository;
import com.project.smashlink.util.Base62Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl  implements  UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final Base62Util base62Util;

    private static final String BASE_URL = "https://smashlink.io/redirect/";

    @Override
    @Transactional
    public UrlResponseDTO shortenUrl(ShortenRequestDTO request, String email) {
        User user = findUserByEmail(email);

        // First save with placeholder shortCode
        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode("pending")
                .user(user)
                .hitCount(0L)
                .hitLimit(request.getHitLimit())
                .expiresAt(request.getExpiresAt())
                .build();
        Url savedUrl = urlRepository.save(url);

        // Encode the DB id -> Base62 shortCode

        String shortCode = base62Util.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);

        Url updatedUrl = urlRepository.save(savedUrl);
        return mapToDTO(updatedUrl);
    }



    @Override
    @Transactional
    public String resolveShortCode(String shortCode) {

        /*   --------REDIRECT KARNA HAI--- */
        Url url = findByShortCode(shortCode);

        // Check expiry
        if(url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new AppException("This short URL has expired", HttpStatus.GONE);
        }

        // Check hit limit
        if(url.getHitLimit() != null && url.getHitCount() >= url.getHitLimit()){
            throw new AppException("This short URL has reached its hit limit!", HttpStatus.GONE);
        }

        url.setHitCount(url.getHitCount() + 1);
        urlRepository.save(url);
        return url.getOriginalUrl();
    }

    @Override
    public UrlResponseDTO getUrlStats(String shortCode, String email) {
        Url url = findByShortCode(shortCode);
        User requester = findUserByEmail(email);

        boolean isOwner = url.getUser().getId().equals(requester.getId());
        boolean isAdmin = url.getUser().getRole().name().equals("ROLE_ADMIN");

        if(!isOwner && !isAdmin){
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        return mapToDTO(url);
    }

    @Override
    public Page<UrlResponseDTO> getUserUrls(String email, int page, int size) {
        User user = findUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return urlRepository.findByUserId(user.getId(), pageable).map(this::mapToDTO);
    }

    @Override
    public void deleteUrl(String shortCode, String email) {
        Url url = findByShortCode(shortCode);
        User requester = findUserByEmail(email);

        boolean isOwner = url.getUser().getId().equals(requester.getId());
        boolean isAdmin = url.getUser().getRole().name().equals("ROLE_ADMIN");

        if(!isOwner && !isAdmin){
            throw new AppException("Access Denied", HttpStatus.FORBIDDEN);
        }

        urlRepository.delete(url);
    }

    @Override
    public Page<UrlResponseDTO> getUserUrlsByAdmin(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size,Sort.by("createdAt").descending());
        return urlRepository.findByUserId(userId, pageable).map(this::mapToDTO);
    }


    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new AppException("User not found : " + email, HttpStatus.NOT_FOUND));
    }
    private UrlResponseDTO mapToDTO(Url url) {
        return UrlResponseDTO.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .hitCount(url.getHitCount())
                .hitLimit(url.getHitLimit())
                .expiresAt(url.getExpiresAt())
                .createdAt(url.getCreatedAt())
                .ownerEmail(url.getUser().getEmail())
                .build();
    }

    private Url findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElseThrow(() -> new AppException("Short URL not found: ", HttpStatus.FORBIDDEN));
    }

}

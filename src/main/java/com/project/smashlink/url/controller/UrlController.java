package com.project.smashlink.url.controller;


import com.project.smashlink.url.dto.ShortenRequestDTO;
import com.project.smashlink.url.dto.UrlResponseDTO;
import com.project.smashlink.url.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService  urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDTO> shorten(
            @Valid @RequestBody ShortenRequestDTO request,
            @AuthenticationPrincipal String email){
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(urlService.shortenUrl(request, email));
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlResponseDTO> getUrlStats(
            @PathVariable String shortCode,
            @AuthenticationPrincipal String email){
        return ResponseEntity.ok(urlService.getUrlStats(shortCode, email));
    }

    @GetMapping("/my-urls")
    public ResponseEntity<Page<UrlResponseDTO>> getMyUrls(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(urlService.getUserUrls(email, page, size));
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<String> deleteUrl(@PathVariable String shortCode,
                                            @AuthenticationPrincipal String email){
        urlService.deleteUrl(shortCode, email);
        return ResponseEntity.ok("URL deleted successfully");
    }
}

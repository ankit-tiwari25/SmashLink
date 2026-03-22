package com.project.smashlink.ratelimit;


import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitConfig rateLimitConfig;

    private static final List<String> RATE_LIMITED_PATHS = List.of(
            "/api/auth/login",
            "/api/users/register",
            "/api/urls/shorten",
            "/redirect/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        boolean shouldLimit = RATE_LIMITED_PATHS.stream()
                .anyMatch(path::startsWith);

        if(!shouldLimit) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = extractIp(request);
        String userKey = extractUserKey(request, path);

        Bucket ipBucket = rateLimitConfig.resolveBucketForIp(ip);
        ConsumptionProbe ipProbe = ipBucket.tryConsumeAndReturnRemaining(1);

        if(!ipProbe.isConsumed()) {
            log.warn("Rate Limit exceeded for ip : {}", ip);
            writeRateLimitResponse(response, ipProbe.getNanosToWaitForRefill());
            return;
        }

        if(userKey != null) {
            Bucket userBucket = rateLimitConfig.resolveBucketForUser(userKey);
            ConsumptionProbe userProbe = userBucket.tryConsumeAndReturnRemaining(1);

            if(!userProbe.isConsumed()) {
                log.warn("Rate Limit exceeded for user/key : {}", userKey);
                writeRateLimitResponse(response, userProbe.getNanosToWaitForRefill());
                return;
            }
        }

        response.addHeader("X-RateLimit-Limit", "10");
        response.addHeader("X-RateLimit-Remaining", String.valueOf(ipProbe.getRemainingTokens()));

        filterChain.doFilter(request, response);
    }

    private String  extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if(forwarded != null &&  !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserKey(HttpServletRequest request, String path){
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7, Math.min(authHeader.length(), 40));
        }
        return null;
    }

    private void writeRateLimitResponse(HttpServletResponse response, long nanosToWaitForRefill) throws IOException {
        long secondsToWait = (nanosToWaitForRefill / 1_000_000_000) + 1;
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Retry-After", String.valueOf(secondsToWait));
        response.getWriter().write("""
                {
                "status" : 429,
                "error" : "Too Many Requests",
                "message": "Rate limit exceeded. Please try after %d seconds.",
                "timestamp" : "%s"
                }""".formatted(secondsToWait, Instant.now()));
    }
}

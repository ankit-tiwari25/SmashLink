package com.project.smashlink.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Getter
@Setter
@AllArgsConstructor
public class UrlEvent {

    private final String userEmail;
    private final String shortCode;
    private final String originalUrl;
    private final UrlEventType eventType;
    private final Long hitCount;
    private final Long hitLimit;
}

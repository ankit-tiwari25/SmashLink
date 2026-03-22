package com.project.smashlink.notification.observer;

import com.project.smashlink.notification.event.UrlEvent;
import com.project.smashlink.notification.service.EmailService;
import com.project.smashlink.url.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationObserver implements UrlEventObserver {
    private final EmailService emailService;


    @Override
    public void onEvent(UrlEvent event) {
        String subject = buildSubject(event);
        String body = buildBody(event);
        emailService.sendEmail(event.getUserEmail(), subject, body);
    }

    private String buildSubject(UrlEvent event) {
        return switch (event.getEventType()){
            case HIT_LIMIT_NEARING ->
                "[SmashLink] WARNING : Your URL is nearing its hit limit";
            case HIT_LIMIT_EXHAUSTED ->
                "[SmashLink] INFO : Your URL is exhausted its hit]";
            case URL_EXPIRY_NEARING ->
                "[SmashLink] WARNING : Your URL has expiring soon";
            case URL_EXPIRED ->
                "[SmashLink] INFO : Your URL has expired";
        };
    }
    private String buildBody(UrlEvent event) {
        return switch (event.getEventType()){
            case HIT_LIMIT_NEARING -> """
                    Hello,
                     Your shortened URL is nearing its hit limit.
                     ShortCode : %s
                     Original URL : %s
                     Hits Used : %d / %d
                     
                     Consider increasing your hit limit before it runs out.
                     
                     -SmashLink Team""".formatted(
                             event.getShortCode(),
                    event.getOriginalUrl(),
                    event.getHitCount(),
                    event.getHitLimit()
            );

            case  HIT_LIMIT_EXHAUSTED -> """
                    Hello,
                    Your shortned URL has reached its hit limit and is no longer accessible.
                    
                    ShortCode : %s
                    Original URL : %s
                    Total Used : %d / %d
                    
                    Please create a new short URL if needed.
                    
                    -SmashLink Team""".formatted(
                            event.getShortCode(),
                    event.getOriginalUrl(),
                    event.getHitCount(),
                    event.getHitLimit()
            );
            case  URL_EXPIRY_NEARING -> """
                    Hello,
                    Your shortened URL is expiring within 24 hours.
                    
                    ShortCode : %s
                    Original URL : %s
                    
                    Please take action before it expires.
                    
                    -SmashLink Team""".formatted(
                            event.getShortCode(),
                    event.getOriginalUrl()
            );

            case  URL_EXPIRED -> """
                    Hello,
                    Your shortened URL is expired and is no longer accessible.
                    ShortCode : %s
                    Original URL : %s
                    
                    Please create a new short URL if needed.
                    
                    -SmashLink Team
                    
            """.formatted(
                    event.getShortCode(),
                    event.getOriginalUrl()
            );
        };
    }

}

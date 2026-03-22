package com.project.smashlink.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    @Value("${rate.limit.requests}")
    private int requests;

    @Value("${rate.limit.duration.minutes}")
    private int durationMinutes;

    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> userBuckets = new  ConcurrentHashMap<>();

    public Bucket resolveBucketForIp(String ip){
        return ipBuckets.computeIfAbsent(ip, k-> createNewBucket());
    }

    public Bucket resolveBucketForUser(String userKey){
        return userBuckets.computeIfAbsent(userKey, k-> createNewBucket());
    }

    private Bucket createNewBucket(){
        Bandwidth limit = Bandwidth.builder()
                .capacity(requests)
                .refillGreedy(requests, Duration.ofMinutes(durationMinutes))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

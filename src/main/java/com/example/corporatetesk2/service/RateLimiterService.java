package com.example.corporatetesk2.service;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final RateLimiter rateLimiter;

    public RateLimiterService() {
        // Rate Limiter 설정: 1초당 최대 500개의 요청
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(500) // 1초당 허용되는 요청 수
                .limitRefreshPeriod(Duration.ofSeconds(1)) // 갱신 주기
                .timeoutDuration(Duration.ofMillis(500)) // 요청이 초과될 때 대기 시간
                .build();

        // Rate Limiter 등록
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        this.rateLimiter = registry.rateLimiter("notificationLimiter", config);
    }

    // Rate Limiter 적용 함수
    public void executeWithRateLimit(Runnable runnable) {
        RateLimiter.decorateRunnable(rateLimiter, runnable).run();
    }
}

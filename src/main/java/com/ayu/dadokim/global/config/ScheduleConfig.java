package com.ayu.dadokim.global.config;


import com.ayu.dadokim.global.security.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleConfig {

    private final JwtService jwtService;

    // 매일 새벽 3시에 8일 지난 Refresh 토큰 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEntityTtlSchedule() {
        jwtService.cleanUpOldRefreshTokens();
    }
}

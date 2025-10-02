package com.ayu.dadokim.global.config;

import com.loginapi.backend.global.security.jwt.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleConfig {

    private final RefreshRepository refreshRepository;

    // Refresh 토큰 저장소 8일 지난 토큰 삭제
    // Todo : service 단에 명시하여 사용하는 것이 더욱 적절하다.
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEntityTtlSchedule() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(8);
        refreshRepository.deleteByCreatedDateBefore(cutoff);
    }

}

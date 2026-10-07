package com.aact.authservice.config;

import com.aact.authservice.service.RetiredUserCleanupService;
import com.aact.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoSchedule {

    private final RetiredUserCleanupService retiredUserCleanupService;

    @Scheduled(cron = "0 0 0 11 * *", zone = "Asia/Seoul")
    public void scheduleRetiredUserCleanup() {
        log.info("퇴사 사용자 자동 비활성화 시작");

        ResponseDTO<?> ret =
                retiredUserCleanupService.deactivateRetiredUsers();

        if ("Y".equals(ret.getErrFlag())) {
            log.error("퇴사 사용자 자동 비활성화 실패: {}", ret.getErrMsg());
        } else {
            log.info("퇴사 사용자 자동 비활성화 완료: {}", ret.getErrMsg());
        }
    }
}
package com.aact.authservice.service;

import com.aact.authservice.repo.UserRepo;
import com.aact.common.DbDto;
import com.aact.common.ResponseDTO;
import com.aact.common.ServiceBase;
import com.aact.common.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetiredUserCleanupService extends ServiceBase {

    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ObjectProvider<UserRepo> userRepoProvider;

    public ResponseDTO<?> deactivateRetiredUsers() {
        // 예: 2026-10-10 실행 시 20261001
        String cutoffDate = LocalDate.now(KOREA)
                .withDayOfMonth(1)
                .format(DATE_FORMAT);

        UserRepo repo = userRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.setRetiredUserCleanup(
                    cutoffDate,
                    "KOR",
                    Util.getGUID(),
                    "SYSTEM_SCHEDULER",
                    "127.0.0.1",
                    "RETIRE_USER_SCHEDULE"
            );

            log.info(
                    "퇴사 사용자 자동 비활성화 실행 완료. 기준일: {}",
                    cutoffDate
            );

            return okOrThrow("setRetiredUserCleanup", dbRet);
        });
    }
}
package com.aact.infraservice.config;

import com.aact.common.ResponseDTO;
import com.aact.infraservice.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoSchedule {

    private final WorkService workService;

    @Scheduled(cron = "0 0 0 25 * *",zone = "Asia/Seoul")
    public void scheduleAutoJob(){
        log.info("scheduleAutoJob : {}","스케줄 자동생성 시작");
        //서비스 처리
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = now.format(formatter2);
        log.info("scheduleAutoJob : 생성날짜 - {}",date);
        ResponseDTO<?> ret = workService.setScheduleAutoJob(date);

        if(ret.getErrFlag().equals("Y")){
            log.error("scheduleAutoJob : {}",ret.getErrMsg());
        }else{
            log.info("scheduleAutoJob : {}",ret.getErrMsg());
        }

        log.info("scheduleAutoJob : {}","스케줄 자동생성 종료");
    }

}

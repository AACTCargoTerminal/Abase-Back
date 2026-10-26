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

    @Scheduled(cron = "0 0 1 * * *",zone = "Asia/Seoul")
    public void setSchAutoMatch() {
        log.info("setSchAutoMatch : {}","스케줄캡스 매칭 시작");

        //서비스 처리
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = now.minusDays(1).format(formatter2);
        log.info("setSchAutoMatch : 생성날짜 - {}",date);

        ResponseDTO<?> ret = workService.setSchAutoMatch(date);
        if(ret.getErrFlag().equals("Y")){
            log.error("setSchAutoMatch : {}",ret.getErrMsg());
        }else{
            log.info("setSchAutoMatch : {}",ret.getErrMsg());
        }

        log.info("setSchAutoMatch : {}","스케줄캡스 매칭 종료");
    }

    @Scheduled(cron = "0 1 0 * * WED",zone = "Asia/Seoul")
    public void setAutoSchAppr() {
        log.info("setAutoSchAppr : {}","스케줄주간 자동확정 시작");

        //서비스 처리
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = now.format(formatter2);
        log.info("setAutoSchAppr : 주간자동확정 생성날짜 - {}",date);

        ResponseDTO<?> ret = workService.setAutoSchAppr(date,false);
        if(ret.getErrFlag().equals("Y")){
            log.error("setAutoSchAppr : {}",ret.getErrMsg());
        }else{
            log.info("setAutoSchAppr : {}",ret.getErrMsg());
        }

        log.info("setAutoSchAppr : {}","스케줄주간 자동확정 시작");
    }

    @Scheduled(cron = "0 0 1 1 * *",zone = "Asia/Seoul")
    public void setAutoSchAppr2() {
        log.info("setAutoSchAppr : {}","스케줄월간 자동확정 시작");

        //서비스 처리
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = now.minusDays(1).format(formatter2);
        log.info("setAutoSchAppr : 월간자동확정 생성날짜 - {}",date);

        ResponseDTO<?> ret = workService.setAutoSchAppr(date,true);
        if(ret.getErrFlag().equals("Y")){
            log.error("setAutoSchAppr : {}",ret.getErrMsg());
        }else{
            log.info("setAutoSchAppr : {}",ret.getErrMsg());
        }

        log.info("setAutoSchAppr : {}","스케줄월간 자동확정 시작");
    }



}

package com.aact.infraservice.service;

import com.aact.common.BizException;
import com.aact.common.ResponseDTO;
import com.aact.common.ServiceBase;
import com.aact.infraservice.dto.CapsEnterDTO;
import com.aact.infraservice.dto.CapsGetType;
import com.aact.infraservice.dto.CapsTimeDTO;
import com.aact.infraservice.dto.CapsUserDTO;
import com.aact.infraservice.repo.CapsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CapsService extends ServiceBase {
    private final CapsMapper capsMapper;

    public ResponseDTO<?> findUsersWithGroup(String start, String end, String name, String id, String[] modes) {

        List<CapsUserDTO> dbRet = capsMapper.findUsersWithGroup(start, end, name, id, modes);

        ResponseDTO<List<CapsUserDTO>> ret = ResponseDTO.<List<CapsUserDTO>>builder().errFlag("N").errMsg("조회완료")
                .data(dbRet).build();

        return okOrThrow("findUsersWithGroup", ret);

    }

    // findEnterToUser

    public ResponseDTO<?> findEnterToUser(String id, String[] modes, String start, String end) {

        List<CapsEnterDTO> dbRet = capsMapper.findEnterToUser(id, modes, start, end);

        ResponseDTO<List<CapsEnterDTO>> ret = ResponseDTO.<List<CapsEnterDTO>>builder().errFlag("N").errMsg("조회완료")
                .data(dbRet).build();

        return okOrThrow("findEnterToUser", ret);

    }

    public ResponseDTO<CapsTimeDTO.CapsRangeResult> findDateToId(CapsGetType getType, String id, String date, String time) {
        String endDate = "";
        String capsTime = "";
        String capsOrgTime = "";
        List<CapsEnterDTO> dbRet = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime  now = LocalDateTime.parse(date+time, formatter);
        //캡스 조회 제한
        if(getType==CapsGetType.START){
            endDate = now.minusHours(2).format(formatter);
            dbRet = capsMapper.findDateToId(id, endDate,date+time);
        }else{
            endDate = now.plusMinutes(30).format(formatter);
            dbRet = capsMapper.findDateToId(id,date+time, endDate);
        }

        if (dbRet.isEmpty()) {
            capsTime = "0000";
            capsOrgTime = "0000";
        }else{
            LocalTime nowTime = LocalTime.parse(time,DateTimeFormatter.ofPattern("HHmm"));
            Stream<CapsEnterDTO> nearest;
            if (getType == CapsGetType.START) {
                nearest = dbRet.stream()
                        .filter(v -> !"2".equals(v.getE_mode()));
            } else {
                nearest = dbRet.stream()
                        .filter(v -> !"1".equals(v.getE_mode()))
                        .filter(v -> !"5".equals(v.getE_mode()));
            }
            CapsEnterDTO ret = nearest.filter(v -> v.getE_time() != null && v.getE_time().length() == 4)
                    .min(Comparator.comparingLong(dto -> {
                LocalTime compareTime =
                        LocalTime.parse(dto.getE_time(),
                                DateTimeFormatter.ofPattern("HHmm"));

                return Math.abs(
                        Duration.between(nowTime, compareTime)
                                .toMinutes()
                );
            })).orElse(null);
            if(ret == null){
                capsTime = "0000";
                capsOrgTime = "0000";
            }else{
                if (getType == CapsGetType.START) {
                    capsTime = roundTime(ret.getE_time());
                }else{
                    capsTime = floorTo30(ret.getE_time());
                }
                capsOrgTime =  ret.getE_time();
            }
        }
        ResponseDTO<CapsTimeDTO.CapsRangeResult> ret = ResponseDTO.<CapsTimeDTO.CapsRangeResult>builder().errMsg("").errFlag("N").data(new CapsTimeDTO.CapsRangeResult(capsTime, capsOrgTime)).build();

        return okOrThrow("findDateToId", ret);

    }

    ClosestResult findClosest(CapsEnterDTO dto, String targetDate, String targetTime) {
        if (dto == null || dto.getE_date() == null || dto.getE_time() == null || dto.getE_time().length() < 4) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        LocalDateTime target = LocalDateTime.parse(targetDate + targetTime, formatter);
        String originalTime = dto.getE_time().substring(0, 4);
        String hhmm = dto.getE_time().substring(0, 4);
        LocalDateTime time = LocalDateTime.parse(dto.getE_date() + hhmm, formatter);

        long diff = Duration.between(target, time).toMinutes();

        CapsEnterDTO newDto = new CapsEnterDTO();
        newDto.setE_date(dto.getE_date());
        newDto.setE_time(hhmm); // 초 제거
        newDto.setE_time_raw(originalTime);
        newDto.setE_mode(dto.getE_mode());
        newDto.setG_id(dto.getG_id());

        return new ClosestResult(newDto, (int) diff);
    }

    String roundTime(String hhmmss) {
        if (hhmmss == null || hhmmss.length() < 4) return null;

        int hour = Integer.parseInt(hhmmss.substring(0, 2));
        int minute = Integer.parseInt(hhmmss.substring(2, 4));
        int second = hhmmss.length() >= 6 ? Integer.parseInt(hhmmss.substring(4, 6)) : 0;

        // 초까지 포함해서 분 반올림
        if (second >= 30) {
            minute += 1;
        }

        // 30분 단위 반올림
        if (minute <= 30) {
            minute = 30;
        } else {
            minute = 0;
            hour += 1;
        }

        // 24시 넘어가면 00시
        if (hour == 24) {
            hour = 0;
        }

        return String.format("%02d%02d", hour, minute);
    }

    String floorTo30(String hhmmss) {
        if (hhmmss == null || hhmmss.length() < 4) return null;

        int hour = Integer.parseInt(hhmmss.substring(0, 2));
        int minute = Integer.parseInt(hhmmss.substring(2, 4));
        int second = hhmmss.length() >= 6 ? Integer.parseInt(hhmmss.substring(4, 6)) : 0;

        // 초 반영 (30초 이상이면 분 +1)
        if (second >= 30) {
            minute += 1;
        }

        // 60분 처리
        if (minute == 60) {
            minute = 0;
            hour += 1;
        }

        // 🔥 핵심 조건
        if (minute >= 55 || minute < 25) {
            minute = 0;
        } else {
            minute = 30;
        }

        // 24시 처리
        if (hour == 24) {
            hour = 0;
        }

        return String.format("%02d%02d", hour, minute);
    }

    record ClosestResult(CapsEnterDTO data, int diffMinutes) {
    }
}

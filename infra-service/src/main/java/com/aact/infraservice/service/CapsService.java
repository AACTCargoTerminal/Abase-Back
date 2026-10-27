package com.aact.infraservice.service;

import com.aact.common.ResponseDTO;
import com.aact.infraservice.dto.CapsGetType;
import com.aact.infraservice.dto.CapsTimeDTO;

import java.util.List;

public interface CapsService {

    /**
     * 캡스 조회 용도
     * @param id 유저 ID
     * @param start 시작날짜 YYYYMMDD
     * @param end 종료날짜 YYYYMMDD
     * @return
     */
    ResponseDTO<List<CapsTimeDTO.CapsUserTimeDTO>> findIdToDate(String id, String start, String end);

    /**
     * 시작 또는 종료만 근접 찾기용도
     * @param getType 시작,종료 구분
     * @param id 사용자 ID
     * @param date 날짜 YYYYMMDD
     * @param time 시간 HHmm
     * @return
     */
    ResponseDTO<CapsTimeDTO.CapsRangeResult> findDateToId(CapsGetType getType, String id, String date, String time);
}

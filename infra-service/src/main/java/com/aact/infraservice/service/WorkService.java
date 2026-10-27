package com.aact.infraservice.service;

import com.aact.common.ResponseDTO;
import com.aact.infraservice.dto.CapsTimeDTO;
import com.aact.infraservice.dto.WorkDTO;

import java.math.BigDecimal;
import java.util.List;

public interface WorkService {

    /**
     * 근무자 스케줄 조회용도
     * @param date 날짜 YYYYMM
     * @param deptCode 부서 코드 HRPAT
     * @param terminalCode 터미널 코드 TRMCD
     * @param approveFlag 확정인지아닌지 조회용도 현재 안씀
     * @return
     */
    ResponseDTO<?> getWorkM010_002(String date, String deptCode, String terminalCode, String approveFlag);

    /**
     * 인사팀 관리페이지 조회 용도
     * @param dto 요청 객체
     * @return
     */
    ResponseDTO<?> getWorkL010_008(WorkDTO.HrSearchDTO dto);

    /**
     * 인사팀 관리페이지 상세테이블 조회
     * @param date 날짜 YYYYMMDD
     * @param userSid 유저테이블 키값
     * @param seq 스케줄 순번
     * @return
     */
    ResponseDTO<?> getWorkL010_009(String date, BigDecimal userSid, BigDecimal seq);

    /**
     * 인사팀 관리페이지 검토취소 용도
     * @param dto 취소정보 객체
     * @return
     */
    ResponseDTO<?> setWorkL010_018(WorkDTO.HrReqSaveDTO dto);

    /**
     * 인사팀 관리페이지 서류 파일 가져오기
     * @param dto 서류 추출용 객체
     * @return
     */
    ResponseDTO<?> setWorkM010_042(WorkDTO.HrFileSearchDTO dto);

    /**
     * 담당자 스케줄 엑셀 업로드 용도
     * @param dto 스케줄 정보 객체
     * @return
     */
    ResponseDTO<?> setWorkM010_014(WorkDTO.SaveDTO dto);

    /**
     * 스케줄 휴무일 조회용도
     * @param date 날짜 YYYYMM
     * @return
     */
    ResponseDTO<?> getHoliDay(String date);

    /**
     * 스케줄 마감 용도
     * @param date 날짜 YYYYMM
     * @param teamCode 공통코드 HRPAT
     * @param terminalCode 공통코드 HRMTR
     * @return
     */
    ResponseDTO<?> setWorkL010_013(String date,String teamCode,String terminalCode);

    /**
     * OT 신청시 조회용도
     * @param date 날짜 YYYYMMDD
     * @param userSid 유저 키값
     * @param seq 스케줄 순번
     * @return
     */
    ResponseDTO<?> getWorkM010_003(String date,BigDecimal userSid,BigDecimal seq);

    /**
     * OT 관리페이지 서류 제출용도
     * @param dto 서류파일 및 필요 정보 객체
     * @return
     */
    ResponseDTO<?> setWorkM010_018(WorkDTO.HrFileSaveDTO dto);

    /**
     * OT 신청 저장 용도
     * @param dto OT 신청 필요 객체
     * @return
     */
    ResponseDTO<?> setWorkM010_019(CapsTimeDTO.SearchDTO dto);

    /**
     * 근무자별 스케줄 1~말일까지 통으로 삭제 용도
     * @param dto 삭제 필요 객체
     * @return
     */
    ResponseDTO<?> setWorkM010_021(WorkDTO.SchDeleteDTO dto);

    /**
     * OT 관리자 페이지 엑셀 일괄 업로드 용도
     * @param dtos 엑셀 추출 객체
     * @return
     */
    ResponseDTO<?> setWorkM010_039(List<CapsTimeDTO.SearchGroupDTO> dtos);

    /**
     * 인사팀 관리페이지 요청 거절 용도
     * @param date 날짜 YYYYMMDD
     * @param userSid 유저 키값
     * @param seq 스케줄 순번
     * @param remark 거절 사유
     * @param type OT반려기능과 요청 거절 구분용도
     * @return
     */
    ResponseDTO<?> setWorkL010_017(String date,BigDecimal userSid,BigDecimal seq,String remark,String type);

    /**
     * 인사팀 전용 수기 OT 신청 용도
     * @param dto OT 신청 정보
     * @return
     */
    ResponseDTO<?> setWorkM010_038(CapsTimeDTO.SearchDTO dto);

    /**
     * 개인 OT 목록 조회
     * @param date 날짜 YYYYMM 또는 YYYYMMDD
     * @param monFlag 날짜 타입 YYYYMM 인지 YYYYMMDD 인지 정하는 용도
     * @return
     */
    ResponseDTO<?> getWorkL010_007(String date,String monFlag);

    /**
     * OT 관리자 조회
     * @param date 날짜 YYYYMM 또는 YYYYMMDD
     * @param deptCode 공통코드 HRPAT 부서코드
     * @param terminalCode 공통코드 HRMTR 터미널코드
     * @param userName 유저명
     * @param approveFlag OT 상태별 조회할 용도
     * @return
     */
    ResponseDTO<?> getWorkL010_006(String date, String deptCode, String terminalCode,String userName, String approveFlag);

    /**
     * OT 삭제용도
     * @param dtos OT 삭제 필요 객체
     * @param adminFlag 관리자용도 삭제인지 사용자용도 삭제인지 구분
     * @return
     */
    ResponseDTO<?> setWorkM010_022(List<CapsTimeDTO.DeleteDTO> dtos,String adminFlag);

    /**
     * OT 확정처리
     * @param dtos OT 확정 필요 객체
     * @return
     */
    ResponseDTO<?> setWorkL010_014(List<CapsTimeDTO.DeleteDTO> dtos);

    /**
     * OT 재처리용도 일괄 수정처리
     * @param dtos 재처리 필요 객체
     * @return
     */
    ResponseDTO<?> setCapsReSave(List<CapsTimeDTO.DeleteDTO> dtos);

    /**
     * 스케줄 확정 용도
     * @param dto 확정 처리 객체
     * @return
     */
    ResponseDTO<?> setWorkL010_015(WorkDTO.ApproveDTO dto);

    /**
     * 인사팀 요청리스트 수락용도
     * @param dto 수락 필요 객체
     * @return
     */
    ResponseDTO<?> setWorkM010_034(WorkDTO.HrReqSaveDTO dto);

    /**
     * 인사팀 검토완료 용도
     * @param dto 검토완료 필요 객체
     * @return
     */
    ResponseDTO<?> setWorkL010_016(WorkDTO.HrReqSaveDTO dto);

    /**
     * 인사팀 캡스 저장용도
     * @param dto 캡스 저장객체
     * @return
     */
    ResponseDTO<?> setWorkM010_043(WorkDTO.HrCapsSaveDTO dto);

    /**
     * 스케줄 자동 등록 및 마감처리 HRPAT VALUE3_NUMBER 1인경우 처리
     * @param dateStr 날짜 YYYYMMDD 기준 한달 후 꺼 실행
     * @return
     */
    ResponseDTO<?> setScheduleAutoJob(String dateStr);

    /**
     * 스케줄 OT 없고 캡스정보 없는경우 전날꺼까지 캡스매칭 ( 바로전날은 스케줄이 다음날로 안넘어가는것들만 )
     * @param dateStr 날짜 YYYYMMDD 기준
     * @return
     */
    ResponseDTO<?> setSchAutoMatch(String dateStr);

    /**
     * 스케줄 주간, 월간 자동 확정
     * @param dateStr 날짜 YYYYMMDD
     * @param flag true,False true일 경우 월간 false일경우 주간 - 주간은 기준 전주 확정, 월간 - 날짜기준 전달 통으로 확정
     * @return
     */
    ResponseDTO<?> setAutoSchAppr(String dateStr,boolean flag);

    /**
     * NAS MANUAL폴더 내 특정 파일명 가져오기
     * @param fileName 파일명
     * @return
     */
    ResponseDTO<?> getManual(String fileName);

    /**
     * OT 일괄 업로드할 예시 엑셀 추출물
     * @return
     */
    ResponseDTO<?> getExTimeWork();

    /**
     * 최신 스케줄 정보 엑셀로 추출
     * @param teamCode 공통코드 HRPAT
     * @param terminalCode 공통코드 HRMTR
     * @param date 날짜 YYYYM
     * @return
     */
    ResponseDTO<?> getExWorkSch(String teamCode,String terminalCode ,String date);

    /**
     * 인사팀 예정, 확정스케줄 엑셀로 추출
     * @param teamCode 공통코드 HRPAT
     * @param terminalCode 터미널코드 TRMCD
     * @param toDate 시작날짜 YYYYMMDD 일간 조회용
     * @param fromDate 종료날짜 YYYYMMDD 일간 조회용
     * @param date 날짜 YYYYMM 월간 조회용
     * @param type 예정, 확정 구분용도
     * @return
     */
    ResponseDTO<?> getWorkSch(String teamCode,String terminalCode,String toDate,String fromDate,String date,String type);

    /**
     * 근무 스케줄 세부내역 엑셀로 추출
     * @param teamCode 공통코드 HRPAT
     * @param terminalCode 공통코드 TRMCD
     * @param toDate 시작날짜 YYYYMMDD 일간 조회용
     * @param fromDate 종료날짜 YYYYMMDD 일간 조회용
     * @param date 날짜 YYYYMM 월간 조회용
     * @return
     */
    ResponseDTO<?> getWorkDetail(String teamCode,String terminalCode,String toDate,String fromDate,String date);

    /**
     * OT 신청서 엑셀 추출용도
     * @param teamCode 공통코드 HRPAT
     * @param terminalCode 공통코드 TRMCD
     * @param toDate 시작날짜 YYYYMMDD 일간 조회용
     * @param fromDate 종료날짜 YYYYMMDD 일간 조회용
     * @param date 날짜 YYYYMM 월간 조회용
     * @param userName 유저명
     * @return
     */
    ResponseDTO<?> getWorkTime(String teamCode,String terminalCode,String toDate,String fromDate,String date,String userName);
}

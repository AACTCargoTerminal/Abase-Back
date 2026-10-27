package com.aact.sysservice.service;

import com.aact.common.ResponseDTO;
import com.aact.sysservice.dto.SysDTO;

import java.math.BigDecimal;
import java.util.List;

public interface SysService {

    /**
     * 공통코드 캐시 전체 재조회
     */
    ResponseDTO<?> reload();

    /**
     *  캐시 특정 CLASS_CODE 재조회
     */
    ResponseDTO<?> reload(String classCode);

    /**
     * 운항스케줄 조회
     * @param fltDate 날짜 형식 YYYYMMDD
     * @param inoutFlag 출항,입항 문자열 크기 1
     * @return
     */
    ResponseDTO<?> getSch(String fltDate, String inoutFlag);

    /**
     * CIMP 스케줄 조회 용도
     * @param fltDate 날짜 형식 YYYYMMDD
     * @param inoutFlag 출항,입항 문자열 크기 1
     * @return
     */
    ResponseDTO<?> getSch003(String fltDate, String inoutFlag);

    /**
     * 공통 코드 캐시된것 코드명 기준으로 조회
     * @param classCode 공통코드 ClassCode
     * @param codeName 공통코드 명
     * @return
     */
    ResponseDTO<?> getBaseOds(String classCode, String codeName);

    /**
     * 공통 코드 클래스코드 시준으로 Value3Char 같은것 조회
     * @param classCode 공통코드 ClassCode
     * @param value3 공통코드 Value3Char 같은것
     * @return
     */
    ResponseDTO<?> getBaseOdsValue3(String classCode, String value3);

    /**
     * 공통 코드 클래스코드 시준으로 Value2Char 같은것 조회
     * @param classCode 공통코드 ClassCode
     * @param value2 공통코드 Value2Char 같은것
     * @return
     */
    ResponseDTO<?> getBaseOdsValue2(String classCode, String value2);

    /**
     * 공통 코드 클래스코드 시준으로 Value1Char 같은것 조회
     * @param classCode 공통코드 ClassCode
     * @param value1 공통코드 Value1Char 같은것
     * @return
     */
    ResponseDTO<?> getBaseOdsValue1(String classCode, String value1);

    /**
     * 운항스케줄 조회 팝업용
     * @param fltDate 날짜 기준 YYYYMMDD
     * @param inoutFlag 입출항 문자열 크기 1
     * @return
     */
    ResponseDTO<?> getSchP010_001(String fltDate, String inoutFlag);

    /**
     * FWB, FHL 운항스케줄 조회용도
     * @param schSid TWM_SCHEDULE_MASTER 키 값
     * @return
     */
    ResponseDTO<?> getSchM010_001(String schSid);

    /**
     * 대시점 조회 팝업 용도
     * @param dto 객체타입
     * @return
     */
    ResponseDTO<?> getCustomerP010_001(SysDTO.CustomerDTO dto);

    /**
     * FWB 용 AGENT 정보
     * @param customerSid 대리점 테이블 키값
     * @return
     */
    ResponseDTO<?> getCustomerL010_002(BigDecimal customerSid);

    /**
     * CIMP SITA정보 이메일로 보내기위한 용도
     * @param dto 이메일로 보내기위해 필요한 객체 파라미터
     * @return
     */
    ResponseDTO<?> sendSitaToEmail(SysDTO.SitaEmailDTO dto);

    /**
     * 공지 게시판 조회용도
     * @return
     */
    ResponseDTO<?> getBoardL010_001();

    /**
     * 공통코드 전용 관리페이지 용도 API
     * @param classCode 클래스코드
     * @param codeName 코드명
     * @param usableFlag 사용유무
     * @return
     */
    ResponseDTO<?> getCodeA010Mgm(String classCode,String codeName, String usableFlag);

    /**
     * 공통코드 전용 관리페이지 저장
     * @param dtoList 공통코드전용 저장 객체
     * @return
     */
    ResponseDTO<?> setCodeA010_012(List<SysDTO.CodeSaveDTO> dtoList);

    /**
     * 공통코드 전용 관리페이지 삭제
     * @param dtoList 공통코드전용 삭제 객체
     * @return
     */
    ResponseDTO<?> setCodeA010_022(List<SysDTO.CodeDelDTO> dtoList);
}

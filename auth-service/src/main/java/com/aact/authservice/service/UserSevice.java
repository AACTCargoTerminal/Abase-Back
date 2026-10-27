package com.aact.authservice.service;

import com.aact.authservice.dto.InfraUser;
import com.aact.authservice.dto.LoginReq;
import com.aact.authservice.dto.SetUserDTO;
import com.aact.common.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface UserSevice {

    /**
     * Sams, Intra 통합 로그인 로그인후 Redis 세션저장
     * @param dto 로그인 전용 객체
     * @param hReq HTTP 요청 정보
     * @return
     */
    ResponseDTO<?> login(LoginReq.LoginDTO dto, HttpServletRequest hReq);

    /**
     * 웹에서 5분마다 주기적으로 정상세션인지 확인용도 HttpFilter에서 점검함
     * @return
     */
    ResponseDTO<?> verity();

    /**
     * 로그아웃 기존 세션 닫는 용도
     * @param hReq HTTP 요청 정보
     * @return
     */
    ResponseDTO<?> logout(HttpServletRequest hReq);

    /**
     * 로그인한 세션으로 사용가능한
     * @return
     */
    ResponseDTO<?> buildMenu();

    /**
     * 사용자 정보 팝업 조회 용도
     * @param userId 사용자 ID
     * @return
     */
    ResponseDTO<?> getUserM010_003(String userId);

    /**
     * INTRA 인사정보 조회 용도
     * @param userSid 유저 테이블 키값
     * @param usableFlag 사용유무
     * @return
     */
    ResponseDTO<?> getUserRel(BigDecimal userSid, String usableFlag);

    /**
     * 사용자 삭제 용도
     * @param userId 사용자 ID
     * @return
     */
    ResponseDTO<?> setUserDelete(String userId);

    /**
     * HR 관련 사용자 목록 조회 용도
     * @param deptCode HRPAT 기준 부서코드
     * @param userId 사용자 ID
     * @param userName 사용자 명 공용어 기준
     * @param usableFlag 사용유무
     * @return
     */
    ResponseDTO<?> getUserList(String deptCode,String userId,String userName,String usableFlag);

    /**
     * 사용자 개인 정보 저장용
     * @param req HTTP 요청 정보 세션 유저 정보값 변경용
     * @param dto 저장 객체
     * @return
     */
    ResponseDTO<?> setUserInfo(HttpServletRequest req, SetUserDTO dto);

    /**
     * 사용자 관리 팝업 용도 저장
     * @param dto 저장 객체
     * @return
     */
    ResponseDTO<?> setUserInfoMgm(InfraUser.SaveUserDTO dto);

    /**
     * 프로그램 테이블에서 프로그램 ID 기준 정보 가져오기
     * @param pgmId 프로그램 ID
     * @return
     */
    ResponseDTO<?> getPgmInfo(String pgmId);

    /**
     * 개인 도장 및 사인 이미지 가져오기
     * @param userId 유저아이디
     * @return
     */
    ResponseDTO<?> getUserSign(String userId);

    /**
     * 개인 도장 및 사인 이미지 저장하기
     * @param req HTTP 요청 정보
     * @param files 이미지 파일
     * @return
     */
    ResponseDTO<?> setUserSign(HttpServletRequest req, List<MultipartFile> files);

    /**
     * INTRA 전용 유저정보 엑셀로 받기
     * @return
     */
    ResponseDTO<?> getUserGroup();

    /**
     * Intra 전용 유저정보 여러개 일괄 저장
     * @param dto 유저정보
     * @return
     */
    ResponseDTO<?> setUserGroup(InfraUser.UserGroupDTO dto);

    /**
     * Intra 전용 인사설정 저장용
     * @param dtos 인사설정 저장용 객체
     * @return
     */
    ResponseDTO<?> setUserRel(List<InfraUser.UserRelDTO> dtos);

    /**
     * Intra 전용 인사설정 삭제용
     * @param dtos 인사설정 삭제용 객체
     * @return
     */
    ResponseDTO<?> delUserRel(List<InfraUser.UserRelDTO>dtos);
}

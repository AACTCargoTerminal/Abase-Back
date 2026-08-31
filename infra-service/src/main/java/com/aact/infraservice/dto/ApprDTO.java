package com.aact.infraservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprDTO {

    // 기안번호
    private BigDecimal apprId;
    // 제목
    private String title;
    // 요청부서
    private String reqDeptCode;
    // 현재 상태 - DB 저장값
    private BigDecimal statusSid;
    // 현재 상태 - 공통코드에서 조회
    private String statusCode;
    // 현재 상태 사유
    private String statusReason;
    // 현재 결재자 - DB 저장값
    private BigDecimal currentApprSid;
    // 현재 결재자 - USER_MASTER에서 조회
    private String currentApprId;
    // 작성자 - DB 저장값
    private BigDecimal writerSid;
    // 작성자 - USER_MASTER에서 조회
    private String writerId;
    private LocalDateTime createDt;
    private LocalDateTime completeDt;
    // 반려
    private String rejectReason;
    // 반려처리자 - DB 저장값
    private BigDecimal rejectBySid;
    // 반려처리자 - USER_MASTER에서 조회
    private String rejectById;
    private LocalDateTime rejectDt;
    private LocalDateTime updateDt;
    // 표시 여부
    private String usableFlag;
    // 로그인 사용자 기준 참조부서 여부
    private String viewByRefDeptYn;
    // 참조부서
    private List<String> refDeptCodes;

}

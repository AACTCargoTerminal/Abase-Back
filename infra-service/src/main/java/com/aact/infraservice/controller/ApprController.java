package com.aact.infraservice.controller;

import com.aact.common.ResponseDTO;
import com.aact.infraservice.dto.ApprDTO;
import com.aact.infraservice.service.ApprService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("appr")
@RequiredArgsConstructor
public class ApprController {

    private final ApprService apprService;

    // 기안 목록 조회
    @GetMapping
    public ResponseDTO<?> getApprM010_001(@RequestParam(required = false) String title, @RequestParam(required = false) String reqDeptCode, @RequestParam(required = false) BigDecimal statusSid) {
        ApprDTO apprDTO = new ApprDTO();

        apprDTO.setTitle(title);
        apprDTO.setReqDeptCode(reqDeptCode);
        apprDTO.setStatusSid(statusSid);

        return apprService.getApprM010_001(apprDTO);
    }

    // 기안 상세 조회
    @GetMapping("/detail")
    public ResponseDTO<?> getApprM010_002(@RequestParam BigDecimal apprId) {
        return apprService.getApprM010_002(apprId);
    }

    // 기안 참조부서 조회
    @GetMapping("/ref-dept")
    public ResponseDTO<?> getApprM010_003(@RequestParam BigDecimal apprId) {
        return apprService.getApprM010_003(apprId);
    }

    // 관리자 기안 목록 전체 조회
    @GetMapping("/admin")
    public ResponseDTO<?> getApprM010_004(@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate, @RequestParam(required = false) String title, @RequestParam(required = false) String reqDeptCode, @RequestParam(required = false) BigDecimal statusSid) {
        ApprDTO apprDTO = new ApprDTO();

        apprDTO.setFromDate(fromDate);
        apprDTO.setToDate(toDate);
        apprDTO.setTitle(title);
        apprDTO.setReqDeptCode(reqDeptCode);
        apprDTO.setStatusSid(statusSid);

        return apprService.getApprM010_004(apprDTO);
    }

    // 관리자 부서별 기안 조회
    @GetMapping("/admin/dept")
    public ResponseDTO<?> getApprM010_005(@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate, @RequestParam(required = false) String title, @RequestParam(required = false) String reqDeptCode, @RequestParam(required = false) BigDecimal statusSid) {
        ApprDTO apprDTO = new ApprDTO();

        apprDTO.setFromDate(fromDate);
        apprDTO.setToDate(toDate);
        apprDTO.setTitle(title);
        apprDTO.setReqDeptCode(reqDeptCode);
        apprDTO.setStatusSid(statusSid);

        return apprService.getApprM010_005(apprDTO);
    }

    // 부서별 사용자 목록 조회
    @GetMapping("/dept-user")
    public ResponseDTO<?> getApprM010_006(@RequestParam String deptCode) {
        return apprService.getApprM010_006(deptCode);
    }

    // 결재 사용자 목록 조회
    @GetMapping("/approval-user")
    public ResponseDTO<?> getApprM010_007() {
        return apprService.getApprM010_007();
    }

    // 기안 등록
    @PostMapping
    public ResponseDTO<?> setApprM010_010(@RequestBody ApprDTO apprDTO) {
        return apprService.setApprM010_010(apprDTO);
    }

    // 기안 비활성화
    @DeleteMapping
    public ResponseDTO<?> setApprM010_020(@RequestParam BigDecimal apprId) {
        return apprService.setApprM010_020(apprId);
    }

    // 참조부서 비활성화
    @DeleteMapping("/ref-dept")
    public ResponseDTO<?> setApprM010_021(@RequestParam BigDecimal apprId, @RequestParam String deptCode) {
        return apprService.setApprM010_021(apprId, deptCode);
    }
}
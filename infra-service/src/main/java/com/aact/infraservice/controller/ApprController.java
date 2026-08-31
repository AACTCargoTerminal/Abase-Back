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
    public ResponseDTO<?> getList(@RequestParam(required = false) String title, @RequestParam(required = false) String reqDeptCode, @RequestParam(required = false) BigDecimal statusSid) {
        ApprDTO apprDTO = new ApprDTO();

        apprDTO.setTitle(title);
        apprDTO.setReqDeptCode(reqDeptCode);
        apprDTO.setStatusSid(statusSid);

        return apprService.getList(apprDTO);
    }

    // 기안 상세 조회
    @GetMapping("/detail")
    public ResponseDTO<?> getDetail(@RequestParam BigDecimal apprId) {
        return apprService.getDetail(apprId);
    }

    // 기안 참조부서 조회
    @GetMapping("/ref-dept")
    public ResponseDTO<?> getRefList(@RequestParam BigDecimal apprId) {
        return apprService.getRefList(apprId);
    }

    // 기안 등록
    @PostMapping
    public ResponseDTO<?> save(@RequestBody ApprDTO apprDTO) {
        return apprService.save(apprDTO);
    }

    // 기안 비활성화
    @DeleteMapping("")
    public ResponseDTO<?> delete(@RequestParam BigDecimal apprId) {
        return apprService.delete(apprId);
    }

    // 참조부서 비활성화
    @DeleteMapping("/ref-dept")
    public ResponseDTO<?> deleteRef(@RequestParam BigDecimal apprId, @RequestParam String deptCode) {
        return apprService.deleteRef(apprId, deptCode);
    }
}
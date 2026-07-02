package com.aact.infraservice.controller;


import com.aact.common.ResponseDTO;
import com.aact.common.SysException;
import com.aact.infraservice.dto.CapsTimeDTO;
import com.aact.infraservice.dto.WorkDTO;
import com.aact.infraservice.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("work")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @GetMapping(value = "/getWorkM010_002")
    public ResponseDTO<?> getWorkM010_002(@RequestParam("date") String date, @RequestParam("deptCode") String deptCode, @RequestParam("approveFlag") String approveFlag) {
        return workService.getWorkM010_002(date, deptCode, approveFlag);
    }

    @PostMapping(value = "/getWorkM010_006")
    public ResponseDTO<?> getWorkM010_006(@RequestBody WorkDTO.HrSearchDTO dto) {
        return workService.getWorkM010_006(dto);
    }

    @GetMapping(value = "/getWorkM010_007")
    public ResponseDTO<?> getWorkM010_007(@RequestParam("date")String date, @RequestParam("userSid")BigDecimal userSid,@RequestParam("seq")BigDecimal seq) {
        return workService.getWorkM010_007(date,userSid,seq);
    }

    @GetMapping(value = "/getHoliDay")
    public ResponseDTO<?> getHoliDay(@RequestParam("date") String date) {
        return workService.getHoliDay(date);
    }

    @PostMapping(value = "/setWorkM010_014")
    public ResponseDTO<?> setWorkM010_014(@RequestBody WorkDTO.SaveDTO dto) {
        return workService.setWorkM010_014(dto);
    }

    @GetMapping(value = "/setWorkM010_017")
    public ResponseDTO<?> setWorkM010_017(@RequestParam("date")String date, @RequestParam("teamCode")String teamCode) {
        return workService.setWorkM010_017(date,teamCode);
    }

    @PostMapping(value = "/setWorkM010_019", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<?> setWorkM010_019(@Validated @ModelAttribute CapsTimeDTO.SearchDTO dto) {
        return workService.setWorkM010_019(dto);
    }
    //getWorkM010_003
    @GetMapping(value = "/getWorkM010_003")
    public ResponseDTO<?> getWorkM010_003(@RequestParam("date") String date,@RequestParam("userSid") BigDecimal userSid,@RequestParam("seq") BigDecimal seq) {
        return workService.getWorkM010_003(date,userSid,seq);
    }

    @GetMapping(value = "/getWorkM010_004")
    public ResponseDTO<?> getWorkM010_004(@RequestParam("date") String date) {
        return workService.getWorkM010_004(date);
    }

    //getWorkM010_005
    @GetMapping(value = "/getWorkM010_005")
    public ResponseDTO<?> getWorkM010_005(@RequestParam("date") String date, @RequestParam("deptCode") String deptCode, @RequestParam("username") String username, @RequestParam("approveFlag") String approveFlag) {
        return workService.getWorkM010_005(date, deptCode, username, approveFlag);
    }

    @PostMapping(value = "/setWorkM010_022")
    public ResponseDTO<?> setWorkM010_022(@RequestBody Map<String, List<CapsTimeDTO.DeleteDTO>> dtos,@RequestParam("adminFlag")String adminFlag) {
        if (dtos.get("DEL") == null) {
            throw new SysException("setWorkM010_022", "삭제 목록이 없습니다.");
        }
        return workService.setWorkM010_022(dtos.get("DEL"),adminFlag);
    }

    //setWorkM010_031
    @PostMapping(value = "/setWorkM010_031")
    public ResponseDTO<?> setWorkM010_031(@RequestBody Map<String, List<CapsTimeDTO.DeleteDTO>> dtos) {
        if (dtos.get("DEL") == null) {
            throw new SysException("setWorkM010_031", "목록이 없습니다.");
        }
        return workService.setWorkM010_031(dtos.get("DEL"));
    }

    //setWorkM010_032
    @PostMapping(value = "/setWorkM010_032")
    public ResponseDTO<?> setWorkM010_032(@RequestBody WorkDTO.ApproveDTO dto) {
        return workService.setWorkM010_032(dto);
    }

    @PostMapping(value = "/setWorkM010_034")
    public ResponseDTO<?> setWorkM010_034(@RequestBody WorkDTO.HrReqSaveDTO dto) {
        return workService.setWorkM010_034(dto);
    }

    @PostMapping(value = "/setWorkM010_035")
    public ResponseDTO<?> setWorkM010_035(@RequestBody WorkDTO.HrReqSaveDTO dto) {
        return workService.setWorkM010_035(dto);
    }

    @PostMapping(value = "/setHrSchSave")
    public ResponseDTO<?> setHrSchSave(@RequestBody WorkDTO.HrSchSaveDTO dto) {
        return workService.setHrSchSave(dto);
    }

    //getWorkSch
    @GetMapping(value = "/getWorkSch")
    public ResponseDTO<?> getWorkSch(@RequestParam("teamCode") String teamCode,
                                     @RequestParam("terminalCode") String terminalCode,
                                     @RequestParam("toDate") String toDate,
                                     @RequestParam("fromDate") String fromDate,
                                     @RequestParam("date") String date,
                                     @RequestParam("type") String type) {
        return workService.getWorkSch(teamCode,terminalCode,toDate,fromDate,date,type);
    }

    @GetMapping(value = "/getWorkTime")
    public ResponseDTO<?> getWorkTime(@RequestParam("teamCode") String teamCode,
                                     @RequestParam("terminalCode") String terminalCode,
                                     @RequestParam("toDate") String toDate,
                                     @RequestParam("fromDate") String fromDate,
                                      @RequestParam("date") String date,
                                      @RequestParam("userName") String userName) {
        return workService.getWorkTime(teamCode,terminalCode,toDate,fromDate,date,userName);
    }

    @GetMapping(value = "/getWorkDetail")
    public ResponseDTO<?> getWorkDetail(@RequestParam("teamCode") String teamCode,
                                      @RequestParam("terminalCode") String terminalCode,
                                      @RequestParam("toDate") String toDate,
                                      @RequestParam("fromDate") String fromDate,
                                        @RequestParam("date") String date) {
        return workService.getWorkDetail(teamCode,terminalCode,toDate,fromDate,date);
    }
    //getExWorkSch
    @GetMapping(value = "/getExWorkSch")
    public ResponseDTO<?> getExWorkSch(@RequestParam("teamCode") String teamCode,
                                        @RequestParam("date") String date) {
        return workService.getExWorkSch(teamCode,date);
    }

    //setWorkM010_037
    @PostMapping(value = "/setWorkM010_038")
    public ResponseDTO<?> setWorkM010_038(@RequestBody CapsTimeDTO.SearchDTO dto) {
        return workService.setWorkM010_038(dto);
    }
    //getExTimeWork
    @GetMapping(value = "/getExTimeWork")
    public ResponseDTO<?> getExTimeWork() {
        return workService.getExTimeWork();
    }

    @PostMapping(value = "/setWorkM010_039")
    public ResponseDTO<?> setWorkM010_039(@RequestBody Map<String, List<CapsTimeDTO.SearchGroupDTO>> dtos) {
        if (dtos.get("OT") == null) {
            throw new SysException("setWorkM010_039", "목록이 없습니다.");
        }
        return workService.setWorkM010_039(dtos.get("OT"));
    }

}

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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("work")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @GetMapping(value = "/getWorkM010_002")
    public ResponseDTO<?> getWorkM010_002(@RequestParam("date") String date, @RequestParam("deptCode") String deptCode, @RequestParam("terminalCode") String terminalCode, @RequestParam("approveFlag") String approveFlag) {
        return workService.getWorkM010_002(date, deptCode, terminalCode,approveFlag);
    }

    @PostMapping(value = "/getWorkL010_008")
    public ResponseDTO<?> getWorkL010_008(@RequestBody WorkDTO.HrSearchDTO dto) {
        return workService.getWorkL010_008(dto);
    }

    @GetMapping(value = "/getWorkL010_009")
    public ResponseDTO<?> getWorkL010_009(@RequestParam("date")String date, @RequestParam("userSid")BigDecimal userSid,@RequestParam("seq")BigDecimal seq) {
        return workService.getWorkL010_009(date,userSid,seq);
    }

    @GetMapping(value = "/getHoliDay")
    public ResponseDTO<?> getHoliDay(@RequestParam("date") String date) {
        return workService.getHoliDay(date);
    }

    @PostMapping(value = "/setWorkM010_014")
    public ResponseDTO<?> setWorkM010_014(@RequestBody WorkDTO.SaveDTO dto) {
        return workService.setWorkM010_014(dto);
    }

    @GetMapping(value = "/setWorkL010_013")
    public ResponseDTO<?> setWorkL010_013(@RequestParam("date")String date, @RequestParam("teamCode")String teamCode, @RequestParam("terminalCode")String terminalCode) {
        return workService.setWorkL010_013(date,teamCode,terminalCode);
    }
    @PostMapping(value = "/setWorkM010_018", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<?> setWorkM010_018(@Validated @ModelAttribute WorkDTO.HrFileSaveDTO dto) {
        return workService.setWorkM010_018(dto);
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

    @GetMapping(value = "/getWorkL010_007")
    public ResponseDTO<?> getWorkL010_007(@RequestParam("date") String date) {
        return workService.getWorkL010_007(date);
    }

    //getWorkM010_005
    @GetMapping(value = "/getWorkL010_006")
    public ResponseDTO<?> getWorkL010_006(@RequestParam("date") String date,
                                          @RequestParam("deptCode") String deptCode,
                                          @RequestParam("terminalCode") String terminalCode,
                                          @RequestParam("username") String username,
                                          @RequestParam("approveFlag") String approveFlag) {
        return workService.getWorkL010_006(date, deptCode, terminalCode,username, approveFlag);
    }

    @PostMapping(value = "/setWorkM010_022")
    public ResponseDTO<?> setWorkM010_022(@RequestBody Map<String, List<CapsTimeDTO.DeleteDTO>> dtos,@RequestParam("adminFlag")String adminFlag) {
        if (dtos.get("DEL") == null) {
            throw new SysException("setWorkM010_022", "삭제 목록이 없습니다.");
        }
        return workService.setWorkM010_022(dtos.get("DEL"),adminFlag);
    }

    //setWorkM010_031
    @PostMapping(value = "/setWorkL010_014")
    public ResponseDTO<?> setWorkM010_031(@RequestBody Map<String, List<CapsTimeDTO.DeleteDTO>> dtos) {
        if(dtos.get("array")==null){
            throw new SysException("setWorkL010_014", "목록이 없습니다.");
        }
        return workService.setWorkL010_014(dtos.get("array"));
    }

    //setWorkM010_032
    @PostMapping(value = "/setWorkL010_015")
    public ResponseDTO<?> setWorkL010_015(@RequestBody WorkDTO.ApproveDTO dto) {
        return workService.setWorkL010_015(dto);
    }

    @PostMapping(value = "/setWorkM010_034")
    public ResponseDTO<?> setWorkM010_034(@RequestBody WorkDTO.HrReqSaveDTO dto) {
        return workService.setWorkM010_034(dto);
    }

    @PostMapping(value = "/setWorkL010_016")
    public ResponseDTO<?> setWorkL010_016(@RequestBody WorkDTO.HrReqSaveDTO dto) {
        return workService.setWorkL010_016(dto);
    }

    @PostMapping(value = "/setWorkL010_018")
    public ResponseDTO<?> setWorkL010_018(@RequestBody WorkDTO.HrReqSaveDTO dto) {
        return workService.setWorkL010_018(dto);
    }

    @PostMapping(value = "/setWorkM010_042")
    public ResponseDTO<?> setWorkM010_042(@RequestBody WorkDTO.HrFileSearchDTO dto) {
        return workService.setWorkM010_042(dto);
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
                                        @RequestParam("date") String date,
                                       @RequestParam("terminalCode") String terminalCode) {
        return workService.getExWorkSch(teamCode,terminalCode,date);
    }

    //setWorkM010_037
    @GetMapping(value = "/setWorkL010_017")
    public ResponseDTO<?> setWorkL010_017(@RequestParam("date") String date,
                                          @RequestParam("userSid") BigDecimal userSid,
                                          @RequestParam("seq") BigDecimal seq,
                                          @RequestParam("remark") String remark,
                                          @RequestParam("type")String type) {
        return workService.setWorkL010_017(date,userSid,seq,remark,type);
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

    @PostMapping(value = "/setWorkM010_043")
    public ResponseDTO<?> setWorkM010_043(@RequestBody WorkDTO.HrCapsSaveDTO dto) {
        return workService.setWorkM010_043(dto);
    }

    @GetMapping(value = "/setScheduleAutoJob")
    public ResponseDTO<?> setScheduleAutoJob(@RequestParam("dateStr")String dateStr) {
        return workService.setScheduleAutoJob(dateStr);
    }
    //getManual
    @GetMapping(value = "/getManual")
    public ResponseDTO<?> getManual(@RequestParam("fileName")String fileName) {
        return workService.getManual(fileName);
    }
    //setCapsReSave
    //setWorkM010_031
    @PostMapping(value = "/setCapsReSave")
    public ResponseDTO<?> setCapsReSave(@RequestBody Map<String, List<CapsTimeDTO.DeleteDTO>> dtos) {
        if(dtos.get("array")==null){
            throw new SysException("setWorkM010_031", "목록이 없습니다.");
        }
        return workService.setCapsReSave(dtos.get("array"));
    }
}

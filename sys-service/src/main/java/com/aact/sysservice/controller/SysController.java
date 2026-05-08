package com.aact.sysservice.controller;

import com.aact.common.ResponseDTO;
import com.aact.common.SysException;
import com.aact.sysservice.dto.SysDTO;
import com.aact.sysservice.service.SysService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sys")
@Validated
@RequiredArgsConstructor
public class SysController {
    private final SysService sysService;

    @GetMapping(value = "/reload")
    public ResponseDTO<?> reload() {
        return sysService.reload();
    }


    @GetMapping(value = "/getBaseOds")
    public ResponseDTO<?> getBaseOds(@RequestParam("classCode") String classCode,
                                     @RequestParam("codeName") String codeName) {
        return sysService.getBaseOds(classCode, codeName);
    }

    @GetMapping(value = "/getSchP010_001")
    public ResponseDTO<?> getSchP010_001(@RequestParam("fltDate") String fltDate,
                                         @RequestParam("inoutFlag") String inoutFlag) {
        return sysService.getSchP010_001(fltDate, inoutFlag);
    }

    @GetMapping(value = "/getSchM010_001")
    public ResponseDTO<?> getSchM010_001(@RequestParam("schSid") String schSid) {
        return sysService.getSchM010_001(schSid);
    }

    // setUldBuildUpM010_013 수출 서버만들면 그걸로 변경해야함
    @GetMapping(value = "/setUldBuildUpM010_013")
    public ResponseDTO<?> setUldBuildUpM010_013(@RequestParam("cargoSid") List<BigDecimal> cargoSid,
                                                @RequestParam("mfstWt") String[] mfstWt) {
        return sysService.setUldBuildUpM010_013(cargoSid, mfstWt);
    }

    // getCustomerP010_001
    @PostMapping(value = "/getCustomerP010_001")
    public ResponseDTO<?> getCustomerP010_001(@RequestBody SysDTO.CustomerDTO dto) {
        return sysService.getCustomerP010_001(dto);
    }

    @GetMapping(value = "/getCustomerL010_002")
    public ResponseDTO<?> getCustomerL010_002(@RequestParam("customerSid") BigDecimal customerSid) {
        return sysService.getCustomerL010_002(customerSid);
    }

    // sendSitaToEmail
    @PostMapping(value = "/sendSitaToEmail")
    public ResponseDTO<?> sendSitaToEmail(@RequestBody SysDTO.SitaEmailDTO dto) {
        return sysService.sendSitaToEmail(dto);
    }

    // getBaseOdsValue3
    @GetMapping(value = "/getBaseOdsValue3")
    public ResponseDTO<?> getBaseOdsValue3(@RequestParam("classCode") String classCode,
                                           @RequestParam("value3") String value3) {
        return sysService.getBaseOdsValue3(classCode, value3);
    }
    //getBaseOdsValue2
    @GetMapping(value = "/getBaseOdsValue2")
    public ResponseDTO<?> getBaseOdsValue2(@RequestParam("classCode") String classCode,
                                           @RequestParam("value2") String value2) {
        return sysService.getBaseOdsValue2(classCode, value2);
    }
    @GetMapping(value = "/getBaseOdsValue1")
    public ResponseDTO<?> getBaseOdsValue1(@RequestParam("classCode") String classCode,
                                           @RequestParam("value1") String value1) {
        return sysService.getBaseOdsValue1(classCode, value1);
    }

    @GetMapping(value = "/getBoardL010_001")
    public ResponseDTO<?> getBoardL010_001() {
        return sysService.getBoardL010_001();
    }
    //getCodeA010Mgm
    @GetMapping(value = "/getCodeA010Mgm")
    public ResponseDTO<?> getCodeA010Mgm(@RequestParam("classCode")String classCode,@RequestParam("codeName")String codeName,@RequestParam("usableFlag")String usableFlag) {
        return sysService.getCodeA010Mgm(classCode,codeName,usableFlag);
    }

    @PostMapping(value = "/setCodeA010_012")
    public ResponseDTO<?> setCodeA010_012(@RequestBody Map<String,List<SysDTO.CodeSaveDTO>> dto) {
        if(dto.get("SAVE")==null){
            throw new SysException("setCodeA010_012","데이터 없음.");
        }
        return sysService.setCodeA010_012(dto.get("SAVE"));
    }

    @PostMapping(value = "/setCodeA010_022")
    public ResponseDTO<?> setCodeA010_022(@RequestBody Map<String,List<SysDTO.CodeDelDTO>> dto) {
        if(dto.get("DEL")==null){
            throw new SysException("setCodeA010_022","데이터 없음.");
        }
        return sysService.setCodeA010_022(dto.get("DEL"));
    }
    @GetMapping(value = "/getSch")
    public ResponseDTO<?> getSch(@RequestParam("fltDate") String fltDate, @RequestParam("inoutFlag") String inoutFlag) {
        return sysService.getSch(fltDate, inoutFlag);
    }

    @GetMapping(value = "/getSch003")
    public ResponseDTO<?> getSch003(@RequestParam("fltDate") String fltDate,
                                    @RequestParam("inoutFlag") String inoutFlag) {
        return sysService.getSch003(fltDate, inoutFlag);
    }
}

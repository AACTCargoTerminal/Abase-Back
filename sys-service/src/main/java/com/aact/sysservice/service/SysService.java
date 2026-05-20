package com.aact.sysservice.service;

import com.aact.common.*;
import com.aact.common.SourcName;
import com.aact.common.ServiceBase;
import com.aact.sysservice.config.SysCodeCache;
import com.aact.sysservice.dto.SysDTO;
import com.aact.sysservice.repo.SysRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysService extends ServiceBase {

    private final ObjectProvider<SysRepo> sysRepo;
    private final SysCodeCache codeCache;

    public ResponseDTO<?> reload(){
        return execute(()->{
           codeCache.load();
           return ResponseDTO.builder().errFlag("N").errMsg("재조회 완료").build();
        });
    }

    public ResponseDTO<?> reload(String classCode){
        return execute(()->{
            codeCache.loadClassCode(classCode);
            return ResponseDTO.builder().errFlag("N").errMsg("재조회 완료").build();
        });
    }

    public ResponseDTO<?> getSch(String fltDate, String inoutFlag) {
        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;
            dbRet = repo.getSch(fltDate, inoutFlag, "Y", info.getUserOperationCarrier(), info.getUserLang(),
                    Util.getGUID(), info.getUserId(), info.getUserIpAddress(), "Main.master");

            return okOrThrow("getSch", dbRet);
        });

    }

    public ResponseDTO<?> getSch003(String fltDate, String inoutFlag) {


        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;
            dbRet = repo.getSch003(fltDate, "", inoutFlag, info.getUserOperationCarrier(), "Y", info.getUserLang(),
                    Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getSch003", dbRet);
        });
    }
    
    public ResponseDTO<?> getBaseOds(String classCode, String codeName) {

        return execute(()->{

            return okOrThrow("getBaseOds", codeCache.getCodeToName(classCode,codeName,null,null,null,null,null,null,null,null,null));
        });

    }

    public ResponseDTO<?> getBaseOdsValue3(String classCode, String value3) {

        return execute(()->{
            return okOrThrow("getBaseOds", codeCache.getCodeToName(classCode,null,null,null,value3,null,null,null,null,null,null));
        });

    }

    public ResponseDTO<?> getBaseOdsValue2(String classCode, String value2) {
        return execute(()->{
            return okOrThrow("getBaseOds", codeCache.getCodeToName(classCode,null,null,value2,null,null,null,null,null,null,null));
        });
    }

    public ResponseDTO<?> getBaseOdsValue1(String classCode, String value1) {
        return execute(()->{
            return okOrThrow("getBaseOds", codeCache.getCodeToName(classCode,null,value1,null,null,null,null,null,null,null,null));
        });
    }

    public ResponseDTO<?> getSchP010_001(String fltDate, String inoutFlag) {

        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();

        return execute(repo,()->{
            DbDto dt = repo.getSchP010_001(fltDate, inoutFlag, info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());


            return okOrThrow("getSchP010_001", dt);
        });
    }

    public ResponseDTO<?> getSchM010_001(String schSid) {

        ClsUserInfo info = UserContext.get();

        BigDecimal sid = new BigDecimal(schSid);
        SysRepo repo = sysRepo.getObject();

        return execute(repo,()->{
            DbDto dt = repo.getSchM010_001(sid, info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getSchM010_001", dt);
        });

    }

    // 임시 수출 서버만들면 변경해야함

    public ResponseDTO<?> setUldBuildUpM010_013(List<BigDecimal> cargoSidArray, String[] mfstWtArray) {
        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();

        return execute(repo,()->{
            DbDto dbDto = null;
            for (int i = 0; i < cargoSidArray.size(); i++) {
                if (mfstWtArray[i] == null || mfstWtArray[i].equals("0") || mfstWtArray[i].equals("")) {
                    //패스
                } else {
                    dbDto = repo.setUldBuildUpM010_013(cargoSidArray.get(i), mfstWtArray[i], info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                    if (dbDto.getErrFlag().equals("N")) {
                    } else {
                        throw new BizException("setUldBuildUpM010_013", dbDto.getErrMsg());
                    }
                }
            }


            return okOrThrow("setUldBuildUpM010_013", dbDto);
        });

    }

    public ResponseDTO<?> getCustomerP010_001(SysDTO.CustomerDTO dto) {

        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();
        return execute(repo,()->{
            DbDto dt = repo.getCustomerP010_001(dto.customerName, dto.registrationNo, dto.customerFlag, dto.vendorFlag, dto.carrierFlag, dto.agencyFlag, dto.customsFlag, dto.iataFlag, info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getCustomerP010_001", dt);
        });

    }

    public ResponseDTO<?> getCustomerL010_002(BigDecimal customerSid) {

        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();
        return execute(repo,()->{
            DbDto dt = repo.getCustomerL010_002(customerSid, "Y", info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getCustomerL010_002", dt);
        });

    }

    public ResponseDTO<?> sendSitaToEmail(SysDTO.SitaEmailDTO dto) {

        ClsUserInfo info = UserContext.get();

        SysRepo repo = sysRepo.getObject();

        return execute(repo, SourcName.INFRA,()->{
            DbDto ret = null;
            String emailGuid = Util.getGUID();
            String msgGuid = Util.getGUID();
            String sendEmail = info.getUserEmail();
            if (!dto.senderEmail().equals("")) {
                sendEmail = dto.senderEmail();
            }

            ret = repo.setEmailManageL010_012(emailGuid, msgGuid, BigDecimal.ZERO, "", dto.title(), dto.msg(), "FWB", sendEmail, info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            if (ret.getErrFlag().equals("Y")) {
                throw new BizException("setEmailManageL010_012", ret.getErrMsg());
            }

            for (String row : dto.recvEmai()) {
                if (!row.equalsIgnoreCase("")) {
                    String refGuid = Util.getGUID();
                    ret = repo.setEmailManageL010_013(emailGuid, refGuid, "RECV", row, info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                    if (ret.getErrFlag().equals("Y")) {
                        throw new BizException("setEmailManageL010_013", ret.getErrMsg());
                    }
                }

            }

            return okOrThrow("sendSitaToEmail", ret);
        });

    }

    public ResponseDTO<?> getBoardL010_001() {

        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();

        return execute(repo,()->{
            DbDto dt = repo.getBoardL010_001("Y", info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getBoardL010_001", dt);
        });

    }

    public ResponseDTO<?> getCodeA010Mgm(String classCode,String codeName, String usableFlag) {
        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();

        return execute(repo,()->{
            DbDto dbRet = null;
            dbRet = repo.getCodeA010_001(classCode, "N", "Y", info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("getCodeA010Mgm", dbRet.getErrMsg());
            } else {
                if (dbRet.getResult().get(0) == null || dbRet.getResult().get(0).isEmpty()) {
                    throw new BizException("getCodeA010Mgm", "해당 공통코드 항목은 없습니다. ( " + classCode + " )");
                }
            }
            ResponseDTO<Map<Integer, List<Map<String, Object>>>> ret1 = ResponseDTO.from(dbRet);

            Map<String, Object> map0 = ret1.getData().get(0).get(0);

            Map<String, Object> toAdd = new LinkedHashMap<>();

            for (Map.Entry<String, Object> row : map0.entrySet()) {
                String key = row.getKey();
                Object value = row.getValue();
                if (key.contains("CHAR_CLASS_SID")) {
                    if (value instanceof BigDecimal) {
                        if (((BigDecimal) value).compareTo(BigDecimal.ZERO) != 0) {
                            dbRet = repo.callSql("SELECT * FROM TCM_CODE_MASTER WHERE CLASS_SID = " + value);
                            if (dbRet.getErrFlag().equals("Y")) {
                                throw new BizException("getCodeA010Mgm", dbRet.getErrMsg());
                            } else {
                                if (dbRet.getResult().get(0) == null || dbRet.getResult().get(0).isEmpty()) {
                                    throw new BizException("getCodeA010Mgm", "CLASS 항목에 문제가 있습니다.\n관리자에게 문의하세요.");
                                }
                                ResponseDTO<Map<Integer, List<Map<String, Object>>>> tmp = ResponseDTO.from(dbRet);
                                toAdd.put(key + "_ARRAY", tmp.getData().get(0));
                            }
                        }
                    }
                }
            }

            if (!toAdd.isEmpty()) {
                map0.putAll(toAdd);
            }

            dbRet = repo.getCodeA010_002(classCode, codeName, usableFlag, info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("getCodeA010Mgm", dbRet.getErrMsg());
            } else {
                if (dbRet.getResult().get(0) == null) {
                    throw new BizException("getCodeA010Mgm", "해당 공통코드 항목은 없습니다. ( " + classCode + " )");
                }
            }

            ResponseDTO<Map<Integer, List<Map<String, Object>>>> ret2 = ResponseDTO.from(dbRet);
            ret1.getData().put(1, ret2.getData().get(0));

            return okOrThrow("getCodeA010Mgm", ret1);
        });
    }

    public ResponseDTO<?> setCodeA010_012(List<SysDTO.CodeSaveDTO> dtoList) {

        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();

        return execute(repo,()->{
            DbDto dtRet = null;


            for (SysDTO.CodeSaveDTO row : dtoList) {
                dtRet = repo.setCodeA010_012(row.classCode(), row.code(), row.asisCode(), row.codeName1(), row.codeName2(), row.codeDesc(), row.value1Char(), row.value2Char(), row.value3Char(), row.value4Char(), row.value5Char(), row.value6Char(), row.value7Char(), row.value8Char(), row.value9Char(), row.value1Num(), row.value2Num(), row.value3Num(), row.value4Num(), row.value5Num(), row.value6Num(), row.value7Num(), row.value8Num(), row.value9Num(), row.seq(), info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dtRet.getErrFlag().equals("Y")) {
                    throw new BizException("setCodeA010_012", dtRet.getErrMsg());
                }
            }


            return okOrThrow("setCodeA010_012", dtRet);
        });

    }

    public ResponseDTO<?> setCodeA010_022(List<SysDTO.CodeDelDTO> dtoList) {

        ClsUserInfo info = UserContext.get();
        SysRepo repo = sysRepo.getObject();

        return execute(repo,()->{
            DbDto dtRet = null;


            for (SysDTO.CodeDelDTO row : dtoList) {
                dtRet = repo.setCodeA010_022(row.classCode(), row.code(), "N", info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dtRet.getErrFlag().equals("Y")) {
                    throw new BizException("setCodeA010_022", dtRet.getErrMsg());
                }
            }


            return okOrThrow("setCodeA010_022", dtRet);
        });

    }

}
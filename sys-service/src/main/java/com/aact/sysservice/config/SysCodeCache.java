package com.aact.sysservice.config;


import com.aact.common.DbDto;
import com.aact.common.ResponseDTO;
import com.aact.common.Util;
import com.aact.sysservice.repo.SysRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SysCodeCache {

    private final ObjectProvider<SysRepo> sysRepo;

    private final Map<String, List<Map<String, Object>>> cache = new ConcurrentHashMap<String, List<Map<String, Object>>>();

    @PostConstruct
    public void load() {
        log.info("공통코드 캐시 로딩 시작...");

        SysRepo repo = sysRepo.getObject();
        if(!cache.isEmpty()){
            cache.clear();
        }

        DbDto dbRet = repo.getAllCode();

        if (dbRet.getErrFlag().equals("Y")) {
            ResponseDTO.setError("공통코드 로드", dbRet.getErrMsg());
        } else {
            ResponseDTO<Map<Integer, List<Map<String, Object>>>> ret = ResponseDTO.from(dbRet);
            for(Map<String,Object> row:ret.getData().get(0)){

                String classCode = Util.getStrChk(row.get("CLASS_CODE"));
                if(classCode.isBlank()){
                    log.error("공통코드 에러 : {}","빈클래스 코드");
                    throw new IllegalStateException("공통코드 에러");
                }

                if(cache.get(classCode)==null){
                    cache.put(classCode,new ArrayList<>());
                    cache.get(classCode).add(row);
                }else{
                    cache.get(classCode).add(row);
                }
            }
        }

        log.info("공통코드 캐시 로딩 완료: {}건", cache.size());
    }

    public void loadClassCode(String classCode) {
        log.info(classCode+"공통코드 캐시 로딩 시작...");

        if(!cache.isEmpty()&&cache.get(classCode)!= null){
            cache.put(classCode,new ArrayList<>());
        }

        SysRepo repo = sysRepo.getObject();

        DbDto dbRet = repo.getAllCode(classCode);

        if (dbRet.getErrFlag().equals("Y")) {
            ResponseDTO.setError("공통코드 로드", dbRet.getErrMsg());
        } else {
            ResponseDTO<Map<Integer, List<Map<String, Object>>>> ret = ResponseDTO.from(dbRet);
            for(Map<String,Object> row:ret.getData().get(0)){

                String tmpClassCode = Util.getStrChk(row.get("CLASS_CODE"));
                if(tmpClassCode.isBlank()){
                    log.error(classCode+"공통코드 에러 : {}","빈클래스 코드");
                    throw new IllegalStateException("공통코드 에러");
                }

                if(cache.get(classCode)==null){
                    cache.put(classCode,new ArrayList<>());
                    cache.get(classCode).add(row);
                }else{
                    cache.get(classCode).add(row);
                }
            }
        }

        log.info(classCode+"공통코드 캐시 로딩 완료: {}건", cache.size());
    }

    public ResponseDTO<List<Map<String, Object>>> getCodeToName(String classCode, String codeName,
                                                                String str1,
                                                                String str2,
                                                                String str3,
                                                                String str4,
                                                                String str5,
                                                                String str6,
                                                                String str7,
                                                                String str8,
                                                                String str9){
        try{
            List<Map<String,Object>> list = cache.get(classCode);
            if(list == null||list.isEmpty()){
                return ResponseDTO.setError2("getCodeToName",classCode+"관련 코드는 없습니다.");
            }
            List<Map<String, Object>> filtered = list.stream()
                    .filter(row -> {
                        if (str1 != null&&!str1.isBlank() && !str1.equals(Util.getStrChk(row.get("VALUE1_CHAR")))) return false;
                        if (str2 != null&&!str2.isBlank() && !str2.equals(Util.getStrChk(row.get("VALUE2_CHAR")))) return false;
                        if (str3 != null&&!str3.isBlank() && !str3.equals(Util.getStrChk(row.get("VALUE3_CHAR")))) return false;
                        if (str4 != null&&!str4.isBlank() && !str4.equals(Util.getStrChk(row.get("VALUE4_CHAR")))) return false;
                        if (str5 != null&&!str5.isBlank() && !str5.equals(Util.getStrChk(row.get("VALUE5_CHAR")))) return false;
                        if (str6 != null&&!str6.isBlank() && !str6.equals(Util.getStrChk(row.get("VALUE6_CHAR")))) return false;
                        if (str7 != null&&!str7.isBlank() && !str7.equals(Util.getStrChk(row.get("VALUE7_CHAR")))) return false;
                        if (str8 != null&&!str8.isBlank() && !str8.equals(Util.getStrChk(row.get("VALUE8_CHAR")))) return false;
                        if (str9 != null&&!str9.isBlank() && !str9.equals(Util.getStrChk(row.get("VALUE9_CHAR")))) return false;
                        if (codeName != null&&!codeName.isBlank()) {
                            String codeCode = Util.getStrChk(row.get("CODE_CODE")).toLowerCase();
                            String codeNameDef = Util.getStrChk(row.get("CODE_NAME")).toLowerCase();
                            String codeName2 = Util.getStrChk(row.get("CODE_NAME2")).toLowerCase();

                            if (!(codeCode.contains(codeName.toLowerCase())
                                    || codeNameDef.contains(codeName.toLowerCase())
                                    || codeName2.contains(codeName.toLowerCase()))) {
                                return false;
                            }
                        }

                        return true;
                    })
                    .toList();
            return ResponseDTO.<List<Map<String, Object>>>builder().errFlag("N").errMsg("조회완료").data(filtered).build();
        }catch (Exception ex){
            return ResponseDTO.setError2("getCodeToName",ex);
        }
    }

}

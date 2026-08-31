package com.aact.infraservice.service;

import com.aact.common.*;
import com.aact.common.ServiceBase;
import com.aact.commonClient.service.ClientService;
import com.aact.commonClient.service.FileClientService;
import com.aact.infraservice.dto.CapsGetType;
import com.aact.infraservice.dto.CapsTimeDTO;
import com.aact.infraservice.dto.ExcelDTO;
import com.aact.infraservice.dto.WorkDTO;
import com.aact.infraservice.repo.WorkRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkService extends ServiceBase {

    private final ObjectProvider<WorkRepo> workRepoProvider;
    private final CapsService capsService;
    private final FileClientService fileClientService;
    private final ClientService clientService;

    public ResponseDTO<?> getWorkM010_002(String date, String deptCode, String terminalCode,String approveFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkM010_002(date, deptCode, terminalCode, Util.getStrChk(approveFlag), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getWorkM010_002", dbRet);
        });
    }

    public ResponseDTO<?> getWorkM010_006(WorkDTO.HrSearchDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkM010_006(dto.type(),dto.reqFlag(),dto.deptCode(),dto.terminalCode(),dto.toDate(),dto.fromDate(),dto.date(),dto.userName(),
                    dto.otFlag(),info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getWorkM010_006", dbRet);
        });
    }

    public ResponseDTO<?> getWorkM010_007(String date,BigDecimal userSid,BigDecimal seq) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            String yyyy = date.substring(0,4);
            String mon = date.substring(4,6);
            Integer day = Integer.parseInt(date.substring(6,8));
            DbDto dbRet = repo.getWorkM010_007(yyyy,mon,userSid,Util.getStrChk(day),seq,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getWorkM010_007", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_041(WorkDTO.HrReqSaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

            DbDto dbRet = null;
            for (WorkDTO.HrReqSaveRowDTO row : dto.reqArray()) {
                dbRet = repo.setWorkM010_041(row.year(), row.mon(), row.userSid(), row.day(), row.seq(),info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_041", dbRet.getErrMsg());
                }
            }
            return okOrThrow("setWorkM010_041", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_042(WorkDTO.HrFileSearchDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

            DbDto dbRet = repo.setWorkM010_042(dto.year(), dto.mon(), dto.userSid(), dto.day(), dto.seq(),dto.imgType(),info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_042", dbRet.getErrMsg());
            }

            if(dbRet.getResult().get(0).isEmpty()){
                throw new BizException("setWorkM010_042", "파일이 없습니다.");
            }

            List<ExcelDTO.HoldImgDTO> tmpFile = new ArrayList<>();

            for(Map<String, DbTypeDTO> row: dbRet.getResult().get(0)){
                ResponseDTO<byte[]> fileRet = fileClientService.fileRead(Util.getStrChk(row.get("FULL_PATH").getObj()));
                if(fileRet.getErrFlag().equals("Y")){
                    throw new BizException("setWorkM010_042", fileRet.getErrMsg());
                }
                ExcelDTO.HoldImgDTO tmpRow = new ExcelDTO.HoldImgDTO();
                tmpRow.setData(fileRet.getData());
                tmpRow.setMime(Util.getStrChk(row.get("MIME").getObj()));
                tmpFile.add(tmpRow);
            }



            return okOrThrow("setWorkM010_042", ResponseDTO.<List<ExcelDTO.HoldImgDTO>>builder().errFlag("N").errMsg("조회완료").data(tmpFile).build());
        });
    }

    public ResponseDTO<?> setWorkM010_014(WorkDTO.SaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

                    if (info.getSignData() == null || info.getSignData().length == 0) {
                        throw new BizException("setWorkM010_014", "도장을 등록하여 주세요.");
                    }

                    DbDto dbRet = null;
                    String yyyy = dto.date().substring(0, 4);
                    String mon = dto.date().substring(4, 6);


                    if (dto.userArray() == null) {
                        throw new BizException("setWorkM010_014", "엑셀에 근무자목록이 없습니다.");
                    }

                    DbDto hrtrm = repo.callSql("SELECT CODE_CODE FROM TCM_CODE_MASTER WHERE CLASS_CODE = 'HRTRM' AND USABLE_FLAG = 'Y'");


                    for (WorkDTO.SaveUserDTO row : dto.userArray()) {
                        BigDecimal userSid = null;

                        if (row.dayArray() == null) {
                            throw new BizException("setWorkM010_014", row.userId() + "에 스케줄 작성이 안돼있습니다.");
                        }

                        for(int dayIdx = 0; dayIdx<row.dayArray().size();dayIdx++){
                            WorkDTO.SaveDayDTO row2 = row.dayArray().get(dayIdx);

                            String tmp = row2.dayStr().replaceAll(" ", "");
                            String[] str = tmp.isEmpty() ? new String[0] : tmp.split("!");

                            if (str.length > 2) {
                                throw new BizException("setWorkM010_014", row.userId() + "근무자 " + row2.day() + "일 " + "하루에 코드는 2개까지입니다.");
                            }
                            if (str.length == 0) {
                                continue;
                            }
                            for (int i = 0; i < str.length; i++) {
                                String dayStrTmp = str[i];
                                String beforeReg = "";
                                String code = "";
                                String terminal = "";
                                BigDecimal ot = new BigDecimal(0);

                                for (int j = 0; j < 3; j++) {
                                    String[] parseTmp = parsingDayStr(dayStrTmp);
                                    if (parseTmp == null) {
                                        throw new BizException("setWorkM010_014", row.userId() + "에 " + row2.day() + "일 코드가 잘못됐습니다. ( " + row2.dayStr() + " )");
                                    }
                                    if (parseTmp[1] == null) {
                                        if (j == 0) {
                                            code = parseTmp[0];
                                        } else {
                                            if (beforeReg.equals("-")) {
                                                terminal = parseTmp[0];
                                            } else if (beforeReg.equals("+")) {
                                                ot = Util.getDecimal(parseTmp[0]);
                                            }
                                        }
                                        break;
                                    }
                                    beforeReg = parseTmp[0];
                                    if (j == 0) {
                                        code = parseTmp[1];
                                    } else {
                                        if (beforeReg.equals("-")) {
                                            terminal = parseTmp[1];
                                        } else if (beforeReg.equals("+")) {
                                            ot = Util.getDecimal(parseTmp[1]);
                                        }
                                    }


                                    dayStrTmp = parseTmp[2];


                                }

                                if (!terminal.isEmpty()) {
                                    if (!terminal.equals("A")) {
                                        boolean flag = false;
                                        for (Map<String, DbTypeDTO> dbRow : hrtrm.getResult().get(0)) {
                                            if (dbRow.get("CODE_CODE").getObj().toString().equals(terminal)) {
                                                flag = true;
                                            }
                                        }

                                        if (!flag) {
                                            throw new BizException("setWorkM010_014", terminal + "< 터미널은 없는 터미널입니다.");
                                        }
                                    }
                                }

                                dbRet = repo.setWorkM010_040(yyyy, mon, row.userId(),row2.day() , new BigDecimal(i),
                                        code,ot,terminal,dto.terminalCode(),dto.teamCode(),
                                        info.getUserLang(), Util.getGUID(),
                                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                                if (dbRet.getErrFlag().equals("Y")) {
                                    throw new BizException("setWorkM010_014", dbRet.getErrMsg());
                                }
                                userSid = Util.getDecimal(dbRet.getRetObj().get("O_USER_SID"));
                            }

                            if((dayIdx == row.dayArray().size()-1)&&row.closeFlag().equals("Y")){
                                dbRet = repo.setWorkM010_014(yyyy, mon, userSid,info.getUserLang(), Util.getGUID(),
                                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                                if (dbRet.getErrFlag().equals("Y")) {
                                    throw new BizException("setWorkM010_014", dbRet.getErrMsg());
                                }
                            }
                        }

                    }
                    return okOrThrow("setWorkM010_014", dbRet);

                }
        );
    }

    public ResponseDTO<?> setHrSchSave(WorkDTO.HrSchSaveDTO dtos){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return  execute(repo,()->{

            if(dtos.schArray() == null||dtos.schArray().isEmpty()){
                throw new BizException("setHrSchSave","수정항목이 없습니다.");
            }
            DbDto dbRet = null;
            for(WorkDTO.HrSchSaveRowDTO row: dtos.schArray()){
                BigDecimal result = row.addHour()
                        .multiply(BigDecimal.valueOf(2))
                        .setScale(0, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(2));
                dbRet = repo.setWorkM010_015(dtos.year(),dtos.mon(),dtos.day(),dtos.userSid(),row.seq(),row.workTypeCode(),result, info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setHrSchSave",dbRet.getErrMsg());
                }


            }
            dbRet = repo.setWorkM010_014(dtos.year(),dtos.mon(),dtos.userId(),info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("setHrSchSave", dbRet);
        });
    }

    public String[] parsingDayStr(String dayStr) {
        try {
            int idx = dayStr.length();
            char found = 0;

            char[] symbols = {'-', '+'};

            for (char c : symbols) {
                int i = dayStr.indexOf(c);
                if (i != -1 && i < idx) {
                    idx = i;
                    found = c;
                }
            }

            // 특수문자가 없는 경우
            if (found == 0) {
                return new String[]{dayStr, null};
            }

            return new String[]{
                    String.valueOf(found),
                    dayStr.substring(0, idx),
                    dayStr.substring(idx + 1)
            };

        } catch (Exception e) {
            return null;
        }
    }

    public ResponseDTO<?> getHoliDay(String date) {
        String sql = "SELECT CALENDAR_DATE " +
                "FROM TCM_CALENDAR_MASTER " +
                "WHERE YYYYMM = '" + date + "' " +
                "AND HOLIDAY_CODE IS NOT NULL";
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            DbDto dbRet = repo.callSql(sql);

            return okOrThrow("getHoliDay", dbRet);
        });


    }

    public ResponseDTO<?> setWorkM010_017(String date,String teamCode,String terminalCode) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkM010_017", "도장을 등록하여 주세요.");
            }
            DbDto dbRet = null;

            dbRet = repo.setWorkM010_017(date.substring(0, 4), date.substring(4, 6), teamCode, terminalCode,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_017", dbRet.getErrMsg());
            }
            return okOrThrow("setWorkM010_017", dbRet);
        });

    }

    public ResponseDTO<?> getWorkM010_003(String date,BigDecimal userSid,BigDecimal seq){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;

            dbRet = repo.getWorkM010_003(date, userSid,seq,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("getWorkM010_003", dbRet.getErrMsg());
            }

           return okOrThrow("getWorkM010_003", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_018(WorkDTO.HrFileSaveDTO dto){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;

            if(dto.files()==null||dto.files().isEmpty()){
                throw new BizException("setWorkM010_018", "파일이 없습니다.");
            }

            ResponseDTO<?> saveRet = fileClientService.fileSave(dto.files());
            if (saveRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_018", "파일저장실패 : " + saveRet.getErrMsg());
            }

            List<FileMeta> metaRet = Util.castIfMatch(saveRet.getData(), new TypeReference<List<FileMeta>>() {
            });
            if (metaRet == null || metaRet.isEmpty()) {
                throw new BizException("setWorkM010_018", "파일변환실패");
            }

            FileMeta meta = metaRet.get(0);
            BigDecimal fileSize = Util.getDecimal(meta.getSize());
            if (fileSize.compareTo(BigDecimal.ZERO) == 0) {
                throw new BizException("setWorkM010_018", "파일사이즈 문제");
            }

            dbRet = repo.setWorkM010_018(dto.year(), dto.mon(), Util.getInteger(dto.day()).toString(), dto.userSid(), dto.seq(), dto.imgType(),
                    meta.getDir(), meta.getOriginName(), meta.getChangeName(), meta.getFullpath(),
                    fileSize, meta.getMime(), meta.getExt(), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_018", dbRet.getErrMsg());
            }

            return okOrThrow("setWorkM010_018", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_019(CapsTimeDTO.SearchDTO dto) {

        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = null;

            if(dto.addDay().compareTo(BigDecimal.ONE) > 0){
                throw new BizException("setWorkM010_019", "날짜를 점검해주세요.");
            }

            Map<String,Object> retTmp = info.getRelArray().stream().filter(v->v.get("CLASS_CODE").equals("HRPAT")).findFirst().orElse(null);

            if(retTmp == null){
                throw new BizException("setWorkM010_019", "파트가 없습니다.");
            }

            String teamCode = Util.getStrChk(retTmp.get("CODE_CODE"));

            if(teamCode.isEmpty()){
                throw new BizException("setWorkM010_019", "파트가 없습니다.");
            }

            ResponseDTO<List<Map<String, Object>>> res = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRPAT")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(res.getErrFlag().equals("Y")){
                throw new BizException("setWorkM010_019", res.getErrMsg());
            }

            Map<String, Object> tmpTeam = res.getData().stream().filter((v)->Util.getStrChk(v.get("CODE_CODE")).equals(teamCode))
                    .findFirst().orElse(null);

            if(tmpTeam == null){
                throw new BizException("setWorkM010_019", "파트가 없습니다.");
            }
            String yyyy = dto.reqStartDate().substring(0, 4);
            String mon = dto.reqStartDate().substring(4, 6);
            String day = String.valueOf(Integer.parseInt(dto.reqStartDate().substring(6, 8)));

            ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsService.findDateToId(CapsGetType.START,info.getUserId(),dto.reqStartDate(),dto.reqStartTime());
            if(startSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", startSet.getErrMsg());
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate endDate = LocalDate.parse(dto.reqStartDate(),formatter).plusDays(dto.addDay().longValue());
            ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsService.findDateToId(CapsGetType.END,info.getUserId(),endDate.format(formatter),dto.reqEndTime());
            if(endSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", endSet.getErrMsg());
            }


            if(Util.getInteger(tmpTeam.get("VALUE1_NUMBER"))==1){
                dbRet = repo.setWorkM010_020(yyyy, mon, day, info.getUserSid(), dto.seq(), startSet.getData().orgTime(), endSet.getData().orgTime(), dto.reqStartTime(), dto.reqEndTime(),
                        dto.addDay(), Util.getDecimal(dto.addHour()), Util.getDecimal(dto.nightHour()),Util.getDecimal(dto.holiHour()), Util.getDecimal(dto.holiAddHour()),
                        dto.remark(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            }else{
                dbRet = repo.setWorkM010_019(yyyy, mon, day, info.getUserSid(), dto.seq(),startSet.getData().orgTime(), endSet.getData().orgTime(), dto.reqStartTime(), dto.reqEndTime(),
                        dto.addDay(),  dto.remark(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            }

            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_019", dbRet.getErrMsg());
            }


            if (dto.files() == null || dto.files().isEmpty()) {
                throw new BizException("setWorkM010_019", "서명이 없습니다.");
            }
            ResponseDTO<?> saveRet = fileClientService.fileSave(dto.files());

            if (saveRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_019", "파일저장실패 : " + saveRet.getErrMsg());
            }

            List<FileMeta> metaRet = Util.castIfMatch(saveRet.getData(), new TypeReference<List<FileMeta>>() {
            });

            if (metaRet == null || metaRet.isEmpty()) {
                throw new BizException("setWorkM010_019", "파일변환실패");
            }

            BigDecimal fileSize = Util.getDecimal(metaRet.get(0).getSize());

            if (fileSize.compareTo(BigDecimal.ZERO) == 0) {
                throw new BizException("setWorkM010_019", "파일사이즈 문제");
            }

            dbRet = repo.setWorkM010_018(yyyy, mon, day, info.getUserSid(), dto.seq(), "TIME",
                    metaRet.get(0).getDir(), metaRet.get(0).getOriginName(), metaRet.get(0).getChangeName(), metaRet.get(0).getFullpath()
                    , fileSize, metaRet.get(0).getMime(), metaRet.get(0).getExt(), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("setWorkM010_019", dbRet);
        });


    }

    public ResponseDTO<?> setWorkM010_039(List<CapsTimeDTO.SearchGroupDTO> dtos) {

        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = null;

            for(CapsTimeDTO.SearchGroupDTO row : dtos){

                String sql = "SELECT USER_SID FROM TCM_USER_MASTER WHERE USABLE_FLAG = 'Y' AND USER_ID = '"+row.userId()+"'";

                dbRet = repo.callSql(sql);

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setWorkM010_039", dbRet.getErrMsg());
                }

                if(dbRet.getResult().get(0).isEmpty()){
                    throw new BizException("setWorkM010_039", row.userId()+" 사번이 없습니다.");
                }

                BigDecimal userSid = Util.getDecimal(dbRet.getResult().get(0).get(0).get("USER_SID").getObj());

                if(row.addDay().compareTo(BigDecimal.ONE) > 0){
                    throw new BizException("setWorkM010_039", "날짜를 점검해주세요.");
                }

                String yyyy = row.reqStartDate().substring(0, 4);
                String mon = row.reqStartDate().substring(4, 6);
                String day = String.valueOf(Integer.parseInt(row.reqStartDate().substring(6, 8)));

                ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsService.findDateToId(CapsGetType.START,row.userId(),row.reqStartDate(),row.reqStartTime());
                if(startSet.getErrFlag().equals("Y")){
                    throw new BizException("findDateToId", startSet.getErrMsg());
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate endDate = LocalDate.parse(row.reqStartDate(),formatter).plusDays(row.addDay().longValue());
                ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsService.findDateToId(CapsGetType.END,row.userId(),endDate.format(formatter),row.reqEndTime());
                if(endSet.getErrFlag().equals("Y")){
                    throw new BizException("findDateToId", endSet.getErrMsg());
                }

                dbRet = repo.setWorkM010_019(yyyy, mon, day, userSid, BigDecimal.ZERO,startSet.getData().orgTime(), endSet.getData().orgTime(), row.reqStartTime(), row.reqEndTime(),
                        row.addDay(),  row.remark(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_039", dbRet.getErrMsg());
                }

            }


            return okOrThrow("setWorkM010_039", dbRet);
        });


    }

    public ResponseDTO<?> setWorkM010_036(String date,BigDecimal userSid,BigDecimal seq,String remark) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = null;
            String yyyy = date.substring(0, 4);
            String mon = date.substring(4, 6);
            String day = String.valueOf(Integer.parseInt(date.substring(6, 8)));

            dbRet = repo.setWorkM010_036(yyyy, mon, day, userSid, seq,remark,  info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_036", dbRet.getErrMsg());
            }

            return okOrThrow("setWorkM010_036", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_038(CapsTimeDTO.SearchDTO dto) {

        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = null;

            if(dto.addDay().compareTo(BigDecimal.ONE) > 0){
                throw new BizException("setWorkM010_038", "날짜를 점검해주세요.");
            }
            String yyyy = dto.reqStartDate().substring(0, 4);
            String mon = dto.reqStartDate().substring(4, 6);
            String day = String.valueOf(Integer.parseInt(dto.reqStartDate().substring(6, 8)));

            ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsService.findDateToId(CapsGetType.START,info.getUserId(),dto.reqStartDate(),dto.reqStartTime());
            if(startSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", startSet.getErrMsg());
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate endDate = LocalDate.parse(dto.reqStartDate(),formatter).plusDays(dto.addDay().longValue());
            ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsService.findDateToId(CapsGetType.END,info.getUserId(),endDate.format(formatter),dto.reqEndTime());
            if(endSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", endSet.getErrMsg());
            }

            dbRet = repo.setWorkM010_038(yyyy, mon, day, dto.userSid(), dto.seq(), startSet.getData().orgTime(), endSet.getData().orgTime(), dto.reqStartTime(), dto.reqEndTime(),
                    dto.addDay(), Util.getDecimal(dto.addHour()), Util.getDecimal(dto.nightHour()),Util.getDecimal(dto.holiHour()), Util.getDecimal(dto.holiAddHour()),
                    dto.remark(), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_038", dbRet.getErrMsg());
            }

            return okOrThrow("setWorkM010_038", dbRet);
        });


    }

    public ResponseDTO<?> getWorkM010_004(String date) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkM010_004(date, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getWorkM010_004", dbRet);
        });
    }

    public ResponseDTO<?> getWorkM010_005(String date, String deptCode, String terminalCode,String userName, String approveFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            String yyyy = "";
            String mon = "";
            String day = "";
            if (date.length() == 6) {
                yyyy = date.substring(0, 4);
                mon = date.substring(4, 6);
            } else if (date.length() == 8) {
                yyyy = date.substring(0, 4);
                mon = date.substring(4, 6);
                day = date.substring(6, 8);
            } else {
                throw new BizException("getWorkM010_005", "날짜 에러");
            }
            DbDto dbRet = repo.getWorkM010_005(yyyy, mon, day, deptCode,terminalCode, userName, approveFlag, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getWorkM010_005", dbRet);
        });
    }


    public ResponseDTO<?> setWorkM010_022(List<CapsTimeDTO.DeleteDTO> dtos,String adminFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            DbDto dbRet = null;
            for (CapsTimeDTO.DeleteDTO row : dtos) {
                String day = String.valueOf(Integer.parseInt(row.date().substring(6, 8)));
                dbRet = repo.setWorkM010_022(row.date().substring(0, 4), row.date().substring(4, 6), day, row.userSid(), row.seq(),adminFlag, info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_022", dbRet.getErrMsg());
                }
            }
            return okOrThrow("setWorkM010_022", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_031(List<CapsTimeDTO.DeleteDTO> dtos) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkM010_031", "도장을 등록하여 주세요.");
            }
            DbDto dbRet = null;
            for (CapsTimeDTO.DeleteDTO row : dtos) {
                String day = String.valueOf(Integer.parseInt(row.date().substring(6, 8)));
                dbRet = repo.setWorkM010_031(row.date().substring(0, 4), row.date().substring(4, 6), day, row.userSid(), row.seq(), row.logSeq(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_031", dbRet.getErrMsg());
                }
            }
            return okOrThrow("setWorkM010_031", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_032(WorkDTO.ApproveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkM010_032", "도장을 등록하여 주세요.");
            }

            DbDto dbRet = null;
            for(WorkDTO.ApproveRowDTO row : dto.userArray()){

                for(String day:row.dayArray()){
                    dbRet = repo.setWorkM010_032(dto.date().substring(0, 4), dto.date().substring(4, 6), day, row.userSid(), info.getUserLang(), Util.getGUID(),
                            info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                    if (dbRet.getErrFlag().equals("Y")) {
                        throw new BizException("setWorkM010_032", dbRet.getErrMsg());
                    }
                }



            }
            return okOrThrow("setWorkM010_032", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_034(WorkDTO.HrReqSaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

            DbDto dbRet = null;
            for (WorkDTO.HrReqSaveRowDTO row : dto.reqArray()) {

                dbRet = repo.setWorkM010_034(row.year(), row.mon(), row.day(), row.userSid(), row.seq(),dto.reqFlag(),info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_034", dbRet.getErrMsg());
                }


            }
            return okOrThrow("setWorkM010_034", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_035(WorkDTO.HrReqSaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

            DbDto dbRet = null;
            for (WorkDTO.HrReqSaveRowDTO row : dto.reqArray()) {

                dbRet = repo.setWorkM010_035(row.year(), row.mon(), row.day(), row.userSid(), row.seq(),info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_035", dbRet.getErrMsg());
                }


            }
            return okOrThrow("setWorkM010_035", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_043(WorkDTO.HrCapsSaveDTO dto){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            DbDto dbRet = null;
            dbRet = repo.setWorkM010_043(dto.getYear(),dto.getMon(),dto.getUserSid(),dto.getDay(),dto.getSeq(),dto.getStartTime(),dto.getEndTime()
                    ,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("setWorkM010_043", dbRet);
        });
    }

    public ResponseDTO<?> setScheduleAutoJob(String dateStr){
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;

            String sql = "";
            sql = "SELECT * FROM TCM_CODE_MASTER WHERE CLASS_CODE = 'HRPAT' AND USABLE_FLAG = 'Y' AND VALUE3_NUMBER = 1";
            dbRet = repo.callSql(sql);

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
            }

            List<Map<String,Object>> hrpat = ResponseDTO.from(dbRet).getData().get(0);

            sql = "SELECT CODE_CODE,VALUE2_CHAR, VALUE3_CHAR FROM TCM_CODE_MASTER WHERE CLASS_CODE = 'OPCOD' AND USABLE_FLAG = 'Y' AND NVL(VALUE1_CHAR,'N') = 'Y' " +
                    "AND ( (VALUE2_CHAR = '09' AND VALUE3_CHAR = '18') " +
                    "OR NVL(VALUE4_CHAR,'N') = 'Y' " +
                    ")";
            dbRet = repo.callSql(sql);

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
            }

            List<Map<String,Object>> opcod = ResponseDTO.from(dbRet).getData().get(0);


            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate now = LocalDate.parse(dateStr, formatter2);
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyyMM");
            String date = now.plusMonths(1).format(formatter);

            sql = "SELECT CALENDAR_DATE, HOLIDAY_CODE FROM TCM_CALENDAR_MASTER WHERE USABLE_FLAG = 'Y' AND YYYYMM = '"+date+"' ORDER BY CALENDAR_TIME";

            dbRet = repo.callSql(sql);

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
            }

            List<Map<String,Object>> cal = ResponseDTO.from(dbRet).getData().get(0);

            List<Map<String,String>> groupArray = new ArrayList<>();

            sql = "SELECT A.USER_SID, A.CODE_CODE TEAM_CODE, IUR.CODE_CODE TERMINAL_CODE, TUM.USER_ID " +
                    "FROM ( SELECT USER_SID, CODE_CODE " +
                    "FROM ( SELECT USER_SID, CODE_CODE " +
                    ", ROW_NUMBER() OVER ( PARTITION BY USER_SID, CODE_CODE ORDER BY VALUE1 DESC ) RN " +
                    "FROM TCM_INTRA_USER_RELATION " +
                    "WHERE CLASS_CODE = 'HRPAT' " +
                    "AND NVL(VALUE1,'00010101') <= '[$1]' " +
                    "AND USABLE_FLAG = 'Y' " +
                    ") WHERE RN = 1 AND CODE_CODE = '[$2]' " +
                    ") A " +
                    "JOIN TCM_INTRA_USER_RELATION IUR " +
                    "ON IUR.USER_SID = A.USER_SID " +
                    "AND IUR.CLASS_CODE = 'TRMCD' " +
                    "AND IUR.USABLE_FLAG = 'Y' " +
                    "JOIN TCM_USER_MASTER TUM " +
                    "ON TUM.USER_SID = A.USER_SID AND TUM.USABLE_FLAG = 'Y'";

            for(Map<String,Object> calRow : cal){
                String calDate = Util.getStrChk(calRow.get("CALENDAR_DATE"));
                String holiCode = Util.getStrChk(calRow.get("HOLIDAY_CODE"));
                String yyyy = calDate.substring(0,4);
                String mon = calDate.substring(4,6);
                String day = Util.getInteger(calDate.substring(6,8)).toString();

                for(Map<String,Object> hrpatRow : hrpat){
                    String hrpatCode = Util.getStrChk(hrpatRow.get("CODE_CODE"));
                    String workTypeCode = "";
                    if(holiCode.isEmpty()){
                        workTypeCode = opcod.stream()
                                .filter(v ->
                                        !Util.getStrChk(v.get("VALUE2_CHAR")).isEmpty()
                                                && !Util.getStrChk(v.get("VALUE3_CHAR")).isEmpty()
                                )
                                .findFirst()
                                .map(v -> Util.getStrChk(v.get("CODE_CODE")))
                                .orElse("");
                    }else{
                        workTypeCode = opcod.stream()
                                .filter(v ->
                                        Util.getStrChk(v.get("VALUE2_CHAR")).isEmpty()
                                                && Util.getStrChk(v.get("VALUE3_CHAR")).isEmpty()
                                )
                                .findFirst()
                                .map(v -> Util.getStrChk(v.get("CODE_CODE")))
                                .orElse("");
                    }

                    if(workTypeCode.isEmpty()){
                        throw new BizException("setScheduleAutoJob","09시에 해당하는 근무코드나, 휴일에 해당하는 근무코드가 없습니다.");
                    }
                    String sql2 = sql
                            .replace("[$1]", calDate)
                            .replace("[$2]", hrpatCode);

                    dbRet = repo.callSql(sql2);

                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                    }

                    List<Map<String,DbTypeDTO>> userTmp = dbRet.getResult().get(0);

                    if(userTmp.isEmpty()){
                        continue;
                    }

                    for(Map<String,DbTypeDTO> userRow : userTmp){



                        BigDecimal userSid = Util.getDecimal(userRow.get("USER_SID").getObj());
                        String userId = Util.getStrChk(userRow.get("USER_ID").getObj());
                        String teamCode = Util.getStrChk(userRow.get("TEAM_CODE").getObj());
                        String terminalCode = Util.getStrChk(userRow.get("TERMINAL_CODE").getObj());

                        if(day.equals("1")){
                            dbRet = repo.setWorkM010_021(yyyy,mon,"00",userSid,new BigDecimal("-1"),"Y","KOR",Util.getGUID(),"DAEMON","::","DAEMON");

                            if(dbRet.getErrFlag().equals("Y")){
                                throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                            }


                        }

                        dbRet = repo.setWorkM010_021(yyyy,mon,day,userSid,new BigDecimal("-1"),"Y","KOR",Util.getGUID(),"DAEMON","::","DAEMON");

                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                        }
                        dbRet = repo.setWorkM010_040(yyyy,mon,userId,day,BigDecimal.ZERO,workTypeCode,
                                BigDecimal.ZERO,"",terminalCode,teamCode,"KOR",Util.getGUID(),"DAEMON","::","DAEMON");
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                        }

                        Map<String,String> groupTmp = groupArray.stream()
                                .filter(v->v.get("TERMINAL_CODE").equals(terminalCode)&&v.get("TEAM_CODE").equals(teamCode))
                                .findFirst().orElse(null);
                        if(groupTmp == null){
                            groupTmp = new HashMap<>();
                            groupTmp.put("TERMINAL_CODE",terminalCode);
                            groupTmp.put("TEAM_CODE",teamCode);
                            groupArray.add(groupTmp);
                        }
                    }



                }
            }

            sql = "SELECT TUM.USER_ID " +
                    "FROM TCM_INTRA_USER_RELATION IUR " +
                    "JOIN TCM_USER_MASTER TUM " +
                    "ON TUM.USER_SID = IUR.USER_SID " +
                    "AND TUM.USABLE_FLAG = 'Y' " +
                    "JOIN TCM_CODE_MASTER TCM " +
                    "ON TCM.CLASS_CODE = 'HRMTR' " +
                    "AND TCM.USABLE_FLAG = 'Y' " +
                    "AND TCM.CODE_CODE = IUR.VALUE1 " +
                    "AND ( " +
                    "          (TCM.VALUE1_CHAR IS NULL " +
                    "           AND TCM.CODE_CODE = '[$1]') " +
                    "       OR " +
                    "          (TCM.VALUE1_CHAR IS NOT NULL " +
                    "           AND INSTR(TCM.VALUE1_CHAR, '[$1]') > 0) " +
                    "       ) " +
                    "WHERE IUR.CLASS_CODE = 'HRTAU' " +
                    "AND NVL(IUR.VALUE2,'*') = '10' " +
                    "AND IUR.CODE_CODE = '[$2]'";

            for(Map<String,String> groupRow : groupArray){

                String sql2 = sql.replace("[$1]",groupRow.get("TERMINAL_CODE")).replace("[$2]",groupRow.get("TEAM_CODE"));
                dbRet = repo.callSql(sql2);

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                }

                if(dbRet.getResult().get(0).isEmpty()){
                    throw new BizException("setScheduleAutoJob",groupRow.get("TEAM_CODE")+" 팀의 팀장은 없습니다.");
                }

                String teamId = Util.getStrChk(dbRet.getResult().get(0).get(0).get("USER_ID").getObj());

                String yyyy = date.substring(0,4);
                String mon = date.substring(4,6);
                dbRet = repo.setWorkM010_017(yyyy,mon,groupRow.get("TEAM_CODE"),groupRow.get("TERMINAL_CODE"),"KOR",Util.getGUID(),teamId,"::","DAEMON");
                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                }
            }

            return okOrThrow("setScheduleAutoJob", dbRet);
        });
    }

    public ResponseDTO<?> getManual(String fileName){
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/MANUAL/"+fileName);
            if(ret.getErrFlag().equals("Y")){
                throw new BizException("getExTimeWork",ret.getErrMsg());
            }
            return ret;
        });
    }

    public ResponseDTO<?> getExTimeWork(){
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/TIME_EX_WORK_FORM.xlsx");
            if(ret.getErrFlag().equals("Y")){
                throw new BizException("getExTimeWork",ret.getErrMsg());
            }
            return ret;
        });
    }

    public ResponseDTO<?> getExWorkSch(String teamCode,String terminalCode ,String date){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;
            String teamName = "";
            String holiday = "";
            List<Map<String,Object>> headers = null;
            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/WORK_EX_SCH_FORM.xlsx");
            if(ret.getErrFlag().equals("Y")){
                throw new BizException("getExWorkSch",ret.getErrMsg());
            }

            ResponseDTO<List<Map<String, Object>>> hrpatRes = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRPAT")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(hrpatRes.getErrFlag().equals("Y")){
                throw new BizException("getExWorkSch", hrpatRes.getErrMsg());
            }

            ResponseDTO<List<Map<String, Object>>> opcodRes = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "OPCOD")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(opcodRes.getErrFlag().equals("Y")){
                throw new BizException("getExWorkSch", opcodRes.getErrMsg());
            }

            Map<String, Object> tmp = hrpatRes.getData().stream().filter(v->v.get("CODE_CODE").equals(teamCode)).findFirst().orElse(null);
            if(tmp==null){
                throw new BizException("getExWorkSch", "팀명을 가져올수 없습니다.");
            }

            teamName = Util.getStrChk(tmp.get("CODE_NAME"));

            dbRet = repo.callSql("SELECT SYS_FUNCTION.FCM_GET_WEEKDAY(WEEK_DAY,'KOR') DD_NAME " +
                    ",TO_CHAR(CALENDAR_TIME,'MM') MON " +
                    ",TO_CHAR(CALENDAR_TIME,'FMDD') DD " +
                    ",NVL(HOLIDAY_CODE,'N') HOLIDAY_CODE " +
                    "FROM TCM_CALENDAR_MASTER WHERE USABLE_FLAG = 'Y' AND YYYYMM = '"+date+"' " +
                    "ORDER BY CALENDAR_TIME ASC");

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("getExWorkSch", dbRet.getErrMsg());
            }
            headers = ResponseDTO.from(dbRet).getData().get(0);
            holiday = Util.getStrChk(dbRet.getResult().get(0).stream().filter((v)->!v.get("HOLIDAY_CODE").getObj().equals("N")).count());

            dbRet = repo.getWorkM010_002(date, teamCode, terminalCode, "N", info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("getExWorkSch", dbRet.getErrMsg());
            }

            ExcelDTO.SchDTO dto = convExSchData(dbRet,date,terminalCode,teamCode,teamName,holiday);

            try(
                    Workbook srcWorkbook = WorkbookFactory.create(new ByteArrayInputStream(ret.getData()));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream()
            ){

                //Example
                Sheet formSheet = srcWorkbook.getSheet("Example");
                Row row = formSheet.getRow(13);
                if (row == null) {
                    row = formSheet.createRow(13);
                }
                Cell cell = row.getCell(0,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue("팀 코드 리스트");
                cell.setCellStyle(getCellStyle_Align(srcWorkbook,true));
                formSheet.addMergedRegion(
                        new CellRangeAddress(13, 14, 0, 3)
                );
                row = formSheet.getRow(15);
                if (row == null) {
                    row = formSheet.createRow(15);
                }
                cell = row.getCell(0,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue("No");
                cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,true,false,true));

                cell = row.getCell(1,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue("코드");
                cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,false,true));

                formSheet.addMergedRegion(
                        new CellRangeAddress(15, 15, 2, 3)
                );
                cell = row.getCell(2,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue("내용");
                cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,true,true));
                cell = row.getCell(3,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,true,true));

                for(int i = 0; i<hrpatRes.getData().size();i++){
                    String num = String.valueOf(i+1);
                    String code = Util.getStrChk(hrpatRes.getData().get(i).get("CODE_CODE"));
                    String name = Util.getStrChk(hrpatRes.getData().get(i).get("CODE_NAME"));
                    row = formSheet.getRow(i+16);
                    if (row == null) {
                        row = formSheet.createRow(i+16);
                    }
                    cell = row.getCell(0,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(num);
                    if(i == 0){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,false,true,false,false));
                    }else if(i== hrpatRes.getData().size()-1){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,true,true,false,false));
                    }else{
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,false,true,false,false));
                    }
                    cell = row.getCell(1,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(code);
                    if(i == 0){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,false,false,false,false));
                    }else if(i== hrpatRes.getData().size()-1){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,true,false,false,false));
                    }else{
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,false,false,false,false));
                    }

                    cell = row.getCell(2,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(name);
                    if(i == 0){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,false,false,false,false));
                    }else if(i== hrpatRes.getData().size()-1){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,true,false,false,false));
                    }else{
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,false,false,false,false));
                    }
                    cell = row.getCell(3,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if(i == 0){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,false,false,true,false));
                    }else if(i== hrpatRes.getData().size()-1){
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,true,false,true,false));
                    }else{
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook,false,false,false,true,false));
                    }
                    formSheet.addMergedRegion(
                            new CellRangeAddress(i+16, i+16, 2, 3)
                    );

                }
                int result = (int)Math.ceil(opcodRes.getData().size() / 15.0);
                row = formSheet.getRow(13);
                if (row == null) {
                    row = formSheet.createRow(13);
                }
                cell = row.getCell(5,  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue("근무 코드 리스트");
                cell.setCellStyle(getCellStyle_Align(srcWorkbook,true));
                formSheet.addMergedRegion(
                        new CellRangeAddress(13, 14, 5, 5+(6*result))
                );
                for(int i = 0;i<result;i++){

                    row = formSheet.getRow(15);
                    if (row == null) {
                        row = formSheet.createRow(15);
                    }
                    cell = row.getCell(5+(i*6),  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("No");
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,true,false,true));

                    cell = row.getCell(6+(i*6),  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("코드");
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,false,true));
                    formSheet.addMergedRegion(
                            new CellRangeAddress(15, 15, 7+(i*6), 10+(i*6))
                    );
                    cell = row.getCell(7+(i*6),  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("코드명");
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,false,true));
                    cell = row.getCell(8+(i*6),  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,false,true));
                    cell = row.getCell(9+(i*6),  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,false,true));
                    cell = row.getCell(10+(i*6),  Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook,true,true,false,true,true));
                }
                int rowStart = 16;      // 시작 행
                int rowLimit = 15;      // 세로로 15개씩
                int colStart = 5;       // 시작 열: F
                int colBlockSize = 6;   // 한 묶음당 6칸
                for (int i = 0; i < opcodRes.getData().size(); i++) {

                    int block = i / rowLimit;                 // 0, 1, 2...
                    int rowIdx = rowStart + (i % rowLimit);   // 16 ~ 30 반복
                    int startCol = colStart + (block * colBlockSize);

                    String num = String.valueOf(i + 1);
                    String code = Util.getStrChk(opcodRes.getData().get(i).get("CODE_CODE"));
                    String name = Util.getStrChk(opcodRes.getData().get(i).get("CODE_NAME"));

                    row = formSheet.getRow(rowIdx);
                    if (row == null) {
                        row = formSheet.createRow(rowIdx);
                    }

                    boolean isFirst = (i % rowLimit == 0);
                    boolean isLast =
                            (i == hrpatRes.getData().size() - 1)
                                    || (i % rowLimit == rowLimit - 1);

                    // 번호
                    cell = row.getCell(startCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(num);
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook, isFirst, isLast, true, false, false));

                    // 코드
                    cell = row.getCell(startCol + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(code);
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook, isFirst, isLast, false, false, false));

                    // 명칭 병합 영역: startCol + 2 ~ startCol + 5
                    cell = row.getCell(startCol + 2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(name);
                    cell.setCellStyle(getCellStyle_Line(srcWorkbook, isFirst, isLast, true, false, false));

                    for (int c = startCol + 3; c <= startCol + 5; c++) {
                        cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellStyle(getCellStyle_Line(srcWorkbook, isFirst, isLast, false, c == startCol + 5, false));
                    }

                    formSheet.addMergedRegion(
                            new CellRangeAddress(
                                    rowIdx,
                                    rowIdx,
                                    startCol + 2,
                                    startCol + 5
                            )
                    );
                }
                //본문부분
                Sheet formSheet2 = srcWorkbook.getSheet("FORM");
                Sheet copySheet = srcWorkbook.cloneSheet(
                        srcWorkbook.getSheetIndex(formSheet2)
                );
                srcWorkbook.setSheetName(
                        srcWorkbook.getSheetIndex(copySheet),
                        Util.getInteger(dto.getMon())+"월"
                );

                row = copySheet.getRow(1);
                cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue("AACT "+dto.getYyyy()+"년 "+dto.getMon()+"월 "+dto.getTeamName()+" SKD");

                row = copySheet.getRow(6);
                cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(dto.getTeamCode());

                row = copySheet.getRow(7);
                cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(terminalCode);

                row = copySheet.getRow(7);
                cell = row.getCell(36, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue("휴무일 : "+dto.getHoliDay());

                for(int i = 0; i<headers.size();i++){
                    row = copySheet.getRow(8);
                    cell = row.getCell(3+i);
                    cell.setCellValue(Util.getInteger(headers.get(i).get("DD")));
                    row = copySheet.getRow(9);
                    cell = row.getCell(3+i);
                    cell.setCellValue(Util.getStrChk(headers.get(i).get("DD_NAME")));

                }
                for(int i = 0;i<dto.getRows().size();i++){
                    int num = i+1;
                    String userId = dto.getRows().get(i).getUserId();
                    String name = dto.getRows().get(i).getUserName();
                    String useHoliday = dto.getRows().get(i).getUseHoliDay();
                    String useAnn = dto.getRows().get(i).getUseAnn();

                    row = copySheet.getRow(10+i);
                    if(row==null){
                        row = copySheet.createRow(10+i);

                    }
                    copyRowStyle(copySheet,10,10+i);
                    cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(num);
                    cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(userId);
                    cell = row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(name);
                    cell = row.getCell(34,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(holiday);
                    cell = row.getCell(35,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(useHoliday);
                    cell = row.getCell(36,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(useAnn);

                    for(Map<String,Object> rowHeader : headers){
                        Integer headerDay = Util.getInteger(rowHeader.get("DD"));
                        List<ExcelDTO.SchCellDTO> tmpCell = dto.getRows().get(i).getCells().stream().filter(v->Util.getInteger(v.getDay()).equals(headerDay)).toList();
                        String str = "";
                        if(!tmpCell.isEmpty()){
                            for(int j=0;j<tmpCell.size();j++){
                                if(j>0){
                                    str = str+"!";
                                }
                                String code = Util.getStrChk(tmpCell.get(j).getWorkTypeCode());
                                String ot = Util.getStrChk(tmpCell.get(j).getAddWorkHour());
                                String trm = Util.getStrChk(tmpCell.get(j).getTmpTerminalCode());
                                if(!code.isEmpty()){
                                    str+=code;
                                }
                                if(!ot.isEmpty()&&!ot.equals("0")){
                                    str+="+"+ot;
                                }
                                if(!trm.isEmpty()){
                                    str+="-"+trm;
                                }
                            }
                        }
                        cell = row.getCell(headerDay+2);
                        cell.setCellValue(str);
                    }
                }


                for (int i = 0; i < srcWorkbook.getNumberOfSheets(); i++) {
                    String name = srcWorkbook.getSheetName(i);

                    if ("Example".equals(name)) {
                        srcWorkbook.setActiveSheet(i);
                        srcWorkbook.setSelectedTab(i);
                        break;
                    }
                }


                int sheetAppr = srcWorkbook.getSheetIndex("FORM");

                srcWorkbook.setSheetHidden(sheetAppr,true);
                srcWorkbook.write(bos);

                ret = ResponseDTO.<byte[]>builder().errFlag("N").errMsg("스케줄표 다운완료").data(bos.toByteArray()).build();

            }catch (IOException ex){
                throw new BizException("getExWorkSch",ex.getMessage());
            }

            return ret;
        });
    }

    public ResponseDTO<?> getWorkSch(String teamCode,String terminalCode,String toDate,String fromDate,String date,String type){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;
            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/WORK_SCH_FORM.xlsx");
            if(ret.getErrFlag().equals("Y")){
                throw new BizException("getWorkSch",ret.getErrMsg());
            }
            String toYYYY = "";
            String toMON = "";
            String toDAY = "";
            String fromYYYY = "";
            String fromMON = "";
            String fromDAY = "";

            if(!toDate.isEmpty()){
                toYYYY = toDate.substring(0,4);
                toMON = toDate.substring(4,6);
                toDAY = toDate.substring(6,8);
            }

            if(!fromDate.isEmpty()){
                fromYYYY = fromDate.substring(0,4);
                fromMON = fromDate.substring(4,6);
                fromDAY = fromDate.substring(6,8);
            }

            dbRet = repo.getWorkM010_008(toYYYY, toMON,toDAY,
                    fromYYYY, fromMON,fromDAY,date,teamCode, terminalCode,type,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("getWorkM010_008", dbRet.getErrMsg());
            }

            ExcelDTO.SchMasterDTO dto = convSchData(dbRet);

            if(dto.getList()==null||dto.getList().isEmpty()){
                throw new BizException("getWorkM010_008", "데이터가 없습니다.");
            }

            try(
                    Workbook srcWorkbook = WorkbookFactory.create(new ByteArrayInputStream(ret.getData()));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream()
            ){

                for(ExcelDTO.SchDTO rowDto : dto.getList()){

                    Sheet formSheet = srcWorkbook.getSheet("FORM");
                    Sheet copySheet = srcWorkbook.cloneSheet(
                            srcWorkbook.getSheetIndex(formSheet)
                    );
                    srcWorkbook.setSheetName(
                            srcWorkbook.getSheetIndex(copySheet),
                            rowDto.getMon()+"_"+rowDto.getTeamName()+"_"+rowDto.getTerminalCode()
                    );
                    //제목
                    Row row = copySheet.getRow(1);
                    Cell cell = row.getCell(0);
                    if(type.equals("Y")){
                        cell.setCellValue("AACT "+rowDto.getYyyy()+"년 "+rowDto.getMon()+"월 "+rowDto.getTeamName()+" 확정 SKD");
                    }else{
                        cell.setCellValue("AACT "+rowDto.getYyyy()+"년 "+rowDto.getMon()+"월 "+rowDto.getTeamName()+" 예정 SKD");
                    }


                    Cell cell1 = row.getCell(36);
                    cell1.setCellValue("팀 장");

                    CreationHelper helper = srcWorkbook.getCreationHelper();

                    // 반드시 copySheet 기준
                    Drawing<?> drawing = copySheet.createDrawingPatriarch();

                    // 담당 결재 이미지
                    if (rowDto.getCreateUser() != null && rowDto.getCreateUser().length > 0) {
                        int pictureIdx = srcWorkbook.addPicture(
                                rowDto.getCreateUser(),
                                Workbook.PICTURE_TYPE_PNG
                        );

                        ClientAnchor anchor = helper.createClientAnchor();

                        anchor.setCol1(34); // AI
                        anchor.setRow1(2);
                        anchor.setCol2(36); // AJ
                        anchor.setRow2(6);

                        anchor.setDx1(Units.toEMU(10));
                        anchor.setDy1(Units.toEMU(10));
                        anchor.setDx2(Units.toEMU(-10));
                        anchor.setDy2(Units.toEMU(-10));

                        drawing.createPicture(anchor, pictureIdx);
                    }

                    // 과장 결재 이미지
                    if (rowDto.getApproveUser() != null && rowDto.getApproveUser().length > 0) {
                        int pictureIdx = srcWorkbook.addPicture(
                                rowDto.getApproveUser(),
                                Workbook.PICTURE_TYPE_PNG
                        );

                        ClientAnchor anchor = helper.createClientAnchor();

                        anchor.setCol1(36); // AK
                        anchor.setRow1(2);
                        anchor.setCol2(38); // AL
                        anchor.setRow2(6);

                        anchor.setDx1(Units.toEMU(10));
                        anchor.setDy1(Units.toEMU(10));
                        anchor.setDx2(Units.toEMU(-10));
                        anchor.setDy2(Units.toEMU(-10));

                        drawing.createPicture(anchor, pictureIdx);
                    }
                    //헤더
                    List<ExcelDTO.SchHeaderDTO> filterHeader = dto.getHeaders().stream().filter((v)->v.getMon().equals(rowDto.getMon()))
                            .sorted(Comparator.comparingInt(v -> Integer.parseInt(v.getDay()))).toList();
                    long holiCount = dto.getHeaders().stream().filter(v->!v.getHolidayCode().isEmpty()).count();

                    row = copySheet.getRow(6);
                    cell = row.getCell(35);
                    cell.setCellValue("휴무일 : "+holiCount);

                    int idx = 2;
                    for(ExcelDTO.SchHeaderDTO rowHeader:filterHeader){
                        row = copySheet.getRow(8);
                        cell = row.getCell(idx);
                        cell.setCellValue(rowHeader.getDay());
                        row = copySheet.getRow(9);
                        cell = row.getCell(idx);
                        cell.setCellValue(rowHeader.getDayName());
                        idx++;
                    }

                    if(rowDto.getRows() == null||rowDto.getRows().isEmpty()){
                        throw new BizException("getWorkM010_008", "데이터가 없습니다.");
                    }
                    int templateRowIdx = 10;
                    int selectIdx = 10;
                    for(int i = 0;i<rowDto.getRows().size();i++){
                        if(i!= 0){
                            copyRowStyle(copySheet, templateRowIdx, selectIdx);
                        }

                        //순번 및 이름
                        row = copySheet.getRow(selectIdx);
                        //순번
                        cell = row.getCell(0);
                        cell.setCellValue(i+1);
                        cell = row.getCell(1);
                        cell.setCellValue(rowDto.getRows().get(i).getUserName());

                        //셀 첫날 인덱스로 치환
                        List<ExcelDTO.SchCellDTO> dayList = rowDto.getRows().get(i).getCells();
                        if(dayList == null||dayList.isEmpty()){
                            //패스
                        }else{
                            for(ExcelDTO.SchCellDTO cellRow:dayList){
                                cell = row.getCell(Util.getInteger(cellRow.getDay())+1);
                                cell.setCellValue(cellRow.getWorkTypeCode());
                            }
                        }

                        cell = row.getCell(34);
                        cell.setCellValue(holiCount);
                        cell = row.getCell(35);
                        cell.setCellValue(rowDto.getRows().get(i).getUseHoliDay());
                        cell = row.getCell(36);
                        cell.setCellValue(rowDto.getRows().get(i).getUseAnn());
                        
                        //행증가
                        selectIdx++;

                    }

                }

                for (int i = 0; i < srcWorkbook.getNumberOfSheets(); i++) {
                    String name = srcWorkbook.getSheetName(i);

                    if (!"APPROVAL".equals(name) && !"FORM".equals(name)) {
                        srcWorkbook.setActiveSheet(i);
                        srcWorkbook.setSelectedTab(i);
                        break;
                    }
                }


                int sheetAppr = srcWorkbook.getSheetIndex("APPROVAL");

                srcWorkbook.setSheetHidden(sheetAppr,true);
                int sheetForm = srcWorkbook.getSheetIndex("FORM");

                srcWorkbook.setSheetHidden(sheetForm,true);


                srcWorkbook.write(bos);

                ret = ResponseDTO.<byte[]>builder().errFlag("N").errMsg("스케줄표 다운완료").data(bos.toByteArray()).build();
            }catch (IOException ex){
                throw new BizException("getWorkSch",ex.getMessage());
            }


            return ret;
        });
    }

    public ResponseDTO<?> getWorkTime(String teamCode,String terminalCode,String toDate,String fromDate,String date,String userName) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        DateTimeFormatter inputFormat =
                DateTimeFormatter.ofPattern("yyyyMMdd");

        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter outputFormat2 =
                DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return execute(repo, () -> {
            DbDto dbRet = null;
            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/TIME_WORK_FORM.xlsx");
            if (ret.getErrFlag().equals("Y")) {
                throw new BizException("getWorkTime", ret.getErrMsg());
            }

            dbRet = repo.getWorkM010_009(toDate,
                    fromDate,date, teamCode, terminalCode, userName,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("getWorkM010_009", dbRet.getErrMsg());
            }
            List<ExcelDTO.DailyDTO> convData  = convDailyData(dbRet,date.isEmpty());

            ResponseDTO<List<Map<String, Object>>> res = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRDOC")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(res.getErrFlag().equals("Y")){
                throw new BizException("getWorkTime", res.getErrMsg());
            }



            try(Workbook srcWorkbook = WorkbookFactory.create(new ByteArrayInputStream(ret.getData()));
                ByteArrayOutputStream bos = new ByteArrayOutputStream()){

                for(ExcelDTO.DailyDTO dto:convData){
                    List<Map<String,Object>> resFilter = res.getData().stream()
                            .filter(v->Util.getStrChk(v.get("CODE_CODE")).contains(dto.getTeamCode()))
                            .toList();

                    if(resFilter.isEmpty()){
                        throw new BizException("getWorkTime", "서류 셋팅이 없습니다.");
                    }
                    Map<String,Object> upLine = resFilter.stream().filter(v->v.get("VALUE1_CHAR").equals("FORM1")).findFirst().orElse(null);
                    if(upLine==null){
                        throw new BizException("getWorkTime", dto.getTeamName()+" 상단 결제 셋팅이 없습니다.");
                    }
                    Map<String,Object> downLine = resFilter.stream().filter(v->v.get("VALUE1_CHAR").equals("FORM2")).findFirst().orElse(null);
                    if(downLine==null){
                        throw new BizException("getWorkTime", dto.getTeamName()+" 하단 결제 셋팅이 없습니다.");
                    }
                    Map<String,Object> downName = resFilter.stream().filter(v->v.get("VALUE1_CHAR").equals("FORM3")).findFirst().orElse(null);
                    if(downName==null){
                        throw new BizException("getWorkTime", dto.getTeamName()+" 하단 결제 이름 셋팅이 없습니다.");
                    }

                    int upCount = 0;
                    for(Map.Entry<String, Object> entry : upLine.entrySet()){
                        if(entry.getKey().contains("CHAR")&&!Util.getStrChk(entry.getValue()).isEmpty()&&!entry.getKey().contains("VALUE1")){
                            upCount++;
                        }
                    }
                    int downCount = 0;
                    for(Map.Entry<String, Object> entry : downLine.entrySet()){
                        if(entry.getKey().contains("CHAR")&&!Util.getStrChk(entry.getValue()).isEmpty()&&!entry.getKey().contains("VALUE1")){
                            downCount++;
                        }
                    }


                    Sheet formSheet = srcWorkbook.getSheet("FORM"+upCount);
                    if(formSheet == null){
                        throw new BizException("getWorkTime", "FORM"+upCount+"시트가 없습니다.");
                    }
                    Sheet copySheet = srcWorkbook.cloneSheet(
                            srcWorkbook.getSheetIndex(formSheet)
                    );
                    srcWorkbook.setSheetName(
                            srcWorkbook.getSheetIndex(copySheet),
                            dto.getDate()+"_"+dto.getTeamName()+"_"+dto.getTerminalCode()
                    );

                    //제목
                    Row row = copySheet.getRow(0);
                    Cell cell = row.getCell(0);
                    cell.setCellValue("시간 외 근무수당 신청서( "+dto.getTeamName()+" )");
                    row = copySheet.getRow(1);
                    //결재라인 수정
                    if(upCount==2){

                        for(int i = 1;i<=upCount;i++){
                            cell = row.getCell(9+i);
                            cell.setCellValue(Util.getStrChk(upLine.get("VALUE"+(i+1)+"_CHAR")));
                        }
                    }else if(upCount == 3){
                        for(int i = 1;i<=upCount;i++){
                            if(i==1){
                                cell = row.getCell(10+i);
                            }else{
                                cell = row.getCell(11+i);
                            }
                            cell.setCellValue(Util.getStrChk(upLine.get("VALUE"+(i+1)+"_CHAR")));
                        }
                    }else{
                        throw new BizException("getWorkTime", upCount+"개수의 결제라인은 없습니다.");
                    }

                    row = copySheet.getRow(4);
                    cell = row.getCell(0);
                    if(date.isEmpty()){
                        LocalDate localDate = LocalDate.parse(dto.getDate(), inputFormat);
                        cell.setCellValue(localDate.format(outputFormat));
                    }else{
                        YearMonth ym = YearMonth.parse(
                                dto.getDate(),
                                DateTimeFormatter.ofPattern("yyyyMM")
                        );
                        cell.setCellValue(
                                ym.format(DateTimeFormatter.ofPattern("yyyy년 MM월"))
                        );
                    }

                    if(dto.getRowList()==null||dto.getRowList().isEmpty()){
                        throw new BizException("getWorkTime", "데이터가 없습니다.");
                    }

                    //엑셀 만들기
                    int selectIdx = 6;

                    int dataCount = dto.getRowList().size();

                    if (dataCount > 1) {
                        copySheet.shiftRows(
                                selectIdx + 1,
                                copySheet.getLastRowNum(),
                                dataCount - 1,
                                true,
                                false
                        );
                    }


                    for(int i = 0;i<dto.getRowList().size();i++){
                        int rowIdx = selectIdx + i;
                        if(i != 0){
                            row = copySheet.createRow(rowIdx);
                            copyRowStyleFast(copySheet,selectIdx, rowIdx);

                            if(upCount==2){
                                // 휴일연장 H:I
                                copySheet.addMergedRegion(
                                        new CellRangeAddress(
                                                rowIdx,
                                                rowIdx,
                                                8,
                                                9
                                        )
                                );

                                // 근무사유 J:K
                                copySheet.addMergedRegion(
                                        new CellRangeAddress(
                                                rowIdx,
                                                rowIdx,
                                                10,
                                                12
                                        )
                                );
                            }else{
                                // 휴일연장 H:I
                                copySheet.addMergedRegion(
                                        new CellRangeAddress(
                                                rowIdx,
                                                rowIdx,
                                                8,
                                                10
                                        )
                                );

                                // 근무사유 J:K
                                copySheet.addMergedRegion(
                                        new CellRangeAddress(
                                                rowIdx,
                                                rowIdx,
                                                11,
                                                13
                                        )
                                );
                            }
                        }else{
                            row = copySheet.getRow(rowIdx);
                        }

                        cell = row.getCell(
                                0,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(i + 1);

                        cell = row.getCell(
                                1,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );

                        LocalDate tmpDate = LocalDate.parse(
                                dto.getRowList().get(i).getDate(),
                                inputFormat
                        );

                        cell.setCellValue(
                                tmpDate.format(outputFormat2)
                        );

                        cell = row.getCell(
                                2,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                dto.getRowList().get(i).getUserName()
                        );

                        cell = row.getCell(
                                3,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                dto.getRowList().get(i).getWorkTypeName()
                        );

                        cell = row.getCell(
                                4,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                Util.formatTime_HHmm(
                                        dto.getRowList().get(i).getReqStartTime()
                                )
                                        + " ~ "
                                        + Util.formatTime_HHmm(
                                        dto.getRowList().get(i).getReqEndTime()
                                )
                        );

                        cell = row.getCell(
                                5,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                dto.getRowList().get(i).getAddHour()
                        );

                        cell = row.getCell(
                                6,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                dto.getRowList().get(i).getNightHour()
                        );

                        cell = row.getCell(
                                7,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                dto.getRowList().get(i).getHoliHour()
                        );

                        // 휴일연장 병합영역의 첫 번째 셀
                        cell = row.getCell(
                                8,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                dto.getRowList().get(i).getHoliAddHour()
                        );

                        // 근무사유는 병합영역 첫 번째 셀에 입력해야 함
                        int remarkCellIdx = upCount == 2 ? 10 : 11;

                        cell = row.getCell(
                                remarkCellIdx,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue(
                                dto.getRowList().get(i).getRemark()
                        );

                    }

                    row = copySheet.getRow(selectIdx+dataCount);
                    cell= row.getCell(5,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getSumAddHour());
                    cell= row.getCell(6,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getSumNightHour());
                    cell= row.getCell(7,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getSumHoliHour());

                    CellStyle rightStyle = srcWorkbook.createCellStyle();
                    rightStyle.setAlignment(HorizontalAlignment.RIGHT);
                    rightStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    //팀장 셋팅
                    row = copySheet.getRow(selectIdx+dataCount + 2);
                    cell = row.getCell(8,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("팀 장 :");

                    cell = row.getCell(10,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getApproveName());

                    cell = row.getCell(11,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("(인)");
                    cell.setCellStyle(rightStyle);

                    byte[] signBytes = dto.getApproveSign();

                    if (signBytes != null && signBytes.length > 0) {
                        int pictureIdx = srcWorkbook.addPicture(
                                signBytes,
                                Workbook.PICTURE_TYPE_PNG
                        );

                        CreationHelper helper = srcWorkbook.getCreationHelper();
                        Drawing<?> drawing = copySheet.createDrawingPatriarch();

                        ClientAnchor anchor = helper.createClientAnchor();

                        // K열 위쪽에 이미지 배치
                        anchor.setCol1(11); // K
                        anchor.setRow1(selectIdx+dataCount+2);  // 9행
                        anchor.setCol2(12); // L
                        anchor.setRow2(selectIdx+dataCount+3); // 11행

                        anchor.setDx1(Units.toEMU(10));
                        anchor.setDy1(Units.toEMU(2));
                        anchor.setDx2(Units.toEMU(0));
                        anchor.setDy2(Units.toEMU(-2));

                        drawing.createPicture(anchor, pictureIdx);
                    }

                    for(int i = 1;i<=downCount;i++){
                        row = copySheet.getRow(selectIdx+dataCount + 2+i);
                        cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(Util.getStrChk(downLine.get("VALUE"+(i+1)+"_CHAR"))+" :");

                        cell = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(Util.getStrChk(downName.get("VALUE"+(i+1)+"_CHAR")));
                        cell = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue("(인)");
                        cell.setCellStyle(rightStyle);
                    }
                }

                boolean activeSet = false;

                for (int i = 0; i < srcWorkbook.getNumberOfSheets(); i++) {
                    String name = srcWorkbook.getSheetName(i);

                    if ("APPROVAL".equals(name) || name.startsWith("FORM")) {
                        srcWorkbook.setSheetHidden(i, true);
                    } else if (!activeSet) {
                        srcWorkbook.setActiveSheet(i);
                        srcWorkbook.setSelectedTab(i);
                        activeSet = true;
                    }
                }


                srcWorkbook.write(bos);

                ret = ResponseDTO.<byte[]>builder().errFlag("N").errMsg("시간 외 근무 다운완료").data(bos.toByteArray()).build();
            }catch (IOException ex){
                throw new BizException("getWorkTime",ex.getMessage());
            }

            return ret;
        });
    }

    public ResponseDTO<?> getWorkDetail(String teamCode,String terminalCode,String toDate,String fromDate,String date){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo,()->{
            DbDto dbRet = null;
            DbDto dbRetSum = null;
            List<ExcelDTO.CoverDTO> covDto = null;
            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/WORK_FORM_DETAIL.xlsx");
            if (ret.getErrFlag().equals("Y")) {
                throw new BizException("getWorkDetail", ret.getErrMsg());
            }

            ResponseDTO<List<Map<String, Object>>> res = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRDOC")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(res.getErrFlag().equals("Y")){
                throw new BizException("getWorkDetail", res.getErrMsg());
            }

            ResponseDTO<List<Map<String, Object>>> res2 = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRDPT")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(res2.getErrFlag().equals("Y")){
                throw new BizException("getWorkDetail", res2.getErrMsg());
            }

            dbRet = repo.getWorkM010_010(toDate,
                    fromDate,date, teamCode, terminalCode,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("getWorkDetail", dbRet.getErrMsg());
            }

            if(!date.isEmpty()){

                YearMonth ym = YearMonth.parse(
                        date,
                        DateTimeFormatter.ofPattern("yyyyMM")
                );

                String prevMonth = ym.minusMonths(1)
                        .format(DateTimeFormatter.ofPattern("yyyyMM"));

                StringBuilder sql = new StringBuilder();

                sql.append("SELECT YEAR||MON YYYYMM, ");
                sql.append("       SUM(ADD_WORK_HOUR) SUM_ADD_HOUR, ");
                sql.append("       SUM(NIGHT_WORK_HOUR) SUM_NIGHT_HOUR, ");
                sql.append("       SUM(HOLIDAY_WORK_HOUR + HOLIDAY_ADD_HOUR) SUM_HOLIDAY_HOUR, ");
                sql.append("       TEAM_CODE, ");
                sql.append("       SYS_FUNCTION.FCM_GET_CODE_NAME_BY_AK1('HRPAT', TEAM_CODE, 'KOR') TEAM_NAME ");
                sql.append("FROM ( ");
                sql.append("    SELECT T.*, ");
                sql.append("           ROW_NUMBER() OVER ( ");
                sql.append("               PARTITION BY YEAR, MON, USER_SID, DAY, SEQ ");
                sql.append("               ORDER BY LOG_SEQ DESC ");
                sql.append("           ) RN ");
                sql.append("    FROM THR_OT_DETAIL_LOG T ");
                sql.append("    WHERE YEAR||MON IN ('").append(date).append("','").append(prevMonth).append("') ");
                sql.append("      AND REQ_FLAG = 'C' ");
                sql.append("      AND USABLE_FLAG = 'Y' ");
                sql.append("      AND REQ_START_TIME IS NOT NULL ");
                sql.append("      AND REQ_END_TIME IS NOT NULL ");
                sql.append(") ");
                sql.append("WHERE RN = 1 ");
                sql.append("GROUP BY TEAM_CODE, YEAR, MON");

                dbRetSum = repo.callSql(sql.toString());
                if (dbRetSum.getErrFlag().equals("Y")) {
                    throw new BizException("getWorkDetail", dbRetSum.getErrMsg());
                }

                if(dbRetSum.getResult().get(0).isEmpty()){
                    throw new BizException("getWorkDetail", "표지 정보가 없습니다.");
                }
                covDto = convDetailCoverData(date,dbRetSum.getResult().get(0),res2.getData(),res.getData());
            }

            List<ExcelDTO.DetailDTO> convData = convDetailData(dbRet);

            try(Workbook srcWorkbook = WorkbookFactory.create(new ByteArrayInputStream(ret.getData()));
                ByteArrayOutputStream bos = new ByteArrayOutputStream()){

                if(covDto!= null){
                    Row row = null;
                    Cell cell = null;
                    YearMonth ym = YearMonth.parse(
                            date,
                            DateTimeFormatter.ofPattern("yyyyMM")
                    );

                    String prevMonth = ym.minusMonths(1)
                            .format(DateTimeFormatter.ofPattern("yyyyMM"));
                    Integer beforeMon = Util.getInteger(prevMonth.substring(4,6));
                    Integer mon = Util.getInteger(date.substring(4,6));
                    for(ExcelDTO.CoverDTO dto:covDto){
                        int lineLength = dto.getGroupLine().length;
                        Sheet formSheet = srcWorkbook.getSheet("FORM_"+lineLength);
                        if(formSheet == null){
                            throw new BizException("getWorkDetail", "Templete FORM_"+lineLength+" 없음.");
                        }
                        Sheet copySheet = srcWorkbook.cloneSheet(
                                srcWorkbook.getSheetIndex(formSheet)
                        );
                        srcWorkbook.setSheetName(
                                srcWorkbook.getSheetIndex(copySheet),
                                dto.getGroupName()+"표지"
                        );

                        row = copySheet.getRow(0);
                        cell = row.getCell(0);
                        String value = cell.getStringCellValue();
                        value = value.replace("{$1}",mon.toString());
                        cell.setCellValue(value);

                        row = copySheet.getRow(9);
                        cell = row.getCell(1);
                        value = cell.getStringCellValue();
                        value = value.replace("{$1}",beforeMon.toString());
                        cell.setCellValue(value);

                        cell = row.getCell(4);
                        value = cell.getStringCellValue();
                        value = value.replace("{$1}",mon.toString());
                        cell.setCellValue(value);

                        row = copySheet.getRow(19);
                        cell = row.getCell(0);
                        value = cell.getStringCellValue();
                        value = value.replace("{$1}",mon.toString());
                        cell.setCellValue(value);

                        LocalDateTime now = LocalDateTime.now();

                        String currentDate = now.format(
                                DateTimeFormatter.ofPattern("yyyy. MM. dd")
                        );
                        row = copySheet.getRow(32);
                        cell = row.getCell(0);
                        cell.setCellValue(currentDate);


                        row = copySheet.getRow(3);

                        if(lineLength == 3){
                            for(int i =0;i<lineLength;i++){
                                cell = row.getCell(12+i);
                                cell.setCellValue(dto.getGroupLine()[i]);
                            }
                        }else if(lineLength == 4){
                            for(int i =0;i<lineLength;i++){
                                if(i==0){
                                    cell = row.getCell(11+i);
                                }else{
                                    cell = row.getCell(12+i);
                                }
                                cell.setCellValue(dto.getGroupLine()[i]);
                            }
                        }else {
                            throw new BizException("getWorkDetail", "결재라인 : "+lineLength+" 없음.");
                        }
                        double totalBeforeAddHour = 0;
                        double totalBeforeNightHour = 0;
                        double totalBeforeHoliHour = 0;

                        double totalNowAddHour = 0;
                        double totalNowNightHour = 0;
                        double totalNowHoliHour = 0;

                        double totalCreAddHour = 0;
                        double totalCreNightHour = 0;
                        double totalCreHoliHour = 0;

                        int start = 11;

                        int dataCount = dto.getRowList().size();

                        if (dataCount > 1) {
                            copySheet.shiftRows(
                                    start + 1,
                                    copySheet.getLastRowNum(),
                                    dataCount - 1,
                                    true,
                                    false
                            );
                        }

                        for(int i = 0;i<dto.getRowList().size();i++){
                            int rowIdx = start + i;
                            if(i!=0){
                                row = copySheet.createRow(rowIdx);
                                copyRowStyleFast(copySheet, start, rowIdx);

                                if(lineLength==3){
                                    // 휴일
                                    copySheet.addMergedRegion(
                                            new CellRangeAddress(
                                                    rowIdx,
                                                    rowIdx,
                                                    9,
                                                    10
                                            )
                                    );

                                    // 증감사유
                                    copySheet.addMergedRegion(
                                            new CellRangeAddress(
                                                    rowIdx,
                                                    rowIdx,
                                                    11,
                                                    14
                                            )
                                    );
                                }else{
                                    // 휴일
                                    copySheet.addMergedRegion(
                                            new CellRangeAddress(
                                                    rowIdx,
                                                    rowIdx,
                                                    9,
                                                    11
                                            )
                                    );

                                    // 증감사유
                                    copySheet.addMergedRegion(
                                            new CellRangeAddress(
                                                    rowIdx,
                                                    rowIdx,
                                                    12,
                                                    15
                                            )
                                    );
                                }
                            }else{
                                row = copySheet.getRow(rowIdx);
                            }
                            cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getDeptName());
                            cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getBeforeAddHour());
                            totalBeforeAddHour += dto.getRowList().get(i).getBeforeAddHour();
                            cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getBeforeNightHour());
                            totalBeforeNightHour += dto.getRowList().get(i).getBeforeNightHour();
                            cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getBeforeHoliHour());
                            totalBeforeHoliHour += dto.getRowList().get(i).getBeforeHoliHour();
                            cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getNowAddHour());
                            totalNowAddHour += dto.getRowList().get(i).getNowAddHour();
                            cell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getNowNightHour());
                            totalNowNightHour += dto.getRowList().get(i).getNowNightHour();
                            cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getNowHoliHour());
                            totalNowHoliHour += dto.getRowList().get(i).getNowHoliHour();
                            cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getNowAddHour()-dto.getRowList().get(i).getBeforeAddHour());
                            totalCreAddHour += dto.getRowList().get(i).getNowAddHour()-dto.getRowList().get(i).getBeforeAddHour();
                            cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getNowNightHour()-dto.getRowList().get(i).getBeforeNightHour());
                            totalCreNightHour += dto.getRowList().get(i).getNowNightHour()-dto.getRowList().get(i).getBeforeNightHour();
                            cell = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getRowList().get(i).getNowHoliHour()-dto.getRowList().get(i).getBeforeHoliHour());
                            totalCreHoliHour += dto.getRowList().get(i).getNowHoliHour()-dto.getRowList().get(i).getBeforeHoliHour();
                        }

                        row = copySheet.getRow(start+dataCount);
                        cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalBeforeAddHour);
                        cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalBeforeNightHour);
                        cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalBeforeHoliHour);

                        cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalNowAddHour);
                        cell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalNowNightHour);
                        cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalNowHoliHour);

                        cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalCreAddHour);
                        cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalCreNightHour);
                        cell = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(totalCreHoliHour);
                    }
                }

                for(ExcelDTO.DetailDTO dto:convData){
                    Sheet formSheet = srcWorkbook.getSheet("FORM_DETAIL");
                    Sheet copySheet = srcWorkbook.cloneSheet(
                            srcWorkbook.getSheetIndex(formSheet)
                    );
                    srcWorkbook.setSheetName(
                            srcWorkbook.getSheetIndex(copySheet),
                            dto.getTeamName()+"_"+dto.getTerminalCode()+"_"+dto.getMon()+"월"
                    );

                    Row row = copySheet.getRow(0);
                    Cell cell = row.getCell(0);
                    cell.setCellValue(dto.getMon()+"월 시간 외 근로시간 개인별 세부내역 ("+dto.getTeamName()+"_"+dto.getTerminalCode()+")");

                    int rowSize = dto.getRowList().size();

                    int cellSize = dto.getRowList().stream()
                            .mapToInt(v -> v.getCellList() == null ? 0 : v.getCellList().size())
                            .sum();

                    int selectIdx = 4;
                    int sumIdx = rowSize+cellSize +3;

                    if(rowSize+cellSize > 2){
                        copySheet.shiftRows(
                                selectIdx + 1,
                                copySheet.getLastRowNum(),
                                rowSize+cellSize - 2,
                                true,
                                false
                        );
                    }

                    row = copySheet.getRow(selectIdx);

                    cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getTeamName());
                    int sumRowIdx = selectIdx;
                    for(int i = 0;i<dto.getRowList().size();i++){

                        ExcelDTO.DetailRowDTO rowList = dto.getRowList().get(i);

                        for(int j = 0;j<rowList.getCellList().size();j++){

                            int rowIdx = sumRowIdx;
                            if(sumRowIdx==selectIdx){
                                row = copySheet.getRow(rowIdx);
                            }else{
                                row = copySheet.createRow(rowIdx);
                                copyRowStyleFast(copySheet,selectIdx, rowIdx);
                            }

                            if(j==0){
                                cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(rowList.getUserName());
                                copySheet.addMergedRegion(
                                        new CellRangeAddress(
                                                sumRowIdx,
                                                sumRowIdx+rowList.getCellList().size()-1,
                                                1, // B열
                                                1
                                        )
                                );
                            }

                            ExcelDTO.DetailCellDTO cellRow = rowList.getCellList().get(j);

                            String day = String.format("%02d", Integer.parseInt(cellRow.getDay()));
                            cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(dto.getYyyy()+"-"+dto.getMon()+"-"+day+" "+cellRow.getWeekDay());
                            if(cellRow.getSchStart().isEmpty()&&cellRow.getSchEnd().isEmpty()){
                                cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(cellRow.getWorkTypeName());
                                copySheet.addMergedRegion(
                                        new CellRangeAddress(
                                                rowIdx,
                                                rowIdx,
                                                3, // B열
                                                4
                                        )
                                );
                            }else{
                                cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(Util.formatTime_HHmm(cellRow.getSchStart()));
                                cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(Util.formatTime_HHmm(cellRow.getSchEnd()));
                            }

                            cell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(Util.formatTime_HHmm(cellRow.getCapsStart()));
                            cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(Util.formatTime_HHmm(cellRow.getCapsEnd()));
                            cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(cellRow.getAddHour());
                            cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(cellRow.getNightHour());
                            cell = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(cellRow.getHoliHour());
                            cell = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(cellRow.getHoliAddHour());

                            sumRowIdx++;
                        }

                        if(i==dto.getRowList().size()-1){
                            row = copySheet.getRow(sumRowIdx);
                        }else{
                            row = copySheet.createRow(sumRowIdx);
                            copyRowStyleFast(copySheet,sumIdx, sumRowIdx);
                            copySheet.addMergedRegion(
                                    new CellRangeAddress(
                                            sumRowIdx,
                                            sumRowIdx,
                                            1, // B열
                                            6
                                    )
                            );
                        }



                        cell = row.getCell(
                                1,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        );
                        cell.setCellValue("소계");
                        cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(rowList.getSumAddHour());
                        cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(rowList.getSumNightHour());
                        cell = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(rowList.getSumHoliHour());
                        cell = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(rowList.getSumHoliAddHour());
                        sumRowIdx++;

                    }

                    copySheet.addMergedRegion(
                            new CellRangeAddress(
                                    selectIdx,
                                    rowSize+cellSize +3,
                                    0,
                                    0
                            )
                    );

                    row = copySheet.getRow(sumRowIdx);
                    cell = row.getCell(0);
                    cell.setCellValue(dto.getTeamName()+" 합계");
                    cell = row.getCell(7);
                    cell.setCellValue(dto.getSumAddHour());
                    cell = row.getCell(8);
                    cell.setCellValue(dto.getSumNightHour());
                    cell = row.getCell(9);
                    cell.setCellValue(dto.getSumHoliHour());
                    cell = row.getCell(10);
                    cell.setCellValue(dto.getSumHoliAddHour());

                }
                boolean activeSet = false;

                for (int i = 0; i < srcWorkbook.getNumberOfSheets(); i++) {
                    String name = srcWorkbook.getSheetName(i);

                    if ("APPROVAL".equals(name) || name.startsWith("FORM")) {
                        srcWorkbook.setSheetHidden(i, true);
                    } else if (!activeSet) {
                        srcWorkbook.setActiveSheet(i);
                        srcWorkbook.setSelectedTab(i);
                        activeSet = true;
                    }
                }
                srcWorkbook.write(bos);
                ret = ResponseDTO.<byte[]>builder().errFlag("N").errMsg("세부내역 다운완료").data(bos.toByteArray()).build();
            }catch (IOException ex){
                throw new BizException("getWorkDetail",ex.getMessage());
            }

            return ret;
        });

    }

    private CellStyle getCellStyle_Line(Workbook workbook,boolean top,boolean bottom,boolean left,boolean right,boolean fontBold){
        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillPattern(FillPatternType.NO_FILL);

        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        headerStyle.setBorderTop(top ? BorderStyle.MEDIUM:BorderStyle.THIN);
        headerStyle.setBorderBottom(bottom ? BorderStyle.MEDIUM:BorderStyle.THIN);
        headerStyle.setBorderLeft(left ? BorderStyle.MEDIUM:BorderStyle.THIN);
        headerStyle.setBorderRight(right ? BorderStyle.MEDIUM:BorderStyle.THIN);

        if(fontBold){
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
        }
        return headerStyle;
    }

    private CellStyle getCellStyle_Align(Workbook workbook,boolean fontBold){
        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillPattern(FillPatternType.NO_FILL);

        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        if(fontBold){
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
        }
        return headerStyle;
    }

    private List<ExcelDTO.CoverDTO> convDetailCoverData(String date,List<Map<String,DbTypeDTO>> dbDto,List<Map<String,Object>> hrdptDto,List<Map<String,Object>> hrdocDto){

        return execute(()->{
            List<ExcelDTO.CoverDTO> ret = new ArrayList<>();
            for(Map<String,DbTypeDTO> row : dbDto){

                Map<String,Object> findHrdoc = hrdocDto.stream()
                        .filter(v->Util.getStrChk(v.get("CODE_CODE")).startsWith(Util.getStrChk(row.get("TEAM_CODE").getObj()))
                                &&Util.getStrChk(v.get("VALUE1_CHAR")).equals("COV3"))
                        .findFirst().orElse(null);
                if(findHrdoc==null){
                    throw new BizException("convDetailCoverData", "표지 그룹셋팅이 없습니다.");
                }
                Map<String,Object> findHrdoc2 = hrdocDto.stream()
                        .filter(v->Util.getStrChk(v.get("CODE_CODE")).startsWith(Util.getStrChk(row.get("TEAM_CODE").getObj()))
                                &&Util.getStrChk(v.get("VALUE1_CHAR")).equals("COV1"))
                        .findFirst().orElse(null);
                if(findHrdoc2==null){
                    throw new BizException("convDetailCoverData", "표지 부서셋팅이 없습니다.");
                }

                ExcelDTO.CoverDTO findCover = ret.stream()
                        .filter(v->v.getGroupCode().equals(Util.getStrChk(findHrdoc.get("VALUE2_CHAR"))))
                        .findFirst().orElse(null);

                if(findCover==null){
                    Map<String,Object> findHrdpt = hrdptDto.stream()
                            .filter(v->Util.getStrChk(v.get("CODE_CODE")).equals(Util.getStrChk(findHrdoc.get("VALUE2_CHAR"))))
                            .findFirst().orElse(null);

                    if(findHrdpt==null){
                        throw new BizException("convDetailCoverData", "표지 그룹셋팅이 없습니다.");
                    }
                    findCover = new ExcelDTO.CoverDTO();
                    findCover.setGroupCode(Util.getStrChk(findHrdoc.get("VALUE2_CHAR")));
                    findCover.setGroupName(Util.getStrChk(findHrdpt.get("CODE_NAME")));
                    findCover.setGroupLine(Util.getStrChk(findHrdpt.get("VALUE1_CHAR")).split(";"));
                    findCover.setRowList(new ArrayList<>());
                    ret.add(findCover);
                }

                ExcelDTO.CoverRowDTO findRowCover = findCover.getRowList().stream()
                        .filter(v->v.getDeptCode().equals(Util.getStrChk(findHrdoc2.get("VALUE2_CHAR"))))
                        .findFirst().orElse(null);

                if(findRowCover==null){
                    Map<String,Object> findHrdpt = hrdptDto.stream()
                            .filter(v->Util.getStrChk(v.get("CODE_CODE")).equals(Util.getStrChk(findHrdoc2.get("VALUE2_CHAR"))))
                            .findFirst().orElse(null);

                    if(findHrdpt==null){
                        throw new BizException("convDetailCoverData", "표지 부서셋팅이 없습니다.");
                    }
                    findRowCover = new ExcelDTO.CoverRowDTO();
                    findRowCover.setDeptCode(Util.getStrChk(findHrdoc2.get("VALUE2_CHAR")));
                    findRowCover.setDeptName(Util.getStrChk(findHrdpt.get("CODE_NAME")));
                    findRowCover.setNowAddHour(0);
                    findRowCover.setNowNightHour(0);
                    findRowCover.setNowHoliHour(0);
                    findRowCover.setBeforeAddHour(0);
                    findRowCover.setBeforeNightHour(0);
                    findRowCover.setBeforeHoliHour(0);

                    findCover.getRowList().add(findRowCover);
                }

                if(date.equals(Util.getStrChk(row.get("YYYYMM").getObj()))){
                    findRowCover.setNowAddHour(findRowCover.getNowAddHour()+Util.getDouble(row.get("SUM_ADD_HOUR").getObj()));
                    findRowCover.setNowNightHour(findRowCover.getNowNightHour()+Util.getDouble(row.get("SUM_NIGHT_HOUR").getObj()));
                    findRowCover.setNowHoliHour(findRowCover.getNowHoliHour()+Util.getDouble(row.get("SUM_HOLIDAY_HOUR").getObj()));
                }else{
                    findRowCover.setBeforeAddHour(findRowCover.getBeforeAddHour()+Util.getDouble(row.get("SUM_ADD_HOUR").getObj()));
                    findRowCover.setBeforeNightHour(findRowCover.getBeforeNightHour()+Util.getDouble(row.get("SUM_NIGHT_HOUR").getObj()));
                    findRowCover.setBeforeHoliHour(findRowCover.getBeforeHoliHour()+Util.getDouble(row.get("SUM_HOLIDAY_HOUR").getObj()));
                }




            }

            return ret;
        });

    }

    private ExcelDTO.SchDTO convExSchData(DbDto dto,String date,String terminalCode,String teamCode,String teamName,String holiDay){
        return execute(()->{
            ExcelDTO.SchDTO ret = new ExcelDTO.SchDTO();
            List<Map<String, DbTypeDTO>> tmpTable = dto.getResult().get(0);
            if(tmpTable.isEmpty()){
                throw new BizException("convExSchData", "데이터가 없습니다.");
            }
            List<Map<String, DbTypeDTO>> tmpTable2 = dto.getResult().get(1);

            ret.setYyyy(date.substring(0,4));
            ret.setMon(date.substring(4,6));
            ret.setTerminalCode(terminalCode);
            ret.setTeamCode(teamCode);
            ret.setTeamName(teamName);
            ret.setHoliDay(holiDay);
            ret.setRows(new ArrayList<>());

            for(Map<String,DbTypeDTO> row : tmpTable){
                ExcelDTO.SchRowDTO rowTmp = new ExcelDTO.SchRowDTO();
                rowTmp.setUserSid(Util.getStrChk(row.get("USER_SID").getObj()));
                rowTmp.setUserName(Util.getStrChk(row.get("USER_NAME").getObj()));
                rowTmp.setUserId(Util.getStrChk(row.get("USER_ID").getObj()));
                rowTmp.setUseAnn(Util.getStrChk(row.get("ANN_DAY").getObj()));
                rowTmp.setUseHoliDay(Util.getStrChk(row.get("HOLIDAY_USE").getObj()));
                rowTmp.setCells(new ArrayList<>());
                ret.getRows().add(rowTmp);
            }

            for(Map<String,DbTypeDTO> row : tmpTable2){
                ExcelDTO.SchCellDTO rowTmp = new ExcelDTO.SchCellDTO();
                rowTmp.setDay(Util.getStrChk(row.get("DAY").getObj()));
                rowTmp.setWorkTypeCode(Util.getStrChk(row.get("WORK_TYPE_CODE").getObj()));
                rowTmp.setSeq(Util.getStrChk(row.get("SEQ").getObj()));
                rowTmp.setAddWorkHour(Util.getStrChk(row.get("ADD_WORK_HOUR").getObj()));
                rowTmp.setTmpTerminalCode(Util.getStrChk(row.get("WORK_TERMINAL_CODE").getObj()));
                ExcelDTO.SchRowDTO filterTmp = ret.getRows().stream().filter((v)->v.getUserSid().equals(Util.getStrChk(row.get("USER_SID").getObj())))
                        .findFirst().orElse(null);
                if(filterTmp != null){
                    filterTmp.getCells().add(rowTmp);
                }
            }
            return ret;
        });
    }

    private List<ExcelDTO.DetailDTO> convDetailData(DbDto dto){
        return execute(()->{
            List<ExcelDTO.DetailDTO> ret = new ArrayList<>();
            List<Map<String, DbTypeDTO>> tmpTable = dto.getResult().get(0);
            if(tmpTable.isEmpty()){
                throw new BizException("convDetailData", "데이터가 없습니다.");
            }

            for(Map<String,DbTypeDTO> row: tmpTable){
                ExcelDTO.DetailDTO filterDto = ret.stream().filter((v)->
                            v.getYyyy().equals(Util.getStrChk(row.get("YEAR").getObj()))
                        &&  v.getMon().equals(Util.getStrChk(row.get("MON").getObj()))
                        &&  v.getTeamCode().equals(Util.getStrChk(row.get("TEAM_CODE").getObj()))
                        &&  v.getTerminalCode().equals(Util.getStrChk(row.get("TERMINAL_CODE").getObj()))
                        ).findFirst().orElse(null);
                if(filterDto ==null){
                    filterDto = new ExcelDTO.DetailDTO();
                    filterDto.setYyyy(Util.getStrChk(row.get("YEAR").getObj()));
                    filterDto.setMon(Util.getStrChk(row.get("MON").getObj()));
                    filterDto.setTeamCode(Util.getStrChk(row.get("TEAM_CODE").getObj()));
                    filterDto.setTeamName(Util.getStrChk(row.get("TEAM_NAME").getObj()));
                    filterDto.setTerminalCode(Util.getStrChk(row.get("TERMINAL_CODE").getObj()));
                    filterDto.setRowList(new ArrayList<>());
                    ret.add(filterDto);
                }

                ExcelDTO.DetailRowDTO filterRowDto = filterDto.getRowList().stream().filter((v)->
                        v.getUserSid().equals(Util.getStrChk(row.get("USER_SID").getObj()))
                ).findFirst().orElse(null);

                if(filterRowDto == null){
                    filterRowDto  = new ExcelDTO.DetailRowDTO();
                    filterRowDto.setUserSid(Util.getStrChk(row.get("USER_SID").getObj()));
                    filterRowDto.setUserName(Util.getStrChk(row.get("USER_NAME").getObj()));
                    filterRowDto.setCellList(new ArrayList<>());
                    filterDto.getRowList().add(filterRowDto);
                }

                ExcelDTO.DetailCellDTO filterCellDto = filterRowDto.getCellList().stream().filter((v)->
                        v.getSeq().equals(Util.getStrChk(row.get("SEQ").getObj()))
                        && v.getDay().equals(Util.getStrChk(row.get("DAY").getObj()))
                ).findFirst().orElse(null);

                if(filterCellDto == null){
                    filterCellDto   =   new ExcelDTO.DetailCellDTO();
                    filterCellDto.setSeq(Util.getStrChk(row.get("SEQ").getObj()));
                    filterCellDto.setWeekDay(Util.getStrChk(row.get("WEEK_DAY").getObj()));
                    filterCellDto.setDay(Util.getStrChk(row.get("DAY").getObj()));
                    filterCellDto.setWorkTypeName(Util.getStrChk(row.get("CODE_NAME").getObj()));
                    filterCellDto.setSchStart(Util.getStrChk(row.get("REQ_START_TIME").getObj()));
                    filterCellDto.setSchEnd(Util.getStrChk(row.get("REQ_END_TIME").getObj()));
                    filterCellDto.setCapsStart(Util.getStrChk(row.get("CAPS_START_TIME").getObj()));
                    filterCellDto.setCapsEnd(Util.getStrChk(row.get("CAPS_END_TIME").getObj()));
                    filterCellDto.setAddHour(Util.getDouble(row.get("ADD_WORK_HOUR").getObj()));
                    filterCellDto.setNightHour(Util.getDouble(row.get("NIGHT_WORK_HOUR").getObj()));
                    filterCellDto.setHoliHour(Util.getDouble(row.get("HOLIDAY_WORK_HOUR").getObj()));
                    filterCellDto.setHoliAddHour(Util.getDouble(row.get("HOLIDAY_ADD_HOUR").getObj()));
                    filterRowDto.getCellList().add(filterCellDto);
                }








            }

            for(ExcelDTO.DetailDTO dtTmp:ret){
                for(ExcelDTO.DetailRowDTO rowTmp:dtTmp.getRowList()){
                    rowTmp.setSumAddHour(rowTmp.getCellList().stream().mapToDouble(ExcelDTO.DetailCellDTO::getAddHour).sum());
                    rowTmp.setSumNightHour(rowTmp.getCellList().stream().mapToDouble(ExcelDTO.DetailCellDTO::getNightHour).sum());
                    rowTmp.setSumHoliHour(rowTmp.getCellList().stream().mapToDouble(ExcelDTO.DetailCellDTO::getHoliHour).sum());
                    rowTmp.setSumHoliAddHour(rowTmp.getCellList().stream().mapToDouble(ExcelDTO.DetailCellDTO::getHoliAddHour).sum());
                }
                dtTmp.setSumAddHour(dtTmp.getRowList().stream().mapToDouble(ExcelDTO.DetailRowDTO::getSumAddHour).sum());
                dtTmp.setSumNightHour(dtTmp.getRowList().stream().mapToDouble(ExcelDTO.DetailRowDTO::getSumNightHour).sum());
                dtTmp.setSumHoliHour(dtTmp.getRowList().stream().mapToDouble(ExcelDTO.DetailRowDTO::getSumHoliHour).sum());
                dtTmp.setSumHoliAddHour(dtTmp.getRowList().stream().mapToDouble(ExcelDTO.DetailRowDTO::getSumHoliAddHour).sum());
            }
            return ret;
        });
    }

    private List<ExcelDTO.DailyDTO> convDailyData(DbDto dto,boolean flag){
        return execute(()->{
            List<ExcelDTO.DailyDTO> ret = new ArrayList<>();

            List<Map<String, DbTypeDTO>> tmpTable = dto.getResult().get(0);
            List<Map<String, DbTypeDTO>> tmpTable2 = dto.getResult().get(1);

            if(tmpTable.isEmpty()){
                throw new BizException("convDailyData", "데이터가 없습니다.");
            }

            for(Map<String,DbTypeDTO> row: tmpTable){

                String targetDate = flag
                        ? Util.getStrChk(row.get("REQ_DATE").getObj())
                        : Util.getStrChk(row.get("YEAR").getObj())
                        + Util.getStrChk(row.get("MON").getObj());

                ExcelDTO.DailyDTO filterDto = ret.stream()
                        .filter(v ->
                                targetDate.equals(v.getDate())
                                        && Util.getStrChk(row.get("TERMINAL_CODE").getObj()).equals(v.getTerminalCode())
                                        && Util.getStrChk(row.get("TEAM_CODE").getObj()).equals(v.getTeamCode())
                        )
                        .findFirst()
                        .orElse(null);

                if(filterDto==null){
                    filterDto   =   new ExcelDTO.DailyDTO();
                    filterDto.setDate(targetDate);
                    filterDto.setTeamCode(Util.getStrChk(row.get("TEAM_CODE").getObj()));
                    filterDto.setTeamName(Util.getStrChk(row.get("TEAM_NAME").getObj()));
                    filterDto.setTerminalCode(Util.getStrChk(row.get("TERMINAL_CODE").getObj()));

                    filterDto.setRowList(new ArrayList<>());

                    Map<String,DbTypeDTO> tmpDbRow = tmpTable2.stream().filter((v)->
                                v.get("REQ_DATE").getObj().equals(targetDate)
                            &&  v.get("TEAM_CODE").getObj().equals(Util.getStrChk(row.get("TEAM_CODE").getObj()))
                                        &&  v.get("TERMINAL_CODE").getObj().equals(Util.getStrChk(row.get("TERMINAL_CODE").getObj()))
                            ).findFirst().orElse(null);

                    if(tmpDbRow==null){
                        throw new BizException("convDailyData", "서명이 없습니다.");
                    }
                    filterDto.setApproveSign((byte[])tmpDbRow.get("APPROVE_SIGN").getObj());
                    filterDto.setApproveName(Util.getStrChk(tmpDbRow.get("APPROVE_NAME").getObj()));
                    ret.add(filterDto);
                }

                ExcelDTO.DailyRowDTO tmpRowDto = new ExcelDTO.DailyRowDTO();
                tmpRowDto.setDate(Util.getStrChk(row.get("REQ_DATE").getObj()));
                tmpRowDto.setRemark(Util.getStrChk(row.get("REMARK").getObj()));
                tmpRowDto.setSeq(Util.getDecimal(row.get("SEQ").getObj()));

                tmpRowDto.setUserName(Util.getStrChk(row.get("USER_NAME").getObj()));
                tmpRowDto.setAddHour(Util.getDouble(row.get("ADD_WORK_HOUR").getObj()));
                tmpRowDto.setHoliHour(Util.getDouble(row.get("HOLIDAY_WORK_HOUR").getObj()));
                tmpRowDto.setNightHour(Util.getDouble(row.get("NIGHT_WORK_HOUR").getObj()));
                tmpRowDto.setHoliAddHour(Util.getDouble(row.get("HOLIDAY_ADD_HOUR").getObj()));
                tmpRowDto.setTeamCode(Util.getStrChk(row.get("TEAM_CODE").getObj()));
                tmpRowDto.setTerminalCode(Util.getStrChk(row.get("TERMINAL_CODE").getObj()));

                tmpRowDto.setReqStartTime(Util.getStrChk(row.get("REQ_START_TIME").getObj()));
                tmpRowDto.setReqEndTime(Util.getStrChk(row.get("REQ_END_TIME").getObj()));
                tmpRowDto.setWorkTypeName(Util.getStrChk(row.get("WORK_TYPE_NAME").getObj()));

                filterDto.getRowList().add(tmpRowDto);

            }

            for(ExcelDTO.DailyDTO row : ret){
                row.setSumAddHour(
                        row.getRowList().stream()
                                .mapToDouble(ExcelDTO.DailyRowDTO::getAddHour)
                                .sum()
                );

                row.setSumNightHour(
                        row.getRowList().stream()
                                .mapToDouble(ExcelDTO.DailyRowDTO::getNightHour)
                                .sum()
                );

                row.setSumHoliHour(
                        row.getRowList().stream()
                                .mapToDouble(ExcelDTO.DailyRowDTO::getHoliHour)
                                .sum()
                );
                row.setSumHoliAddHour(row.getRowList().stream()
                        .mapToDouble(ExcelDTO.DailyRowDTO::getHoliAddHour)
                        .sum());
            }

            return ret;
        });
    }

    private ExcelDTO.SchMasterDTO convSchData(DbDto dto){
        return execute(()->{
            ExcelDTO.SchMasterDTO result = new ExcelDTO.SchMasterDTO();
            List<ExcelDTO.SchDTO> ret = new ArrayList<>();

            List<Map<String, DbTypeDTO>> tmpTable = null;

            tmpTable = dto.getResult().get(0);

            List<ExcelDTO.SchHeaderDTO> headers = new ArrayList<>();

            int holiday = 0;

            for(Map<String,DbTypeDTO> row : tmpTable){
                if(!Util.getStrChk(row.get("HOLIDAY_CODE").getObj()).isEmpty()){
                    holiday++;
                }
                ExcelDTO.SchHeaderDTO tmpDto = new ExcelDTO.SchHeaderDTO(Util.getStrChk(row.get("DD").getObj())
                        ,Util.getStrChk(row.get("DD_NAME").getObj())
                        ,Util.getStrChk(row.get("MON").getObj())
                        ,Util.getStrChk(row.get("HOLIDAY_CODE").getObj()));
                headers.add(tmpDto);
            }

            result.setHeaders(headers);

            tmpTable = dto.getResult().get(1);

            for(Map<String,DbTypeDTO> row : tmpTable){

                ExcelDTO.SchDTO tmpFilter = ret.stream()
                        .filter(v -> v.getYyyy().equals(Util.getStrChk(row.get("YEAR").getObj()))
                                &&v.getMon().equals(Util.getStrChk(row.get("MON").getObj()))
                                &&v.getTerminalCode().equals(Util.getStrChk(row.get("TERMINAL_CODE").getObj()))
                                &&v.getTeamCode().equals(Util.getStrChk(row.get("TEAM_CODE").getObj()))
                        )
                        .findFirst()
                        .orElse(null);
                if(tmpFilter == null){
                    byte[] createUser = (byte[])row.get("SAVE_SIGN").getObj();
                    byte[] approveUser = (byte[])row.get("CLOSE_SIGN").getObj();
                    List<ExcelDTO.SchRowDTO> tmpRow = new ArrayList<>();
                    ExcelDTO.SchRowDTO tmpRowDto = new ExcelDTO.SchRowDTO(Util.getStrChk(row.get("USER_SID").getObj()),Util.getStrChk(row.get("USER_NAME").getObj()),""
                            ,Util.getStrChk(row.get("HOLIDAY_USE").getObj()),Util.getStrChk(row.get("ANN_DAY").getObj()),new ArrayList<>());
                    tmpRow.add(tmpRowDto);
                    tmpFilter = new ExcelDTO.SchDTO(Util.getStrChk(row.get("YEAR").getObj())
                            ,Util.getStrChk(row.get("MON").getObj())
                            ,Util.getStrChk(row.get("TEAM_CODE").getObj()),Util.getStrChk(row.get("TEAM_NAME").getObj()),Util.getStrChk(row.get("TERMINAL_CODE").getObj())
                            ,createUser,approveUser,Util.getStrChk(holiday),tmpRow);
                    ret.add(tmpFilter);
                }else{
                    if(tmpFilter.getApproveUser()==null||tmpFilter.getApproveUser().length==0){
                        tmpFilter.setApproveUser((byte[])row.get("CLOSE_SIGN").getObj());
                    }
                    ExcelDTO.SchRowDTO rowFilter = tmpFilter.getRows().stream().filter(v->v.getUserSid().equals(Util.getStrChk(row.get("USER_SID").getObj())))
                            .findFirst().orElse(null);
                    if(rowFilter == null){
                        ExcelDTO.SchRowDTO tmpRowDto = new ExcelDTO.SchRowDTO(Util.getStrChk(row.get("USER_SID").getObj()),Util.getStrChk(row.get("USER_NAME").getObj()),""
                                ,Util.getStrChk(row.get("HOLIDAY_USE").getObj()),Util.getStrChk(row.get("ANN_DAY").getObj()),new ArrayList<>());
                        tmpFilter.getRows().add(tmpRowDto);
                    }
                }
            }

            tmpTable = dto.getResult().get(2);

            for(Map<String,DbTypeDTO> row : tmpTable){

                ExcelDTO.SchDTO schTmp = ret.stream()
                        .filter(v ->
                                v.getYyyy().equals(Util.getStrChk(row.get("YEAR").getObj()))
                                        && v.getMon().equals(Util.getStrChk(row.get("MON").getObj()))
                                            && v.getTeamCode().equals(Util.getStrChk(row.get("TEAM_CODE").getObj()))
                                            && v.getTerminalCode().equals(Util.getStrChk(row.get("TERMINAL_CODE").getObj()))
                        )
                        .findFirst().orElse(null);
                if(schTmp != null){
                    ExcelDTO.SchRowDTO rowFilter = schTmp.getRows().stream().filter(v->v.getUserSid().equals(Util.getStrChk(row.get("USER_SID").getObj())))
                            .findFirst().orElse(null);
                    if(rowFilter != null){
                        ExcelDTO.SchCellDTO cellFilter = rowFilter.getCells().stream().filter(v->v.getDay().equals(Util.getStrChk(row.get("DAY").getObj())))
                                .findFirst()
                                .orElse(null);
                        if(cellFilter == null){
                            ExcelDTO.SchCellDTO addTmp = new ExcelDTO.SchCellDTO();
                            addTmp.setDay(Util.getStrChk(row.get("DAY").getObj()));
                            addTmp.setWorkTypeCode(Util.getStrChk(row.get("WORK_TYPE_CODE").getObj()));
                            rowFilter.getCells().add(addTmp);

                        }
                    }
                }



            }

            result.setList(ret);


            return result;

        });
    }

    private void copyRowStyle(Sheet sheet, int srcRowIdx, int destRowIdx) {

        Row srcRow = sheet.getRow(srcRowIdx);
        if (srcRow == null) return;

        Row destRow = sheet.getRow(destRowIdx);
        if (destRow == null) {
            destRow = sheet.createRow(destRowIdx);
        }

        // 행 높이 복사
        destRow.setHeight(srcRow.getHeight());

        // 셀 스타일/값 복사
        for (int i = srcRow.getFirstCellNum(); i < srcRow.getLastCellNum(); i++) {

            Cell srcCell = srcRow.getCell(i);
            if (srcCell == null) continue;

            Cell destCell = destRow.getCell(i);
            if (destCell == null) {
                destCell = destRow.createCell(i);
            }

            destCell.setCellStyle(srcCell.getCellStyle());

        }

        // 병합 영역 복사
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);

            if (region.getFirstRow() == srcRowIdx && region.getLastRow() == srcRowIdx) {

                CellRangeAddress newRegion = new CellRangeAddress(
                        destRowIdx,
                        destRowIdx,
                        region.getFirstColumn(),
                        region.getLastColumn()
                );

                if (!isMergedRegionExists(sheet, newRegion)) {
                    sheet.addMergedRegion(newRegion);
                }
            }
        }
    }

    private boolean isMergedRegionExists(
            Sheet sheet,
            CellRangeAddress newRegion
    ) {

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {

            CellRangeAddress existing =
                    sheet.getMergedRegion(i);

            if (existing.intersects(newRegion)) {
                return true;
            }
        }

        return false;
    }


    private void copyRowStyleFast(
            Sheet sheet,
            int srcRowIdx,
            int destRowIdx
    ) {
        Row srcRow = sheet.getRow(srcRowIdx);
        if (srcRow == null) return;

        Row destRow = sheet.getRow(destRowIdx);

        if (destRow == null) {
            destRow = sheet.createRow(destRowIdx);
        }

        destRow.setHeight(srcRow.getHeight());

        for (int i = srcRow.getFirstCellNum();
             i < srcRow.getLastCellNum();
             i++) {

            Cell srcCell = srcRow.getCell(i);

            if (srcCell == null) {
                continue;
            }

            Cell destCell = destRow.getCell(i);

            if (destCell == null) {
                destCell = destRow.createCell(i);
            }

            destCell.setCellStyle(
                    srcCell.getCellStyle()
            );
        }
    }
}

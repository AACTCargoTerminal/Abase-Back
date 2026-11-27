package com.aact.infraservice.service.impl;

import com.aact.common.*;
import com.aact.common.ServiceBase;
import com.aact.commonClient.service.ClientService;
import com.aact.commonClient.service.FileClientService;
import com.aact.infraservice.dto.CapsGetType;
import com.aact.infraservice.dto.CapsTimeDTO;
import com.aact.infraservice.dto.ExcelDTO;
import com.aact.infraservice.dto.WorkDTO;
import com.aact.infraservice.repo.WorkRepo;
import com.aact.infraservice.service.WorkService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl extends ServiceBase implements WorkService {

    private final ObjectProvider<WorkRepo> workRepoProvider;
    private final CapsServiceImpl capsServiceImpl;
    private final FileClientService fileClientService;
    private final ClientService clientService;

    private static final int CARD_ROWS = 8;
    private static final int ORG_GAP = 1;

    @Override
    public ResponseDTO<?> getWorkM010_002(String date, String deptCode, String terminalCode,String approveFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkL010_005(date, deptCode, terminalCode, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getWorkM010_002", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> getWorkL010_008(WorkDTO.HrSearchDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkL010_008(dto.type(),dto.reqFlag(),dto.deptCode(),dto.terminalCode(),dto.toDate(),dto.fromDate(),dto.date(),dto.userName(),
                    dto.otFlag(),info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getWorkL010_008", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> getWorkL010_009(String date,BigDecimal userSid,BigDecimal seq) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            String yyyy = date.substring(0,4);
            String mon = date.substring(4,6);
            Integer day = Integer.parseInt(date.substring(6,8));
            DbDto dbRet = repo.getWorkL010_009(yyyy,mon,userSid,Util.getStrChk(day),seq,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getWorkL010_009", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> setWorkL010_018(WorkDTO.HrReqSaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

            DbDto dbRet = null;
            for (WorkDTO.HrReqSaveRowDTO row : dto.reqArray()) {
                dbRet = repo.setWorkL010_018(row.year(), row.mon(), row.userSid(), row.day(), row.seq(),info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkL010_018", dbRet.getErrMsg());
                }
            }
            return okOrThrow("setWorkL010_018", dbRet);
        });
    }
    @Override
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
                tmpRow.setRemark(Util.getStrChk(row.get("REMARK").getObj()));
                tmpFile.add(tmpRow);
            }



            return okOrThrow("setWorkM010_042", ResponseDTO.<List<ExcelDTO.HoldImgDTO>>builder().errFlag("N").errMsg("조회완료").data(tmpFile).build());
        });
    }
    @Override
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

                    DbDto calCount = repo.callSql("SELECT COUNT(*) CAL_COUNT FROM TCM_CALENDAR_MASTER WHERE USABLE_FLAG = 'Y' AND YYYYMM = '"+yyyy+mon+"'");
                    if(calCount.getErrFlag().equals("Y")){
                        throw new BizException("setWorkM010_014", calCount.getErrMsg());
                    }

                    int count = Util.getInteger(calCount.getResult().get(0).get(0).get("CAL_COUNT").getObj());

                    for (WorkDTO.SaveUserDTO row : dto.userArray()) {
                        BigDecimal userSid = null;

                        if (row.dayArray() == null) {
                            throw new BizException("setWorkM010_014", row.userId() + "에 스케줄 작성이 안돼있습니다.");
                        }

                        String sql = "SELECT USER_SID FROM TCM_USER_MASTER WHERE USABLE_FLAG = 'Y' AND USER_ID = '"+row.userId()+"'";

                        dbRet = repo.callSql(sql);

                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setWorkM010_014",dbRet.getErrMsg());
                        }

                        if(dbRet.getResult().get(0).isEmpty()){
                            throw new BizException("setWorkM010_014",row.userId()+" 없는 근무자 입니다.");
                        }

                        userSid = Util.getDecimal(dbRet.getResult().get(0).get(0).get("USER_SID").getObj());

                        for(int dayIdx = 0; dayIdx<row.dayArray().size();dayIdx++){
                            WorkDTO.SaveDayDTO row2 = row.dayArray().get(dayIdx);

                            String tmp = row2.dayStr().replaceAll(" ", "");
                            String[] str = tmp.isEmpty() ? new String[0] : tmp.split("!");

                            if (str.length > 2) {
                                throw new BizException("setWorkM010_014", row.userId() + "근무자 " + row2.day() + "일 " + "하루에 코드는 2개까지입니다.");
                            }

                            if (str.length == 0) {
                                dbRet = repo.setWorkL010_011(yyyy, mon, row.userId(),row2.day() , BigDecimal.ZERO,
                                        "",BigDecimal.ZERO,"",dto.terminalCode(),dto.teamCode(),
                                        info.getUserLang(), Util.getGUID(),
                                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                                if (dbRet.getErrFlag().equals("Y")) {
                                    throw new BizException("setWorkM010_014", dbRet.getErrMsg());
                                }
                                userSid = Util.getDecimal(dbRet.getRetObj().get("O_USER_SID"));


                            }
                            else{
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
                                                throw new BizException("setWorkM010_014", row.userId() +"근무자 "+row2.day()+"일 "+ terminal + " 터미널은 없는 터미널입니다.");
                                            }
                                        }
                                    }

                                    dbRet = repo.setWorkL010_011(yyyy, mon, row.userId(),row2.day() , new BigDecimal(i),
                                            code,ot,terminal,dto.terminalCode(),dto.teamCode(),
                                            info.getUserLang(), Util.getGUID(),
                                            info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                                    if (dbRet.getErrFlag().equals("Y")) {
                                        throw new BizException("setWorkM010_014", dbRet.getErrMsg());
                                    }
                                    userSid = Util.getDecimal(dbRet.getRetObj().get("O_USER_SID"));
                                }

                            }
                        }

                        dbRet = repo.setWorkL010_012(yyyy, mon, userSid,info.getUserLang(), Util.getGUID(),
                                info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                        if (dbRet.getErrFlag().equals("Y")) {
                            throw new BizException("setWorkL010_012", dbRet.getErrMsg());
                        }
                    }
                    return okOrThrow("setWorkM010_014", dbRet);

                }
        );
    }

    private String[] parsingDayStr(String dayStr) {
        try {
            dayStr = dayStr.toUpperCase();
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
    @Override
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
    @Override
    public ResponseDTO<?> setWorkL010_013(String date,String teamCode,String terminalCode) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkL010_013", "도장을 등록하여 주세요.");
            }
            DbDto dbRet = null;

            dbRet = repo.setWorkL010_013(date.substring(0, 4), date.substring(4, 6), teamCode, terminalCode,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkL010_013", dbRet.getErrMsg());
            }
            return okOrThrow("setWorkL010_013", dbRet);
        });

    }
    @Override
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
    @Override
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

            for(int i = 0;i<metaRet.size();i++){
                BigDecimal fileSize = Util.getDecimal(metaRet.get(i).getSize());
                if (fileSize.compareTo(BigDecimal.ZERO) == 0) {
                    throw new BizException("setWorkM010_018", "파일사이즈 문제");
                }
                FileMeta meta = metaRet.get(i);
                BigDecimal idx = Util.getDecimal(i+1);
                dbRet = repo.setWorkM010_018(dto.year(), dto.mon(), Util.getInteger(dto.day()).toString(), dto.userSid(), dto.seq(), idx,dto.imgType(),
                        meta.getDir(), meta.getOriginName(), meta.getChangeName(), meta.getFullpath(),
                        fileSize, meta.getMime(), meta.getExt(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_018", dbRet.getErrMsg());
                }
            }



            return okOrThrow("setWorkM010_018", dbRet);
        });
    }
    @Override
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

            ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsServiceImpl.findDateToId(CapsGetType.START,info.getUserId(),dto.reqStartDate(),dto.reqStartTime());
            if(startSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", startSet.getErrMsg());
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate endDate = LocalDate.parse(dto.reqStartDate(),formatter).plusDays(dto.addDay().longValue());
            ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsServiceImpl.findDateToId(CapsGetType.END,info.getUserId(),endDate.format(formatter),dto.reqEndTime());
            if(endSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", endSet.getErrMsg());
            }


            if(Util.getInteger(tmpTeam.get("VALUE1_NUMBER"))==1){
                dbRet = repo.setWorkM010_020(yyyy, mon, day, info.getUserSid(), dto.seq(), dto.insWorkTypeCode(),startSet.getData().orgTime(), endSet.getData().orgTime(), dto.reqStartTime(), dto.reqEndTime(),
                        dto.addDay(), Util.getDecimal(dto.addHour()), Util.getDecimal(dto.nightHour()),Util.getDecimal(dto.holiHour()), Util.getDecimal(dto.holiAddHour()),
                        dto.remark(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            }else{
                dbRet = repo.setWorkM010_019(yyyy, mon, day, info.getUserSid(), dto.seq(), dto.insWorkTypeCode(),startSet.getData().orgTime(), endSet.getData().orgTime(), dto.reqStartTime(), dto.reqEndTime(),
                        dto.addDay(), dto.deductFlag(), dto.remark(), info.getUserLang(), Util.getGUID(),
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

            dbRet = repo.setWorkM010_018(yyyy, mon, day, info.getUserSid(), dto.seq(),BigDecimal.ONE, "TIME",
                    metaRet.get(0).getDir(), metaRet.get(0).getOriginName(), metaRet.get(0).getChangeName(), metaRet.get(0).getFullpath()
                    , fileSize, metaRet.get(0).getMime(), metaRet.get(0).getExt(), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("setWorkM010_019", dbRet);
        });


    }
    @Override
    public ResponseDTO<?> setWorkM010_021(WorkDTO.SchDeleteDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = null;

            DbDto calCount = repo.callSql("SELECT COUNT(*) CAL_COUNT FROM TCM_CALENDAR_MASTER WHERE USABLE_FLAG = 'Y' AND YYYYMM = '"+dto.date()+"'");
            if(calCount.getErrFlag().equals("Y")){
                throw new BizException("setWorkM010_021", calCount.getErrMsg());
            }

            int count = Util.getInteger(calCount.getResult().get(0).get(0).get("CAL_COUNT").getObj());

            String yyyy = dto.date().substring(0,4);
            String mon = dto.date().substring(4,6);

            if(dto.userArray()==null||dto.userArray().isEmpty()){
                throw new BizException("setWorkM010_021", "삭제할 근무자가 없습니다.");
            }

            for(int i = 1;i<= count;i++){
                for(BigDecimal row : dto.userArray()){
                    dbRet = repo.setWorkM010_021(yyyy,mon,Util.getStrChk(i) ,row,new BigDecimal("-1"),"N",
                            info.getUserLang(), Util.getGUID(),
                            info.getUserId(), info.getUserIpAddress(), info.getPgmId());

                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setWorkM010_021", dbRet.getErrMsg());
                    }
                }

            }

            for(BigDecimal row : dto.userArray()){
                dbRet = repo.setWorkM010_021(yyyy,mon,"00" ,row,new BigDecimal("-1"),"N",
                        info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setWorkM010_021", dbRet.getErrMsg());
                }
            }

            return okOrThrow("setWorkM010_021", dbRet);
        });
    }
    @Override
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

                ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsServiceImpl.findDateToId(CapsGetType.START,row.userId(),row.reqStartDate(),row.reqStartTime());
                if(startSet.getErrFlag().equals("Y")){
                    throw new BizException("findDateToId", startSet.getErrMsg());
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate endDate = LocalDate.parse(row.reqStartDate(),formatter).plusDays(row.addDay().longValue());
                ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsServiceImpl.findDateToId(CapsGetType.END,row.userId(),endDate.format(formatter),row.reqEndTime());
                if(endSet.getErrFlag().equals("Y")){
                    throw new BizException("findDateToId", endSet.getErrMsg());
                }

                sql = "SELECT WORK_TYPE_CODE FROM THR_OT_DETAIL WHERE USABLE_FLAG = 'Y' AND YEAR = '"+yyyy+"' "
                       + "AND MON = '"+mon+"' AND DAY = '"+day+"' AND USER_SID = "+userSid+" AND SEQ = "+BigDecimal.ZERO;

                dbRet = repo.callSql(sql);
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_039", dbRet.getErrMsg());
                }

                if(dbRet.getResult().get(0).isEmpty()){
                    throw new BizException("setWorkM010_039", row.userId()+"은 예정근무가 없습니다.");
                }

                String workTypeCode = Util.getStrChk(dbRet.getResult().get(0).get(0).get("WORK_TYPE_CODE").getObj());

                dbRet = repo.setWorkM010_019(yyyy, mon, day, userSid, BigDecimal.ZERO,workTypeCode,startSet.getData().orgTime(), endSet.getData().orgTime(), row.reqStartTime(), row.reqEndTime(),
                        row.addDay(), row.deductFlag(), row.remark(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_039", dbRet.getErrMsg());
                }

            }
            return okOrThrow("setWorkM010_039", dbRet);
        });


    }
    @Override
    public ResponseDTO<?> setWorkL010_017(String date,BigDecimal userSid,BigDecimal seq,String remark,String type) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = null;
            String yyyy = date.substring(0, 4);
            String mon = date.substring(4, 6);
            String day = String.valueOf(Integer.parseInt(date.substring(6, 8)));

            dbRet = repo.setWorkL010_017(yyyy, mon, day, userSid, seq,remark, type, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkL010_017", dbRet.getErrMsg());
            }

            return okOrThrow("setWorkL010_017", dbRet);
        });
    }
    @Override
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

            String sql = "SELECT USER_ID FROM TCM_USER_MASTER WHERE USABLE_FLAG = 'Y' AND USER_SID = "+dto.userSid();

            dbRet = repo.callSql(sql);

            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_038", dbRet.getErrMsg());
            }

            String userId = Util.getStrChk(dbRet.getResult().get(0).get(0).get("USER_ID").getObj());

            ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsServiceImpl.findDateToId(CapsGetType.START,userId,dto.reqStartDate(),dto.reqStartTime());
            if(startSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", startSet.getErrMsg());
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate endDate = LocalDate.parse(dto.reqStartDate(),formatter).plusDays(dto.addDay().longValue());
            ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsServiceImpl.findDateToId(CapsGetType.END,userId,endDate.format(formatter),dto.reqEndTime());
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
    @Override
    public ResponseDTO<?> getWorkL010_007(String date,String monFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkL010_007(date, monFlag,info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getWorkL010_007", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> getWorkL010_006(String date, String deptCode, String terminalCode,String userName, String approveFlag) {
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
                throw new BizException("getWorkL010_006", "날짜 에러");
            }
            DbDto dbRet = repo.getWorkL010_006(yyyy, mon, day, deptCode,terminalCode, userName, approveFlag, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getWorkL010_006", dbRet);
        });
    }

    @Override
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
    @Override
    public ResponseDTO<?> setWorkL010_014(List<CapsTimeDTO.DeleteDTO> dtos) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkL010_014", "도장을 등록하여 주세요.");
            }
            DbDto dbRet = null;
            for (CapsTimeDTO.DeleteDTO row : dtos) {
                repo.beginTrans();
                String day = String.valueOf(Integer.parseInt(row.date().substring(6, 8)));
                dbRet = repo.setWorkL010_014(row.date().substring(0, 4), row.date().substring(4, 6), day, row.userSid(), row.seq(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkL010_014", dbRet.getErrMsg());
                }
                repo.commit();
            }
            return okOrThrow("setWorkL010_014", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> setCapsReSave(List<CapsTimeDTO.DeleteDTO> dtos) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();


        return execute(repo, () -> {
            DbDto dbRet = null;
            for (CapsTimeDTO.DeleteDTO row : dtos) {

                dbRet = repo.getWorkM010_003(row.date(), row.userSid(),row.seq(),info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setCapsReSave", dbRet.getErrMsg());
                }
                String yyyy = row.date().substring(0, 4);
                String mon = row.date().substring(4, 6);
                String day = String.valueOf(Integer.parseInt(row.date().substring(6, 8)));

                if(dbRet.getResult().get(0).isEmpty()){
                    throw new BizException("setCapsReSave", "잘못된 사용자가 있습니다.");
                }

                Map<String,DbTypeDTO> userDto = dbRet.getResult().get(0).get(0);

                StringBuilder sql = new StringBuilder();
                sql.append("SELECT L.REMARK  REMARK ");
                sql.append("FROM THR_OT_DETAIL_LOG L ");
                sql.append("WHERE L.YEAR = '" + yyyy + "' ");
                sql.append("AND L.MON = '" + mon + "' ");
                sql.append("AND L.DAY = '" + day + "' ");
                sql.append("AND L.USER_SID = " + row.userSid() + " ");
                sql.append("AND L.SEQ = " + row.seq() + " ");
                sql.append("AND L.LOG_SEQ = ( ");
                sql.append("    SELECT MAX(ODL.LOG_SEQ) ");
                sql.append("    FROM THR_OT_DETAIL_LOG ODL ");
                sql.append("    JOIN TCM_CODE_MASTER TCM ");
                sql.append("      ON TCM.CODE_CODE = ODL.REQ_FLAG ");
                sql.append("     AND TCM.USABLE_FLAG = 'Y' ");
                sql.append("    WHERE ODL.YEAR = '" + yyyy + "' ");
                sql.append("      AND ODL.MON = '" + mon + "' ");
                sql.append("      AND ODL.DAY = '" + day + "' ");
                sql.append("      AND ODL.USER_SID = " + row.userSid() + " ");
                sql.append("      AND ODL.SEQ = " + row.seq() + " ");
                sql.append("      AND ODL.USABLE_FLAG = 'Y' ");
                sql.append("      AND NVL(TCM.VALUE6_CHAR,'*') = 'OT_INS' ");
                sql.append(")");

                dbRet = repo.callSql(sql.toString());

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setCapsReSave", dbRet.getErrMsg());
                }
                String userId = Util.getStrChk(userDto.get("USER_ID").getObj());
                if(dbRet.getResult().get(0).isEmpty()){
                    throw new BizException("setCapsReSave", userId+" 신청 최신사유가 없습니다.");
                }

                String remark = Util.getStrChk(dbRet.getResult().get(0).get(0).get("REMARK").getObj());
                String createdId = Util.getStrChk(userDto.get("CREATED_USER_ID").getObj());
                String startTime = Util.getStrChk(userDto.get("START_TIME").getObj());
                String endTime = Util.getStrChk(userDto.get("END_TIME").getObj());
                long addDay = Util.getInteger(userDto.get("VALUE1_NUMBER").getObj());
                String deductFlag = Util.getStrChk(userDto.get("DEDUCT_FLAG").getObj());
                String workTypeCode = Util.getStrChk(userDto.get("INS_CODE_CODE").getObj());

                ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsServiceImpl.findDateToId(CapsGetType.START,userId,row.date(),startTime);
                if(startSet.getErrFlag().equals("Y")){
                    throw new BizException("setCapsReSave", startSet.getErrMsg());
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate endDate = LocalDate.parse(row.date(), formatter)
                        .plusDays(addDay);
                ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsServiceImpl.findDateToId(CapsGetType.END,userId,endDate.format(formatter),endTime);
                if(endSet.getErrFlag().equals("Y")){
                    throw new BizException("setCapsReSave", endSet.getErrMsg());
                }

                dbRet = repo.setWorkM010_019(yyyy, mon, day, row.userSid(), row.seq(),workTypeCode,startSet.getData().orgTime(), endSet.getData().orgTime(), startTime,endTime,
                        Util.getDecimal(addDay),deductFlag,  remark, info.getUserLang(), Util.getGUID(),
                        createdId, info.getUserIpAddress(), info.getPgmId());

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setCapsReSave", dbRet.getErrMsg());
                }
            }
            return okOrThrow("setCapsReSave", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> setWorkL010_015(WorkDTO.ApproveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkL010_015", "도장을 등록하여 주세요.");
            }

            DbDto dbRet = null;
            for(WorkDTO.ApproveRowDTO row : dto.userArray()){
                repo.beginTrans();
                for(WorkDTO.ApproveCellDTO day:row.dayArray()){
                    dbRet = repo.setWorkL010_015(dto.date().substring(0, 4), dto.date().substring(4, 6), day.day(), row.userSid(),day.seq(),
                            info.getUserLang(), Util.getGUID(),
                            info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                    if (dbRet.getErrFlag().equals("Y")) {
                        throw new BizException("setWorkL010_015", dbRet.getErrMsg());
                    }
                }
                repo.commit();

            }
            return okOrThrow("setWorkL010_015", dbRet);
        });
    }
    @Override
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

    @Override
    public ResponseDTO<?> setWorkM010_025(WorkDTO.WorkDefaultDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

            DbDto dbRet = null;
            dbRet = repo.setWorkM010_025(dto.year(), dto.mon(), dto.day(), dto.userSid(), dto.seq(),"N",info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_025", dbRet.getErrMsg());
            }
            return okOrThrow("setWorkM010_025", dbRet);
        });
    }

    @Override
    public ResponseDTO<?> setWorkM010_025_1(WorkDTO.NightApprDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;

            if(dto.array()==null||dto.array().isEmpty()){
                throw new BizException("setWorkM010_025_1","삭제 항목이 없습니다.");
            }

            for(WorkDTO.WorkDefaultDTO row:dto.array()){
                dbDto = repo.setWorkM010_025(row.year(),row.mon(),row.day(),row.userSid(),row.seq(),"Y"
                        ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

                if(dbDto.getErrFlag().equals("Y")){
                    throw new BizException("setWorkM010_025_1",dbDto.getErrMsg());
                }
            }


            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    @Override
    public ResponseDTO<?> setWorkL010_016(WorkDTO.HrReqSaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

            DbDto dbRet = null;
            for (WorkDTO.HrReqSaveRowDTO row : dto.reqArray()) {

                dbRet = repo.setWorkL010_016(row.year(), row.mon(), row.day(), row.userSid(), row.seq(),info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkL010_016", dbRet.getErrMsg());
                }


            }
            return okOrThrow("setWorkL010_016", dbRet);
        });
    }
    @Override
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
    @Override
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
                    "ON TUM.USER_SID = A.USER_SID AND TUM.USABLE_FLAG = 'Y' " +
                    "WHERE NOT EXISTS ( " +
                    "    SELECT 1 " +
                    "    FROM TCM_INTRA_USER_RELATION WKT " +
                    "    WHERE WKT.USER_SID = A.USER_SID " +
                    "    AND WKT.CLASS_CODE = 'HRWKT' " +
                    "    AND WKT.CODE_CODE = 'C' " +
                    "    AND WKT.USABLE_FLAG = 'Y' " +
                    ") "+
                    "AND NOT EXISTS (\n" +
                    "\t\t\t\t      \t\tSELECT\t1\n" +
                    "\t\t\t\t      \t\tFROM\tTCM_INTRA_USER_RELATION\n" +
                    "\t\t\t\t      \t\tWHERE\tCLASS_CODE\t=\t'HRWDT'\n" +
                    "\t\t\t\t      \t\tAND\t\tCODE_CODE\t=\t'B'\n" +
                    "\t\t\t\t      \t\tAND\t\tUSABLE_FLAG\t=\t'Y'\n" +
                    "\t\t\t\t      \t\tAND\t\tUSER_SID\t=\tTUM.USER_SID\n" +
                    "\t\t\t\t      \t\tAND\t\tSUBSTR(NVL(VALUE1,'99991231'),0,6) < '" +date+"' "+
                    "\t\t\t\t      )";

            for(int i = 0;i<cal.size();i++){
                Map<String,Object> calRow = cal.get(i);

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
                        dbRet = repo.setWorkL010_011(yyyy,mon,userId,day,BigDecimal.ZERO,workTypeCode,
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

                        if(i == cal.size()-1){
                            dbRet = repo.setWorkL010_012(yyyy,mon,userSid,"KOR",Util.getGUID(),"DAEMON","::","DAEMON");
                            if(dbRet.getErrFlag().equals("Y")){
                                throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                            }

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
                    "AND IUR.USABLE_FLAG = 'Y' "+
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
                dbRet = repo.setWorkL010_013(yyyy,mon,groupRow.get("TEAM_CODE"),groupRow.get("TERMINAL_CODE"),"KOR",Util.getGUID(),teamId,"::","DAEMON");
                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setScheduleAutoJob",dbRet.getErrMsg());
                }
            }

            return okOrThrow("setScheduleAutoJob", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> setSchAutoMatch(String dateStr){
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HHmmss");
            DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String yyyyMM = dateStr.substring(0,6);
            String dd = dateStr.substring(6,8);

            StringBuilder sql = new StringBuilder();

            sql.append("SELECT TOD.YEAR, TOD.MON, TOD.USER_SID, TOD.DAY, TOD.SEQ, TCM.VALUE1_NUMBER ADD_DAY, TUM.USER_ID, ");
            sql.append("RPAD(TCM.VALUE2_CHAR,4,'0') START_TIME, RPAD(TCM.VALUE3_CHAR,4,'0') END_TIME, VALUE4_CHAR HOLIDAY_FLAG, ");
            sql.append("ODR.CODE_CODE TEAM_CODE, ODR2.CODE_CODE TERMINAL_CODE ");
            sql.append("FROM THR_OT_DETAIL  TOD ");
            sql.append("JOIN TCM_USER_MASTER TUM ON TOD.USER_SID = TUM.USER_SID AND TUM.USABLE_FLAG = 'Y' ");
            sql.append("JOIN TCM_CODE_MASTER TCM ON TOD.WORK_TYPE_CODE = TCM.CODE_CODE AND TCM.CLASS_CODE = 'OPCOD' AND TCM.USABLE_FLAG = 'Y' ");
            sql.append("LEFT JOIN THR_OT_TIME TOT ON TOT.YEAR = TOD.YEAR AND TOT.MON = TOD.MON AND TOT.DAY = TOD.DAY AND TOT.USER_SID = TOD.USER_SID ");
            sql.append("AND TOT.SEQ = TOD.SEQ AND TOT.USABLE_FLAG = 'Y' JOIN\tTHR_OT_DETAIL_REF\tODR ON\t\tODR.YEAR\t=\tTOD.YEAR AND\t\tODR.MON\t\t=\tTOD.MON ");
            sql.append("AND\t\tODR.DAY\t\t=\tTOD.DAY AND\t\tODR.USER_SID\t=\tTOD.USER_SID AND\t\tODR.SEQ\t\t\t=\tTOD.SEQ AND\t\tODR.CLASS_CODE\t=\t'HRPAT' ");
            sql.append("AND\t\tODR.USABLE_FLAG\t=\t'Y'\t\t JOIN\tTHR_OT_DETAIL_REF\tODR2 ON\t\tODR2.YEAR\t=\tTOD.YEAR AND\t\tODR2.MON\t\t=\tTOD.MON ");
            sql.append("AND\t\tODR2.DAY\t\t=\tTOD.DAY AND\t\tODR2.USER_SID\t=\tTOD.USER_SID AND\t\tODR2.SEQ\t\t\t=\tTOD.SEQ AND\t\tODR2.CLASS_CODE\t=\t'TRMCD' ");
            sql.append("AND\t\tODR2.USABLE_FLAG\t=\t'Y' WHERE\tNVL(TOD.APPROVE_FLAG,'N') = 'N' AND\t\tTOD.YEAR||TOD.MON = '" + yyyyMM+"' ");
            sql.append("AND TOT.REQ_START_TIME IS NULL ");
            sql.append("AND ( ");
            sql.append("    TO_NUMBER(TOD.DAY) < TO_NUMBER('" + dd + "') ");
            sql.append("    OR ( ");
            sql.append("        TO_NUMBER(TOD.DAY) = TO_NUMBER('" + dd + "') ");
            sql.append("        AND TCM.VALUE1_NUMBER = 0 ");
            sql.append("    ) ");
            sql.append(") ");
            sql.append("AND EXISTS ( ");
            sql.append("    SELECT 1 FROM THR_OT_MASTER TOM ");
            sql.append("    WHERE TOM.YEAR = TOD.YEAR ");
            sql.append("    AND TOM.MON = TOD.MON AND TOM.USER_SID = TOD.USER_SID ");
            sql.append("    AND TOM.CLOSE_FLAG = 'Y' AND TOM.USABLE_FLAG = 'Y' ");
            sql.append(") ");
            sql.append("ORDER BY TO_NUMBER(TOD.DAY) DESC");

            dbRet = repo.callSql(sql.toString());

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setSchAutoMatch",dbRet.getErrMsg());
            }

            if(dbRet.getResult().get(0).isEmpty()){
                throw new BizException("setSchAutoMatch","처리할 목록이 없습니다.");
            }

            List<Map<String,DbTypeDTO>> dbTmp = dbRet.getResult().get(0);

            for(Map<String,DbTypeDTO> row: dbTmp){
                String yyyy = Util.getStrChk(row.get("YEAR").getObj());
                String mon = Util.getStrChk(row.get("MON").getObj());
                String day = Util.getStrChk(row.get("DAY").getObj());
                String parsingDay = String.format("%02d", Util.getInteger(day));
                BigDecimal userSid = Util.getDecimal(row.get("USER_SID").getObj());
                String userId = Util.getStrChk(row.get("USER_ID").getObj());
                BigDecimal seq = Util.getDecimal(row.get("SEQ").getObj());
                String teamCode = Util.getStrChk(row.get("TEAM_CODE").getObj());
                String terminalCode = Util.getStrChk(row.get("TERMINAL_CODE").getObj());

                long addDay = Util.getInteger(row.get("ADD_DAY").getObj());
                String holidayFlag = Util.getStrChk(row.get("HOLIDAY_FLAG").getObj());

                String startTime = Util.getStrChk(row.get("START_TIME").getObj());
                String endTime = Util.getStrChk(row.get("END_TIME").getObj());

                if((startTime.isEmpty()||endTime.isEmpty())&&!holidayFlag.equals("Y")){
                    continue;
                }

                String saveStartTime = "XXXX";
                String saveEndTime = "XXXX";
                boolean approveStartFlag = false;
                boolean approveEndFlag = false;

                LocalDate startDate = LocalDate.parse((yyyy+mon+parsingDay),formatter).minusDays(1);
                LocalDate endDate = LocalDate.parse((yyyy+mon+parsingDay),formatter).plusDays(1);

                ResponseDTO<List<CapsTimeDTO.CapsUserTimeDTO>> capsTime = capsServiceImpl.findIdToDate(userId,startDate.format(formatter),endDate.format(formatter));
                if(capsTime.getErrFlag().equals("Y")){
                    throw new BizException("setSchAutoMatch", capsTime.getErrMsg());
                }

                sql = new StringBuilder();
                sql.append("SELECT CASE WHEN TOT.REQ_START_TIME IS NULL THEN CASE WHEN TCM.VALUE2_CHAR IS NULL THEN TOT.CAPS_START_TIME ELSE RPAD(TCM.VALUE2_CHAR,4,'0') END ELSE TOT.CAPS_START_TIME END START_TIME ");
                sql.append(", CASE WHEN TOT.REQ_START_TIME IS NULL THEN CASE WHEN TCM.VALUE3_CHAR IS NULL THEN TOT.CAPS_END_TIME ELSE RPAD(TCM.VALUE3_CHAR,4,'0') END ELSE TOT.CAPS_END_TIME END END_TIME ");
                sql.append(", CASE WHEN TOT.REQ_START_TIME IS NULL THEN TCM.VALUE1_NUMBER ELSE TOT.ADD_DAY END AS ADD_DAY ");
                sql.append("FROM THR_OT_DETAIL TOD ");
                sql.append("JOIN TCM_CODE_MASTER TCM ");
                sql.append("ON TCM.CODE_CODE = TOD.WORK_TYPE_CODE AND TCM.USABLE_FLAG = 'Y' AND TCM.CLASS_CODE = 'OPCOD' ");
                sql.append("LEFT JOIN THR_OT_TIME TOT ON TOT.YEAR = TOD.YEAR AND TOT.MON = TOD.MON AND TOT.DAY = TOD.DAY AND TOT.USER_SID = TOD.USER_SID ");
                sql.append("AND TOT.SEQ = TOD.SEQ AND TOT.USABLE_FLAG = 'Y' ");
                sql.append("WHERE TOD.YEAR||TOD.MON||LPAD(TOD.DAY,2,'0') = '"+startDate.format(formatter)+"' ");
                sql.append("AND TOD.USER_SID = "+userSid+" ");
                sql.append("AND TOD.USABLE_FLAG = 'Y' ");
                sql.append("ORDER BY TOD.SEQ DESC");

                StringBuilder sql2 = new StringBuilder();
                sql2.append("SELECT CASE WHEN TOT.REQ_START_TIME IS NULL THEN CASE WHEN TCM.VALUE2_CHAR IS NULL THEN TOT.CAPS_START_TIME ELSE RPAD(TCM.VALUE2_CHAR,4,'0') END ELSE TOT.CAPS_START_TIME END START_TIME ");
                sql2.append(", CASE WHEN TOT.REQ_START_TIME IS NULL THEN CASE WHEN TCM.VALUE3_CHAR IS NULL THEN TOT.CAPS_END_TIME ELSE RPAD(TCM.VALUE3_CHAR,4,'0') END ELSE TOT.CAPS_END_TIME END END_TIME ");
                sql2.append(", CASE WHEN TOT.REQ_START_TIME IS NULL THEN TCM.VALUE1_NUMBER ELSE TOT.ADD_DAY END AS ADD_DAY ");
                sql2.append("FROM THR_OT_DETAIL TOD ");
                sql2.append("JOIN TCM_CODE_MASTER TCM ");
                sql2.append("ON TCM.CODE_CODE = TOD.WORK_TYPE_CODE AND TCM.USABLE_FLAG = 'Y' AND TCM.CLASS_CODE = 'OPCOD' ");
                sql2.append("LEFT JOIN THR_OT_TIME TOT ON TOT.YEAR = TOD.YEAR AND TOT.MON = TOD.MON AND TOT.DAY = TOD.DAY AND TOT.USER_SID = TOD.USER_SID ");
                sql2.append("AND TOT.SEQ = TOD.SEQ AND TOT.USABLE_FLAG = 'Y' ");
                sql2.append("WHERE TOD.YEAR||TOD.MON||LPAD(TOD.DAY,2,'0') = '"+endDate.format(formatter)+"' ");
                sql2.append("AND TOD.USER_SID = "+userSid+" ");
                sql2.append("AND TOD.USABLE_FLAG = 'Y' ");
                sql2.append("ORDER BY TOD.SEQ ASC");

                if(holidayFlag.equals("Y")){
                    //시작시간
                    dbRet = repo.callSql(sql.toString());
                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setSchAutoMatch", dbRet.getErrMsg());
                    }

                    if(dbRet.getResult().get(0).isEmpty()){
                        CapsTimeDTO.CapsUserTimeDTO capsStart = capsTime.getData().stream()
                                .filter(item->(yyyy+mon+parsingDay).equals(item.getDate()))
                                .filter(item->"1".equals(item.getMode()))
                                .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                .orElse(null);

                        if(capsStart != null){
                            saveStartTime = capsStart.getTime().substring(0,4);
                        }
                    }else{
                        String bfEndTime = Util.getStrChk(dbRet.getResult().get(0).get(0).get("END_TIME").getObj());
                        long bfAddDay = Util.getInteger(dbRet.getResult().get(0).get(0).get("ADD_DAY").getObj());

                        if(bfEndTime.isEmpty()||bfEndTime.equals("XXXX")){
                            CapsTimeDTO.CapsUserTimeDTO capsStart = capsTime.getData().stream()
                                    .filter(item->(yyyy+mon+parsingDay).equals(item.getDate()))
                                    .filter(item->"1".equals(item.getMode()))
                                    .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                    .orElse(null);

                            if(capsStart != null){
                                saveStartTime = capsStart.getTime().substring(0,4);
                            }
                        }else{
                            String tmpStartTime = LocalDateTime.parse( startDate.plusDays(bfAddDay).format(formatter)+bfEndTime, formatter3)
                                    .plusMinutes(30)
                                    .format(formatter3);
                            String tmpEndTime = yyyy+mon+parsingDay+"2359";

                            CapsTimeDTO.CapsUserTimeDTO capsStart = capsTime.getData().stream()
                                    .filter(item -> "1".equals(item.getMode()))
                                    .filter(item -> {
                                        String dateTime = item.getDate()
                                                + String.format("%06d", Integer.parseInt(String.valueOf(item.getTime())));

                                        return dateTime.compareTo(tmpStartTime + "00") >= 0
                                                && dateTime.compareTo(tmpEndTime + "59") <= 0;
                                    })
                                    .min(Comparator.comparing(item ->
                                            item.getDate() + String.format("%06d",
                                                    Integer.parseInt(String.valueOf(item.getTime())))
                                    ))
                                    .orElse(null);
                            if(capsStart != null){
                                saveStartTime = capsStart.getTime().substring(0,4);
                            }
                        }
                    }

                    //종료시간
                    if(!saveStartTime.equals("XXXX")){
                        dbRet = repo.callSql(sql2.toString());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setSchAutoMatch", dbRet.getErrMsg());
                        }

                        if(dbRet.getResult().get(0).isEmpty()){
                            CapsTimeDTO.CapsUserTimeDTO capsEnd = capsTime.getData().stream()
                                    .filter(item->(yyyy+mon+parsingDay).equals(item.getDate()))
                                    .filter(item->"2".equals(item.getMode()))
                                    .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                    .orElse(null);

                            if(capsEnd != null){
                                saveEndTime = capsEnd.getTime().substring(0,4);
                            }
                        }else{
                            String afStartTime = Util.getStrChk(dbRet.getResult().get(0).get(0).get("START_TIME").getObj());
                            if(afStartTime.isEmpty()||afStartTime.equals("XXXX")){
                                CapsTimeDTO.CapsUserTimeDTO capsEnd = capsTime.getData().stream()
                                        .filter(item->(yyyy+mon+parsingDay).equals(item.getDate()))
                                        .filter(item->"2".equals(item.getMode()))
                                        .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                        .orElse(null);

                                if(capsEnd != null){
                                    saveEndTime = capsEnd.getTime().substring(0,4);
                                }
                            }else{
                                String tmpStartTime = LocalDateTime.parse( endDate.format(formatter)+afStartTime, formatter3)
                                        .minusMinutes(30)
                                        .format(formatter3);;
                                String tmpEndTime = yyyy+mon+parsingDay+"0000";

                                CapsTimeDTO.CapsUserTimeDTO capsEnd = capsTime.getData().stream()
                                        .filter(item -> "2".equals(item.getMode()))
                                        .filter(item -> {
                                            String dateTime = item.getDate()
                                                    + String.format("%06d", Integer.parseInt(String.valueOf(item.getTime())));

                                            return dateTime.compareTo(tmpEndTime + "00") >= 0
                                                    && dateTime.compareTo(tmpStartTime + "59") <= 0;
                                        })
                                        .min(Comparator.comparing(item ->
                                                item.getDate() + String.format("%06d",
                                                        Integer.parseInt(String.valueOf(item.getTime())))
                                        ))
                                        .orElse(null);
                                if(capsEnd != null){
                                    saveEndTime = capsEnd.getTime().substring(0,4);
                                }
                            }
                        }
                    }

                }
                else {

                    //시작시간
                    LocalTime startTarget = LocalTime.parse(
                            startTime,
                            DateTimeFormatter.ofPattern("HHmm")
                    );

                    CapsTimeDTO.CapsUserTimeDTO capsStart = capsTime.getData().stream()
                            .filter(item->(yyyy+mon+parsingDay).equals(item.getDate()))
                            .filter(item->{
                                String timeStr = String.format("%6s", item.getTime()).replace(' ', '0');
                                LocalTime time = LocalTime.parse(timeStr, formatter2);

                                long diff = Duration.between(time, startTarget).getSeconds();

                                return diff >= 0 && diff <= 3600;
                            }).findFirst().orElse(null);
                    if(capsStart != null){
                        saveStartTime = capsStart.getTime().substring(0,4);
                        approveStartFlag = true;
                    }else{
                        dbRet = repo.callSql(sql.toString());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setSchAutoMatch", dbRet.getErrMsg());
                        }

                        if(dbRet.getResult().get(0).isEmpty()){
                            capsStart = capsTime.getData().stream()
                                    .filter(item->(yyyy+mon+parsingDay).equals(item.getDate()))
                                    .filter(item->"1".equals(item.getMode()))
                                    .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                    .orElse(null);

                            if(capsStart != null){
                                saveStartTime = capsStart.getTime().substring(0,4);
                            }
                        }else{
                            String bfEndTime = Util.getStrChk(dbRet.getResult().get(0).get(0).get("END_TIME").getObj());

                            if(bfEndTime.isEmpty()||bfEndTime.equals("XXXX")){
                                capsStart = capsTime.getData().stream()
                                        .filter(item->(yyyy+mon+parsingDay).equals(item.getDate()))
                                        .filter(item->"1".equals(item.getMode()))
                                        .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                        .orElse(null);

                                if(capsStart != null){
                                    saveStartTime = capsStart.getTime().substring(0,4);
                                }
                            }else{
                                String tmpStartTime = startDate.format(formatter)+bfEndTime;
                                String tmpEndTime = yyyy+mon+parsingDay+startTime;

                                capsStart = capsTime.getData().stream()
                                        .filter(item -> "1".equals(item.getMode()))
                                        .filter(item -> {
                                            String dateTime = item.getDate()
                                                    + String.format("%06d", Integer.parseInt(String.valueOf(item.getTime())));

                                            return dateTime.compareTo(tmpStartTime + "00") >= 0
                                                    && dateTime.compareTo(tmpEndTime + "59") <= 0;
                                        })
                                        .min(Comparator.comparing(item ->
                                                item.getDate() + String.format("%06d",
                                                        Integer.parseInt(String.valueOf(item.getTime())))
                                        ))
                                        .orElse(null);
                                if(capsStart != null){
                                    saveStartTime = capsStart.getTime().substring(0,4);
                                }
                            }
                        }

                    }

                    //종료시간
                    LocalTime endTarget = LocalTime.parse(
                            endTime,
                            DateTimeFormatter.ofPattern("HHmm")
                    );

                    CapsTimeDTO.CapsUserTimeDTO capsEnd = capsTime.getData().stream()
                            .filter(item->(addDay>0?endDate.format(formatter):yyyy+mon+parsingDay).equals(item.getDate()))
                            .filter(item->{
                                String timeStr = String.format("%6s", item.getTime()).replace(' ', '0');
                                LocalTime time = LocalTime.parse(timeStr, formatter2);

                                long diff = Duration.between(endTarget, time).getSeconds();

                                return diff >= 0 && diff <= 1800;
                            }).findFirst().orElse(null);

                    if(capsEnd != null){
                        saveEndTime = capsEnd.getTime().substring(0,4);
                        approveEndFlag = true;
                    }else{
                        dbRet = repo.callSql(sql2.toString());
                        if(dbRet.getErrFlag().equals("Y")){
                            throw new BizException("setSchAutoMatch", dbRet.getErrMsg());
                        }

                        if(dbRet.getResult().get(0).isEmpty()){
                            capsEnd = capsTime.getData().stream()
                                    .filter(item->(addDay>0?endDate.format(formatter):yyyy+mon+parsingDay).equals(item.getDate()))
                                    .filter(item->"2".equals(item.getMode()))
                                    .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                    .orElse(null);

                            if(capsEnd != null){
                                saveEndTime = capsEnd.getTime().substring(0,4);
                            }
                        }else{
                            String afStartTime = Util.getStrChk(dbRet.getResult().get(0).get(0).get("START_TIME").getObj());

                            if(afStartTime.isEmpty()||afStartTime.equals("XXXX")){
                                capsEnd = capsTime.getData().stream()
                                        .filter(item->(addDay>0?endDate.format(formatter):yyyy+mon+parsingDay).equals(item.getDate()))
                                        .filter(item->"2".equals(item.getMode()))
                                        .min(Comparator.comparingInt(item -> Integer.parseInt(String.valueOf(item.getTime()))))
                                        .orElse(null);

                                if(capsEnd != null){
                                    saveEndTime = capsEnd.getTime().substring(0,4);
                                }
                            }else{
                                String tmpStartTime = endDate.format(formatter)+afStartTime;
                                String tmpEndTime = (addDay>0?endDate.format(formatter):yyyy+mon+parsingDay)+endTime;

                                capsEnd = capsTime.getData().stream()
                                        .filter(item -> "2".equals(item.getMode()))
                                        .filter(item -> {
                                            String dateTime = item.getDate()
                                                    + String.format("%06d", Integer.parseInt(String.valueOf(item.getTime())));

                                            return dateTime.compareTo(tmpEndTime + "00") >= 0
                                                    && dateTime.compareTo(tmpStartTime + "59") <= 0;
                                        })
                                        .min(Comparator.comparing(item ->
                                                item.getDate() + String.format("%06d",
                                                        Integer.parseInt(String.valueOf(item.getTime())))
                                        ))
                                        .orElse(null);
                                if(capsEnd != null){
                                    saveEndTime = capsEnd.getTime().substring(0,4);
                                }
                            }
                        }
                    }
                }

                dbRet = repo.setWorkM010_037(yyyy,mon,day,userSid,seq,null,null,null,null,saveStartTime,saveEndTime
                                            , Util.getDecimal(addDay),null,null,null,null,"N",""
                                            ,"KOR",Util.getGUID(),"DAEMON","","DAEMON");

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setSchAutoMatch",dbRet.getErrMsg());
                }

                //스케줄 확정
                if(approveStartFlag && approveEndFlag){
                    String auSql = " SELECT\tTUM.USER_ID " +
                            " FROM \tTCM_INTRA_USER_RELATION\tIUR\n" +
                            " JOIN\tTCM_USER_MASTER\tTUM\n" +
                            " ON\t\tTUM.USER_SID = IUR.USER_SID\n" +
                            " AND\tTUM.USABLE_FLAG = 'Y'\n" +
                            " JOIN\tTCM_CODE_MASTER\t\tTCM\n" +
                            " ON\t\tTCM.CLASS_CODE = 'HRMTR'\n" +
                            " AND\tTCM.CODE_CODE = IUR.VALUE1\n" +
                            " AND\tTCM.USABLE_FLAG = 'Y'\n" +
                            " WHERE\tIUR.CLASS_CODE = 'HRTAU'\n" +
                            " AND\tIUR.VALUE2 = '10'\n" +
                            " AND IUR.USABLE_FLAG = 'Y'" +
                            " AND\tIUR.CODE_CODE = '"+teamCode+"' " +
                            "AND (\n" +
                            "            (TCM.VALUE1_CHAR IS NULL AND IUR.VALUE1 = '" + terminalCode +"') "+
                            "            OR\n" +
                            "            (TCM.VALUE1_CHAR IS NOT NULL AND INSTR(TCM.VALUE1_CHAR, '"+terminalCode+"') > 0)\n" +
                            "        )";
                    dbRet = repo.callSql(auSql);

                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setSchAutoMatch",dbRet.getErrMsg());
                    }

                    if(dbRet.getResult().get(0).isEmpty()){
                        throw new BizException("setSchAutoMatch",teamCode+" : "+ terminalCode+" 권한을 가진 팀장이 없습니다.");
                    }
                    String approveId = Util.getStrChk(dbRet.getResult().get(0).get(0).get("USER_ID").getObj());

                    dbRet = repo.setWorkL010_015(yyyy,mon,day,userSid,seq,"KOR",Util.getGUID(),approveId,"DAEMON","DAEMON");

                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setSchAutoMatch",dbRet.getErrMsg());
                    }
                }

            }

            return okOrThrow("setSchAutoMatch", dbRet);
        });
    }
    @Override
    public ResponseDTO<?> setAutoSchAppr(String dateStr,boolean flag){
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbRet = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String sql = "SELECT\tTOD.YEAR\n" +
                    "\t, \tTOD.MON\n" +
                    "\t, \tTOD.DAY\n" +
                    "\t, \tTOD.USER_SID\n" +
                    "\t, \tTOD.SEQ\n" +
                    "\t, \tODR.CODE_CODE TERMINAL_CODE\n" +
                    "\t, \tODR2.CODE_CODE TEAM_CODE\n" +
                    "FROM\tTHR_OT_DETAIL\t\tTOD\n" +
                    "LEFT\n" +
                    "JOIN\tTHR_OT_TIME\t\t\tTOT\n" +
                    "ON\t\tTOD.YEAR\t\t=\tTOT.YEAR\n" +
                    "AND\t\tTOD.MON\t\t\t=\tTOT.MON\n" +
                    "AND\t\tTOD.DAY\t\t\t=\tTOT.DAY\n" +
                    "AND\t\tTOD.USER_SID\t=\tTOT.USER_SID\n" +
                    "AND\t\tTOD.SEQ\t\t\t=\tTOT.SEQ\n" +
                    "AND\t\tTOT.USABLE_FLAG\t=\t'Y'\n" +
                    "JOIN\tTHR_OT_DETAIL_REF\tODR\n" +
                    "ON\t\tTOD.YEAR\t\t=\tODR.YEAR\n" +
                    "AND\t\tTOD.MON\t\t\t=\tODR.MON\n" +
                    "AND\t\tTOD.DAY\t\t\t=\tODR.DAY\n" +
                    "AND\t\tTOD.USER_SID\t=\tODR.USER_SID\n" +
                    "AND\t\tTOD.SEQ\t\t\t=\tODR.SEQ\n" +
                    "AND\t\tODR.CLASS_CODE\t=\t'TRMCD'\n" +
                    "AND\t\tODR.USABLE_FLAG\t=\t'Y'\n" +
                    "JOIN\tTHR_OT_DETAIL_REF\tODR2\n" +
                    "ON\t\tTOD.YEAR\t\t=\tODR2.YEAR\n" +
                    "AND\t\tTOD.MON\t\t\t=\tODR2.MON\n" +
                    "AND\t\tTOD.DAY\t\t\t=\tODR2.DAY\n" +
                    "AND\t\tTOD.USER_SID\t=\tODR2.USER_SID\n" +
                    "AND\t\tTOD.SEQ\t\t\t=\tODR2.SEQ\n" +
                    "AND\t\tODR2.CLASS_CODE\t=\t'HRPAT'\n" +
                    "AND\t\tODR2.USABLE_FLAG\t=\t'Y'\n" +
                    "WHERE\tTOD.USABLE_FLAG\t=\t'Y'\n" +
                    "AND\t\tTOT.REQ_START_TIME\tIS\tNULL\n" +
                    "AND\t\tNVL(TOD.APPROVE_FLAG,'N')\t=\t'N'\n" +
                    "AND\t\tTOD.YEAR = '[$1]'\n" +
                    "AND\t\tTOD.MON\t=\t'[$2]'\n" +
                    "AND\t\tTO_NUMBER(TOD.DAY) <= TO_NUMBER('[$3]') " +
                    "AND EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM THR_OT_MASTER TOM\n" +
                    "    WHERE TOM.YEAR = TOD.YEAR\n" +
                    "      AND TOM.MON = TOD.MON\n" +
                    "      AND TOM.USER_SID = TOD.USER_SID\n" +
                    "      AND TOM.USABLE_FLAG = 'Y' "+
                    "      AND NVL(TOM.CLOSE_FLAG, 'N') = 'Y'\n" +
                    ")";
            String targetDate = dateStr;
            LocalDate date = LocalDate.parse(dateStr, formatter);
            if(flag){
                targetDate = date.with(TemporalAdjusters.lastDayOfMonth())
                        .format(formatter);
            }else{
                LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
                LocalDate end = start.plusDays(6);

                targetDate = end.getYear() == date.getYear()
                        && end.getMonthValue() == date.getMonthValue()
                        ? end.format(formatter)
                        : null;
                if(targetDate == null){
                    throw new BizException("setAutoSchAppr","처리가능 날짜 없음 ( 마지막날짜 : "+end.format(formatter)+" )");
                }
            }

            String yyyy = targetDate.substring(0,4);
            String mon = targetDate.substring(4,6);
            String day = targetDate.substring(6,8);

            sql = sql.replace("[$1]",yyyy);
            sql = sql.replace("[$2]",mon);
            sql = sql.replace("[$3]",day);

            dbRet = repo.callSql(sql);

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("setAutoSchAppr",dbRet.getErrMsg());
            }

            if(dbRet.getResult().get(0).isEmpty()){
                throw new BizException("setAutoSchAppr","처리할 프로세스 없음.");
            }

            List<Map<String,DbTypeDTO>> dbArray = dbRet.getResult().get(0);

            sql = " SELECT\tTUM.USER_ID " +
                    " FROM \tTCM_INTRA_USER_RELATION\tIUR\n" +
                    " JOIN\tTCM_USER_MASTER\tTUM\n" +
                    " ON\t\tTUM.USER_SID = IUR.USER_SID\n" +
                    " AND\tTUM.USABLE_FLAG = 'Y'\n" +
                    " JOIN\tTCM_CODE_MASTER\t\tTCM\n" +
                    " ON\t\tTCM.CLASS_CODE = 'HRMTR'\n" +
                    " AND\tTCM.CODE_CODE = IUR.VALUE1\n" +
                    " AND\tTCM.USABLE_FLAG = 'Y'\n" +
                    " WHERE\tIUR.CLASS_CODE = 'HRTAU'\n" +
                    " AND\tIUR.VALUE2 = '10'\n" +
                    " AND IUR.USABLE_FLAG = 'Y'" +
                    " AND\tIUR.CODE_CODE = '[$1]' " +
                    "AND (\n" +
                    "            (TCM.VALUE1_CHAR IS NULL AND IUR.VALUE1 = '[$2]') "+
                    "            OR\n" +
                    "            (TCM.VALUE1_CHAR IS NOT NULL AND INSTR(TCM.VALUE1_CHAR, '[$2]') > 0)\n" +
                    "        )";

            Map<WorkDTO.AutoKey,String> approveMap = new HashMap<>();

            for(Map<String,DbTypeDTO> row: dbArray){
                String rowYyyy = Util.getStrChk(row.get("YEAR").getObj());
                String rowMon = Util.getStrChk(row.get("MON").getObj());
                String rowDay = Util.getStrChk(row.get("DAY").getObj());
                String rowTerminalCode = Util.getStrChk(row.get("TERMINAL_CODE").getObj());
                String rowTeamCode = Util.getStrChk(row.get("TEAM_CODE").getObj());
                BigDecimal rowUserSid = Util.getDecimal(row.get("USER_SID").getObj());
                BigDecimal rowSeq = Util.getDecimal(row.get("SEQ").getObj());

                String approveId = approveMap.get(new WorkDTO.AutoKey(rowTeamCode,rowTerminalCode));

                if(approveId == null){
                    String tmpSql = sql;
                    tmpSql = tmpSql.replace("[$1]",rowTeamCode);
                    tmpSql = tmpSql.replace("[$2]",rowTerminalCode);
                    dbRet = repo.callSql(tmpSql);

                    if(dbRet.getErrFlag().equals("Y")){
                        throw new BizException("setAutoSchAppr",dbRet.getErrMsg());
                    }
                    if(dbRet.getResult().get(0).isEmpty()){
                        throw new BizException("setAutoSchAppr",rowTeamCode+" : "+ rowTerminalCode+" 권한을 가진 팀장이 없습니다.");
                    }
                    approveId = Util.getStrChk(dbRet.getResult().get(0).get(0).get("USER_ID").getObj());
                    approveMap.put(new WorkDTO.AutoKey(rowTeamCode,rowTerminalCode),approveId);

                }

                dbRet = repo.setWorkL010_015(rowYyyy,rowMon,rowDay,rowUserSid,rowSeq,"KOR",Util.getGUID(),approveId,"DAEMON","DAEMON");

                if(dbRet.getErrFlag().equals("Y")){
                    throw new BizException("setAutoSchAppr",dbRet.getErrMsg());
                }
            }

            return okOrThrow("setAutoSchAppr", dbRet);
        });
    }
    @Override
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
    @Override
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
    @Override
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

            dbRet = repo.getWorkL010_005(date, teamCode, terminalCode, info.getUserLang(), Util.getGUID(),
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
    @Override
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
    @Override
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

                    Map<String,Object> downName = resFilter.stream().filter(v->v.get("VALUE1_CHAR").equals("FORM3")).findFirst().orElse(null);


                    int upCount = 0;
                    for(Map.Entry<String, Object> entry : upLine.entrySet()){
                        if(entry.getKey().contains("CHAR")&&!Util.getStrChk(entry.getValue()).isEmpty()&&!entry.getKey().contains("VALUE1")){
                            upCount++;
                        }
                    }
                    int downCount = 0;
                    if(downLine!=null){
                        for(Map.Entry<String, Object> entry : downLine.entrySet()){
                            if(entry.getKey().contains("CHAR")&&!Util.getStrChk(entry.getValue()).isEmpty()&&!entry.getKey().contains("VALUE1")){
                                downCount++;
                            }
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
                    Cell cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("시간 외 근무수당 신청서( "+dto.getTeamName()+" )");
                    row = copySheet.getRow(1);
                    //결재라인 수정
                    if(upCount==2){

                        for(int i = 1;i<=upCount;i++){
                            cell = row.getCell(9+i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(Util.getStrChk(upLine.get("VALUE"+(i+1)+"_CHAR")));
                        }
                    }else if(upCount == 3){
                        int[] approvalCols = {10, 12, 13};

                        for (int i = 0; i < approvalCols.length; i++) {
                            cell = row.getCell(
                                    approvalCols[i],
                                    Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                            );
                            cell.setCellValue(
                                    Util.getStrChk(upLine.get("VALUE" + (i + 2) + "_CHAR"))
                            );
                        }
                    }else{
                        throw new BizException("getWorkTime", upCount+"개수의 결제라인은 없습니다.");
                    }

                    row = copySheet.getRow(4);
                    cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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
                    int nameCol = upCount == 2 ? 10 : 11; // FORM2: K, FORM3: L
                    int signCol = upCount == 2 ? 11 : 13; // FORM2: L, FORM3: N
                    int signRowIdx = selectIdx + dataCount + 2;

                    row = copySheet.getRow(signRowIdx);

// FORM3: 이름 영역 L:M 병합
                    if (upCount == 3) {
                        // 템플릿에 이미 같은 병합이 있으면 중복 추가하지 않음
                        boolean alreadyMerged = false;

                        for (CellRangeAddress region : copySheet.getMergedRegions()) {
                            if (region.getFirstRow() == signRowIdx
                                    && region.getLastRow() == signRowIdx
                                    && region.getFirstColumn() == 11
                                    && region.getLastColumn() == 12) {
                                alreadyMerged = true;
                                break;
                            }
                        }

                        if (!alreadyMerged) {
                            copySheet.addMergedRegion(
                                    new CellRangeAddress(signRowIdx, signRowIdx, 11, 12)
                            );
                        }
                    }

                    cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("팀 장 :");

                    // 이름: 병합 영역의 첫 셀인 L에 입력
                    cell = row.getCell(nameCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getApproveName());

                    cell = row.getCell(signCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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
                        anchor.setCol1(signCol);
                        anchor.setCol2(signCol + 1);
                        anchor.setRow1(signRowIdx);
                        anchor.setRow2(signRowIdx + 1);

                        anchor.setDx1(Units.toEMU(10));
                        anchor.setDy1(Units.toEMU(2));
                        anchor.setDx2(Units.toEMU(0));
                        anchor.setDy2(Units.toEMU(-2));

                        drawing.createPicture(anchor, pictureIdx);
                    }
                    if (downLine != null) {
                        for (int i = 1; i <= downCount; i++) {
                            int rowIdx = selectIdx + dataCount + 2 + i;

                            row = copySheet.getRow(rowIdx);
                            if (row == null) {
                                row = copySheet.createRow(rowIdx);
                            }

                            // FORM3: 이름 영역 L:M 병합
                            if (upCount == 3) {
                                boolean alreadyMerged = false;

                                for (CellRangeAddress region : copySheet.getMergedRegions()) {
                                    if (region.getFirstRow() == rowIdx
                                            && region.getLastRow() == rowIdx
                                            && region.getFirstColumn() == 11
                                            && region.getLastColumn() == 12) {
                                        alreadyMerged = true;
                                        break;
                                    }
                                }

                                if (!alreadyMerged) {
                                    copySheet.addMergedRegion(
                                            new CellRangeAddress(rowIdx, rowIdx, 11, 12)
                                    );
                                }
                            }

                            // 직책
                            cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(
                                    Util.getStrChk(downLine.get("VALUE" + (i + 1) + "_CHAR")) + " :"
                            );

                            // 이름
                            cell = row.getCell(nameCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(
                                    downName == null
                                            ? ""
                                            : Util.getStrChk(downName.get("VALUE" + (i + 1) + "_CHAR"))
                            );

                            // (인)
                            cell = row.getCell(signCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue("(인)");
                            cell.setCellStyle(rightStyle);
                        }
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
    @Override
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

                sql.append("SELECT SUM(TOT.ADD_WORK_HOUR) SUM_ADD_HOUR ");
                sql.append("     , SUM(TOT.NIGHT_WORK_HOUR) SUM_NIGHT_HOUR ");
                sql.append("     , SUM(TOT.HOLIDAY_WORK_HOUR + TOT.HOLIDAY_ADD_HOUR) SUM_HOLIDAY_HOUR ");
                sql.append("     , ODR.CODE_CODE TEAM_CODE ");
                sql.append("     , SYS_FUNCTION.FCM_GET_CODE_NAME_BY_AK1('HRPAT', ODR.CODE_CODE, 'KOR') TEAM_NAME ");
                sql.append("     , TOD.YEAR||TOD.MON    YYYYMM ");
                sql.append("FROM THR_OT_DETAIL TOD ");
                sql.append("JOIN THR_OT_TIME TOT ");
                sql.append("  ON TOD.YEAR = TOT.YEAR ");
                sql.append(" AND TOD.MON = TOT.MON ");
                sql.append(" AND TOD.DAY = TOT.DAY ");
                sql.append(" AND TOD.USER_SID = TOT.USER_SID ");
                sql.append(" AND TOD.SEQ = TOT.SEQ ");
                sql.append(" AND TOT.USABLE_FLAG = 'Y' ");
                sql.append("JOIN THR_OT_DETAIL_REF ODR ");
                sql.append("  ON TOD.YEAR = ODR.YEAR ");
                sql.append(" AND TOD.MON = ODR.MON ");
                sql.append(" AND TOD.DAY = ODR.DAY ");
                sql.append(" AND TOD.USER_SID = ODR.USER_SID ");
                sql.append(" AND TOD.SEQ = ODR.SEQ ");
                sql.append(" AND ODR.USABLE_FLAG = 'Y' ");
                sql.append(" AND ODR.CLASS_CODE = 'HRPAT' ");
                sql.append("JOIN TCM_CODE_MASTER TCM ");
                sql.append("  ON TCM.CODE_CODE = TOD.DETAIL_STATUS ");
                sql.append(" AND TCM.USABLE_FLAG = 'Y' ");
                sql.append(" AND TCM.CLASS_CODE = 'HRREQ' ");
                sql.append("WHERE TOD.USABLE_FLAG = 'Y' ");
                sql.append("  AND TOD.YEAR || TOD.MON IN ('").append(date).append("','").append(prevMonth).append("') ");
                sql.append("  AND TCM.VALUE6_CHAR = 'SCH_COM' ");
                sql.append("GROUP BY ODR.CODE_CODE, TOD.YEAR, TOD.MON ");

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
                        cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String value = cell.getStringCellValue();
                        value = value.replace("{$1}",mon.toString());
                        cell.setCellValue(value);

                        row = copySheet.getRow(9);
                        cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = cell.getStringCellValue();
                        value = value.replace("{$1}",beforeMon.toString());
                        cell.setCellValue(value);

                        cell = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = cell.getStringCellValue();
                        value = value.replace("{$1}",mon.toString());
                        cell.setCellValue(value);

                        row = copySheet.getRow(19);
                        cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = cell.getStringCellValue();
                        value = value.replace("{$1}",mon.toString());
                        cell.setCellValue(value);

                        LocalDateTime now = LocalDateTime.now();

                        String currentDate = now.format(
                                DateTimeFormatter.ofPattern("yyyy. MM. dd")
                        );
                        row = copySheet.getRow(32);
                        cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(currentDate);


                        row = copySheet.getRow(3);

                        if(lineLength == 3){
                            for(int i =0;i<lineLength;i++){
                                cell = row.getCell(12+i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(dto.getGroupLine()[i]);
                            }
                        }else if(lineLength == 4){
                            for(int i =0;i<lineLength;i++){
                                if(i==0){
                                    cell = row.getCell(11+i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                }else{
                                    cell = row.getCell(12+i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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
                    Cell cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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
                                int mergeSize = rowList.getCellList().size();

                                if (mergeSize > 1) {
                                    copySheet.addMergedRegion(
                                            new CellRangeAddress(
                                                    sumRowIdx,
                                                    sumRowIdx + mergeSize - 1,
                                                    1, // B열
                                                    1
                                            )
                                    );
                                }
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
                    cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getTeamName()+" 합계");
                    cell = row.getCell(7,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getSumAddHour());
                    cell = row.getCell(8,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getSumNightHour());
                    cell = row.getCell(9,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(dto.getSumHoliHour());
                    cell = row.getCell(10,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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

    @Override
    public ResponseDTO<?> getWorkL010_031(String yyyymm) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;

            dbDto = repo.getWorkL010_031(yyyymm,info.getUserSid(),info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

            if(dbDto.getErrFlag().equals("Y")){
                throw new BizException("getWorkL010_031",dbDto.getErrMsg());
            }

            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    @Override
    public  ResponseDTO<?> getWorkM010_004(String date,BigDecimal userSid,BigDecimal seq){
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;
            String yyyy = date.substring(0,4);
            String mon = date.substring(4,6);
            String day = Util.getInteger(date.substring(6,8)).toString();
            dbDto = repo.getWorkM010_004(yyyy,mon,day,userSid,seq,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

            if(dbDto.getErrFlag().equals("Y")){
                throw new BizException("getWorkM010_004",dbDto.getErrMsg());
            }

            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    @Override
    public ResponseDTO<?> setWorkM010_014_1(WorkDTO.NightTransSaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate tmpDate = LocalDate.of(
                    Integer.parseInt(dto.year()),
                    Integer.parseInt(dto.mon()),
                    Integer.parseInt(dto.day())
            );

            String startDate = tmpDate.format(formatter);
            String endDate = tmpDate.plusDays(dto.addDay()).format(formatter);

            ResponseDTO<CapsTimeDTO.CapsRangeResult> startSet = capsServiceImpl.findDateToId(CapsGetType.START,dto.userId(),startDate,dto.startTime());
            if(startSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", startSet.getErrMsg());
            }

            ResponseDTO<CapsTimeDTO.CapsRangeResult> endSet = capsServiceImpl.findDateToId(CapsGetType.END,dto.userId(),endDate,dto.endTime());
            if(endSet.getErrFlag().equals("Y")){
                throw new BizException("findDateToId", endSet.getErrMsg());
            }

            dbDto = repo.setWorkM010_014(dto.year(),dto.mon(),dto.day(),dto.userSid(),dto.seq(),dto.code(),dto.value1()
                    ,dto.remark(),startSet.getData().orgTime(),endSet.getData().orgTime()
                    ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

            if(dbDto.getErrFlag().equals("Y")){
                throw new BizException("setWorkM010_014_1",dbDto.getErrMsg());
            }

            if (dto.files() == null || dto.files().isEmpty()) {
                throw new BizException("setWorkM010_014_1", "서명이 없습니다.");
            }
            ResponseDTO<?> saveRet = fileClientService.fileSave(dto.files());

            if (saveRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_014_1", "파일저장실패 : " + saveRet.getErrMsg());
            }

            List<FileMeta> metaRet = Util.castIfMatch(saveRet.getData(), new TypeReference<List<FileMeta>>() {
            });

            if (metaRet == null || metaRet.isEmpty()) {
                throw new BizException("setWorkM010_014_1", "파일변환실패");
            }

            BigDecimal fileSize = Util.getDecimal(metaRet.get(0).getSize());

            if (fileSize.compareTo(BigDecimal.ZERO) == 0) {
                throw new BizException("setWorkM010_014_1", "파일사이즈 문제");
            }

            dbDto = repo.setWorkM010_018(dto.year(),dto.mon(),dto.day(),dto.userSid(), dto.seq(),BigDecimal.ONE, "NIGHT",
                    metaRet.get(0).getDir(), metaRet.get(0).getOriginName(), metaRet.get(0).getChangeName(), metaRet.get(0).getFullpath()
                    , fileSize, metaRet.get(0).getMime(), metaRet.get(0).getExt(), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    @Override
    public ResponseDTO<?> getWorkL010_032(String date, String teamCode, String terminalCode, String userName, String approveFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;
            String yyyy = date.substring(0,4);
            String mon = date.substring(4,6);
            dbDto = repo.getWorkL010_032(yyyy,mon,teamCode,terminalCode,userName,approveFlag
                    ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

            if(dbDto.getErrFlag().equals("Y")){
                throw new BizException("getWorkL010_032",dbDto.getErrMsg());
            }

            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    @Override
    public ResponseDTO<?> setWorkL010_019(WorkDTO.NightApprDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;

            if(dto.array()==null||dto.array().isEmpty()){
                throw new BizException("setWorkL010_019","확정 항목이 없습니다.");
            }

            for(WorkDTO.WorkDefaultDTO row:dto.array()){
                dbDto = repo.setWorkL010_019(row.year(),row.mon(),row.day(),row.userSid(),row.seq()
                        ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

                if(dbDto.getErrFlag().equals("Y")){
                    throw new BizException("setWorkL010_019",dbDto.getErrMsg());
                }
            }


            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    @Override
    public ResponseDTO<?> getNightWorkIns(String date) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbDto = null;
            Row row = null;
            Cell cell = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
            Path tempDir = Paths.get(
                    System.getProperty("user.dir"),
                    ".temp",
                    UUID.randomUUID().toString()
            );

            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/NIGHT_INS_TEMPLATE.xlsx");
            if (ret.getErrFlag().equals("Y")) {
                throw new BizException("getNightWorkIns", ret.getErrMsg());
            }

            ResponseDTO<List<Map<String, Object>>> res = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRCOS")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(res.getErrFlag().equals("Y")){
                throw new BizException("getNightWorkIns", res.getErrMsg());
            }

            dbDto = repo.setWorkM010_005(date.substring(0,4),date.substring(4,6),info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if(dbDto.getErrFlag().equals("Y")){
                throw new BizException("getNightWorkIns", dbDto.getErrMsg());
            }

            Map<BigDecimal,List<Map<String,Object>>> dbRet = convNightData(dbDto);
            List<Map<String,DbTypeDTO>> dbRet2 = dbDto.getResult().get(1);

            if(dbRet.isEmpty()||dbRet2.isEmpty()){
                throw new BizException("getNightWorkIns", "심야교통비 출력할 목록이 없습니다.");
            }

            res.getData().sort(Comparator.comparingInt((item->Integer.parseInt(Util.getStrChk(item.get("VALUE1_CHAR"),"0")))));
            Map<String,String> hrcosMap = new LinkedHashMap<>();
            for(Map<String,Object> hrcosRow : res.getData()){
                String amount = Util.getStrChk(hrcosRow.get("VALUE1_CHAR"),"0");
                String tmpRegion = Util.getStrChk(hrcosRow.get("CODE_NAME"));
                hrcosMap.merge(amount, tmpRegion, (oldValue, newValue) -> oldValue + ", " + newValue);
            }

            Map<BigDecimal,List<Path>> pathArray = new LinkedHashMap<>();

            try{
                Files.createDirectories(tempDir);

                for(int i = 0;i<dbRet2.size();i++){
                    Map<String,DbTypeDTO> dbRow2 = dbRet2.get(i);

                    try(XSSFWorkbook srcWorkbook = new XSSFWorkbook(new ByteArrayInputStream(ret.getData()));
                        ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

                        XSSFSheet formSheet = srcWorkbook.getSheet("SHEET");
                        if(formSheet == null){
                            throw new BizException("getNightWorkIns", "Templete SHEET 기본 시트없음");
                        }

                        BigDecimal tmpUserSid = Util.getDecimal(dbRow2.get("USER_SID").getObj());
                        String tmpApproveName = Util.getStrChk(dbRow2.get("USER_NAME").getObj());
                        byte[] approveSign = (byte[]) dbRow2.get("DATA").getObj();
                        String tmpSignPath = Util.getStrChk(dbRow2.get("FULL_PATH").getObj());
                        String createdTime = Util.getStrChk(dbRow2.get("CREATED_TIME").getObj());

                        LocalDate createdTime2 = LocalDate.parse(createdTime, formatter);
                        createdTime = formatter2.format(createdTime2);

                        //작성일자 셋팅
                        row = formSheet.getRow(5);
                        cell = row.getCell(7);
                        cell.setCellValue("작성일자 : "+createdTime);

                        int idx = 10;
                        int seq = 1;

                        if (hrcosMap.size() > 0) {
                            formSheet.shiftRows(
                                    idx,
                                    formSheet.getLastRowNum(),
                                    hrcosMap.size()-1,
                                    true,
                                    false
                            );
                        }
                        //지역별 지급 금액 수정
                        for (Map.Entry<String, String> entry : hrcosMap.entrySet()) {
                            String amount = entry.getKey();
                            String region = entry.getValue();

                            if(seq!=hrcosMap.entrySet().size()){
                                copyRowStyleFast(formSheet,10+hrcosMap.size()-1, idx);
                            }
                            row = formSheet.getRow(idx);
                            cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(seq+") "+region+" : "+String.format("%,d", Integer.parseInt(amount))+"원");

                            idx++;
                            seq++;

                        }

                        //본문작성
                        int originIdx = idx+5;

                        List<Map<String, Object>> list  =   dbRet.get(tmpUserSid);

                        if(list == null || list.isEmpty()){
                            continue;
                        }

                        Map<String, Object> lastRow = list.get(list.size() - 1);
                        ResponseDTO<byte[]> signRet = fileClientService.fileRead(tmpSignPath);

                        if(signRet.getErrFlag().equals("Y")){
                            throw new BizException("getNightWorkIns",signRet.getErrMsg());
                        }
                        //하단 신청인, 팀장 수정
                        int insertSignIdx = originIdx+10;
                        int approveSignIdx = originIdx+12;

                        row = formSheet.getRow(insertSignIdx);
                        cell = row.getCell(8,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                        cell.setCellValue(Util.getStrChk(lastRow.get("USER_NAME")));

                        row = formSheet.getRow(approveSignIdx);
                        cell = row.getCell(8,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(tmpApproveName);

                        if(list.size()>3){
                            int addCount = list.size() - 3;
                            formSheet.shiftRows(
                                    originIdx,
                                    formSheet.getLastRowNum(),
                                    addCount,
                                    true,
                                    false
                            );

                            CellCopyPolicy policy = new CellCopyPolicy.Builder()
                                    .cellStyle(true)
                                    .cellValue(true)
                                    .cellFormula(true)
                                    .mergedRegions(true)
                                    .rowHeight(true)
                                    .build();

                            for (int j = 0; j < addCount; j++) {
                                formSheet.copyRows(
                                        list.size()+idx+4,
                                        list.size()+idx+4,
                                        originIdx + j,
                                        policy
                                );
                            }
                        }



                        int insSeq = 1;
                        BigDecimal sumAmount = BigDecimal.ZERO;
                        for(Map<String,Object> entryRow : list){

                            String userName = Util.getStrChk(entryRow.get("USER_NAME"));
                            String timeDate = Util.getStrChk(entryRow.get("TIME_DATE"));
                            String startTime = Util.getStrChk(entryRow.get("START_TIME"));
                            String endTime = Util.getStrChk(entryRow.get("END_TIME"));
                            int addDay = Util.getInteger(entryRow.get("ADD_DAY"));

                            String workTime = startTime+" ~ "+endTime;

                            if(addDay!=0){
                                workTime = workTime+"+"+addDay;
                            }


                            String remark = Util.getStrChk(entryRow.get("REMARK"));
                            String region = Util.getStrChk(entryRow.get("REGION"));
                            String amount = String.format("%,d", Util.getInteger(entryRow.get("AMOUNT")));
                            String cerPath =  Util.getStrChk(entryRow.get("FULL_PATH"));
                            BigDecimal amountDec = Util.getDecimal(entryRow.get("AMOUNT"));
                            sumAmount = sumAmount.add(amountDec);

                            row = formSheet.getRow(originIdx);
                            cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(insSeq);

                            cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(userName);

                            cell = row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(timeDate);

                            cell = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(workTime);

                            cell = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(remark);

                            cell = row.getCell(5,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(region);

                            cell = row.getCell(8,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(amount);

                            if(!cerPath.isEmpty()){
                                ResponseDTO<byte[]> cerRet = fileClientService.fileRead(cerPath);

                                if(cerRet.getErrFlag().equals("Y")){
                                    throw new BizException("getNightWorkIns",cerRet.getErrMsg());
                                }

                                byte[] cerPdf = cerRet.getData();

                                String pdfFileName  =   tmpUserSid.toPlainString()+"_"+insSeq+".pdf";

                                Path pdfPath = tempDir.resolve(pdfFileName);
                                Files.write(pdfPath, cerPdf);

                                pathArray.computeIfAbsent(tmpUserSid, k -> new ArrayList<>()).add(pdfPath);

                            }

                            originIdx++;
                            insSeq++;

                        }

                        row = formSheet.getRow(list.size()>3?originIdx:22);
                        cell = row.getCell(8);
                        cell.setCellValue(  new DecimalFormat("#,##0").format(sumAmount));

                        CreationHelper helper = srcWorkbook.getCreationHelper();
                        Drawing<?> drawing = formSheet.createDrawingPatriarch();

                        // ===========================
                        // 1. 첫 번째 서명 이미지
                        // ===========================

                        int pictureIdx = srcWorkbook.addPicture(
                                approveSign,
                                Workbook.PICTURE_TYPE_PNG
                        );

                        ClientAnchor anchor1 = helper.createClientAnchor();

                        anchor1.setCol1(9);
                        anchor1.setCol2(10);
                        anchor1.setRow1(list.size()>3?originIdx + 11:31);
                        anchor1.setRow2(list.size()>3?originIdx + 12:32);

                        anchor1.setDx1(Units.toEMU(10));
                        anchor1.setDy1(Units.toEMU(2));
                        anchor1.setDx2(0);
                        anchor1.setDy2(0);

                        drawing.createPicture(anchor1, pictureIdx);


                        // ===========================
                        // 2. 두 번째 서명 이미지
                        // ===========================

                        pictureIdx = srcWorkbook.addPicture(
                                signRet.getData(),
                                Workbook.PICTURE_TYPE_PNG
                        );

                        ClientAnchor anchor2 = helper.createClientAnchor();

                        anchor2.setCol1(9);
                        anchor2.setCol2(10);
                        anchor2.setRow1(list.size()>3?originIdx + 9:29);
                        anchor2.setRow2(list.size()>3?originIdx + 10:30);

                        anchor2.setDx1(Units.toEMU(10));
                        anchor2.setDy1(Units.toEMU(2));
                        anchor2.setDx2(0);
                        anchor2.setDy2(0);

                        drawing.createPicture(anchor2, pictureIdx);
                        //pdf 셋팅
                        formSheet.setFitToPage(true);
                        formSheet.getPrintSetup().setFitWidth((short) 1);
                        formSheet.getPrintSetup().setFitHeight((short) 0);


                        String fileName = tmpUserSid.toPlainString() + ".xlsx";

                        Path excelPath = tempDir.resolve(fileName);

                        srcWorkbook.write(bos);

                        Files.write(excelPath, bos.toByteArray());

                        Path pdfPath = excelToPdf(excelPath,tempDir);

                        pathArray.computeIfAbsent(
                                tmpUserSid,
                                k -> new ArrayList<>()
                        ).add(0, pdfPath);

                    }catch (Exception ex){
                        throw new BizException("getNightWorkIns", ex.getMessage());
                    }
                }

                PDFMergerUtility merger = new PDFMergerUtility();

                for (List<Path> pdfList : pathArray.values()) {
                    for (Path pdfPath : pdfList) {
                        merger.addSource(pdfPath.toFile());
                    }
                }

                Path finalPdfPath = tempDir.resolve("final.pdf");

                merger.setDestinationFileName(finalPdfPath.toString());

                merger.mergeDocuments(
                        IOUtils.createTempFileOnlyStreamCache()
                );
                byte[] pdfBytes = Files.readAllBytes(finalPdfPath);
                ret = ResponseDTO.<byte[]>builder().errFlag("N").errMsg("심야교통비 신청서 다운완료").data(pdfBytes).build();

            } catch (Exception e) {
                throw new BizException("getNightWorkIns",e.getMessage());
            }finally {
                deleteTempDirectory(tempDir);
            }


            return ret;
        });
    }

    private void deleteTempDirectory(Path tempDir) {

        if (tempDir == null || !Files.exists(tempDir)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(tempDir)) {

            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            ResponseDTO.setError2("deleteTempDirectory",e.getMessage());
                        }
                    });

        } catch (IOException e) {
            ResponseDTO.setError2("deleteTempDirectory",e.getMessage());
        }
    }

    public Path excelToPdf(Path excelPath, Path outputDir)
            throws Exception {

        String soffice =
                "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

        ProcessBuilder pb = new ProcessBuilder(
                soffice,
                "--headless",
                "--convert-to", "pdf:calc_pdf_Export",
                "--outdir", outputDir.toString(),
                excelPath.toString()
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        // 프로세스 출력 버퍼가 막히지 않도록 읽기
        String output;
        try (InputStream in = process.getInputStream()) {
            output = new String(
                    in.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        int exitCode = process.waitFor();

        String fileName = excelPath.getFileName().toString();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));

        Path pdfPath = outputDir.resolve(fileName + ".pdf");

        if (exitCode != 0 || !Files.exists(pdfPath)) {
            throw new IOException("PDF 변환 실패: " + output);
        }

        return pdfPath;
    }

    @Override
    public ResponseDTO<?> getNightDetail(String date) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            DbDto dbDto = null;
            Row row = null;
            Cell cell = null;
            String yyyy = date.substring(0,4);
            String mon = date.substring(4,6);
            DecimalFormat df = new DecimalFormat("#,###");

            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/NIGHT_DETAIL_FORM.xlsx");
            if (ret.getErrFlag().equals("Y")) {
                throw new BizException("getNightDetail", ret.getErrMsg());
            }

            ResponseDTO<List<Map<String, Object>>> res = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRDOC")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(res.getErrFlag().equals("Y")){
                throw new BizException("getNightDetail", res.getErrMsg());
            }

            ResponseDTO<List<Map<String, Object>>> hrcos = clientService.get(ClientName.SYS,uriBuilder -> uriBuilder
                    .path("/sys/getBaseOds")
                    .queryParam("classCode", "HRCOS")
                    .queryParam("codeName", "")
                    .build(),new ParameterizedTypeReference<ResponseDTO<List<Map<String, Object>>>>() {});
            if(res.getErrFlag().equals("Y")){
                throw new BizException("getNightDetail", res.getErrMsg());
            }

            List<Map<String, Object>> hrcosArray = hrcos.getData();

            dbDto = repo.setWorkM010_006(date.substring(0,4),date.substring(4,6),info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if(dbDto.getErrFlag().equals("Y")){
                throw new BizException("getNightDetail", dbDto.getErrMsg());
            }

            if(dbDto.getResult().get(1).isEmpty()||dbDto.getResult().get(0).isEmpty()){
                throw new BizException("getNightDetail", "출력할 자료가 없습니다.");
            }

            List<Map<String,DbTypeDTO>> detailList = dbDto.getResult().get(0);

            int calLength = dbDto.getResult().get(2).size();

            Map<String, List<ExcelDTO.NightDetailHeaderDTO>> headerGroup =    convHeaderGroup(res.getData(),dbDto.getResult().get(1));

            try(XSSFWorkbook srcWorkbook = new XSSFWorkbook(new ByteArrayInputStream(ret.getData()));
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

                CellCopyPolicy policy = new CellCopyPolicy.Builder()
                        .cellStyle(true)
                        .cellValue(true)
                        .cellFormula(true)
                        .mergedRegions(true)
                        .rowHeight(true)
                        .build();

                //지급 명단 부분
                XSSFSheet formSheet = srcWorkbook.getSheet("SHEET");
                int sheetIndex = srcWorkbook.getSheetIndex(formSheet);
                srcWorkbook.setSheetName(sheetIndex, mon+"월 지급명단");
                //제목셋팅
                row = formSheet.getRow(0);
                cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(yyyy+"년 "+mon+"월 심야교통비 지급 명단");

                int totalCount = headerGroup.values().stream()
                        .mapToInt(List::size)
                        .sum();

                if(totalCount > 1){
                    formSheet.shiftRows(
                            3,
                            formSheet.getLastRowNum(),
                            totalCount-1,
                            true,
                            false
                    );
                    for(int i = 0;i<totalCount-1;i++){
                        formSheet.copyRows(
                                totalCount+2,
                                totalCount+2,
                                3+i,
                                policy
                        );
                    }
                }

                int seq = 1;
                int srcIdx = 3;
                BigDecimal sumAmount = BigDecimal.ZERO;
                for(Map.Entry<String,List<ExcelDTO.NightDetailHeaderDTO>> entryRow:headerGroup.entrySet()){
                    String teamName = entryRow.getKey();
                    List<ExcelDTO.NightDetailHeaderDTO> value = entryRow.getValue();
                    if(value == null||value.isEmpty()){
                        continue;
                    }

                    //수당 내역부분
                    XSSFSheet formSheet2 = srcWorkbook.getSheet("SHEET2");
                    XSSFSheet copySheet = srcWorkbook.cloneSheet(
                            srcWorkbook.getSheetIndex(formSheet2)
                    );

                    srcWorkbook.setSheetName(
                            srcWorkbook.getSheetIndex(copySheet),
                            mon+"월 수당내역("+teamName+")"
                    );

                    //세부내역 부분
                    XSSFSheet formSheet3 = srcWorkbook.getSheet("SHEET3");
                    XSSFSheet copySheet2 = srcWorkbook.cloneSheet(
                            srcWorkbook.getSheetIndex(formSheet3)
                    );

                    srcWorkbook.setSheetName(
                            srcWorkbook.getSheetIndex(copySheet2),
                            "세부내역("+teamName+")");
                    //시트 총함
                    BigDecimal bodySumAmount = BigDecimal.ZERO;

                    row = copySheet.getRow(0);
                    cell = row.getCell(0);
                    cell.setCellValue(mon+"월 심야교통비 수당내역("+teamName+")");

                    //월 개수만큼 열 숨기기
                    int monLength=31-calLength;
                    for(int i =0;i<monLength;i++){
                        copySheet.setColumnHidden(32-i, true);
                    }

                    //시트별 총개수만큼 행생성
                    int payCount = value.stream()
                            .mapToInt(dto -> dto.getCount() == null ? 0 : dto.getCount())
                            .sum();
                    copySheet.shiftRows(
                            5,
                            copySheet.getLastRowNum(),
                            payCount,
                            true,
                            false
                    );

                    //지급내역셋팅
                    int startIdx = srcIdx;
                    String terminal = "";

                    //수당내역셋팅
                    int copyIdx = 5;
                    int assingSeq = 1;
                    BigDecimal totalSumAmount = BigDecimal.ZERO;

                    //세부내역 셋팅
                    row = copySheet2.getRow(0);
                    cell = row.getCell(0);
                    cell.setCellValue(mon+"월 심야 교통비 개인별 세부내역 ("+teamName+")");

                    int depTotalCount = value.stream()
                            .mapToInt(dto -> dto.getTotalCount() == null ? 0 : dto.getTotalCount())
                            .sum();
                    copySheet2.shiftRows(
                            4,
                            copySheet2.getLastRowNum(),
                            depTotalCount,
                            true,
                            false
                    );
                    int bodyRowIdx = 4;
                    //근무자 헤더 순환
                    for(ExcelDTO.NightDetailHeaderDTO dtoRow:value){
                        //지급명단 부분
                        row = formSheet.getRow(srcIdx);
                        cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(seq);
                        cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(teamName);
                        cell = row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(dtoRow.getUserName());
                        cell = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(dtoRow.getRegion());
                        cell = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        BigDecimal amount = Util.getDecimal(dtoRow.getAmount());
                        sumAmount = sumAmount.add(amount);
                        cell.setCellValue(df.format(amount));
                        if (!terminal.isEmpty()
                                && !terminal.equals(dtoRow.getTerminalCode())) {

                            if (startIdx < srcIdx - 1) {
                                formSheet.addMergedRegion(
                                        new CellRangeAddress(startIdx, srcIdx - 1, 1, 1)
                                );
                            }

                            startIdx = srcIdx;
                        }

                        terminal = dtoRow.getTerminalCode();
                        srcIdx++;
                        //수당내역 처리
                        for(int i=0;i<dtoRow.getCount();i++){
                            if(i==0){
                                copySheet.copyRows(
                                        payCount+5,
                                        payCount+5,
                                        copyIdx,
                                        policy
                                );
                            }else if(i==dtoRow.getCount()-1)
                            {
                                copySheet.copyRows(
                                        payCount+7,
                                        payCount+7,
                                        copyIdx+i,
                                        policy
                                );
                            }else
                            {
                                copySheet.copyRows(
                                        payCount+6,
                                        payCount+6,
                                        copyIdx+i,
                                        policy
                                );
                            }
                        }
                        BigDecimal detailSumAmount = BigDecimal.ZERO;
                        List<Map<String,DbTypeDTO>> filterDetail = detailList.stream()
                                .filter(v-> dtoRow.getUserSid().compareTo(Util.getDecimal(v.get("USER_SID").getObj()))==0)
                                .toList();
                        if(filterDetail.isEmpty()){
                            continue;
                        }

                        row = copySheet.getRow(copyIdx);
                        cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(assingSeq);
                        cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(dtoRow.getUserName());

                        int detailSeq = 0;
                        int count = 0;
                        BigDecimal beforeAmount = BigDecimal.ZERO;
                        int rowCount = 0;
                        boolean holidayFlag = false;
                        for(Map<String,DbTypeDTO> detailRow:filterDetail){
                            rowCount++;
                            int day = Util.getInteger(detailRow.get("DAY").getObj());
                            if(Util.getStrChk(detailRow.get("HOLIDAY_FLAG").getObj()).equals("Y")){
                                holidayFlag = true;
                            }
                            BigDecimal detaillAmount = Util.getDecimal(detailRow.get("AMOUNT").getObj());
                            detailSumAmount = detailSumAmount.add(detaillAmount);
                            totalSumAmount = totalSumAmount.add(detaillAmount);
                            if(beforeAmount.compareTo(detaillAmount)!=0&&beforeAmount.compareTo(BigDecimal.ZERO)!=0){
                                row = copySheet.getRow(copyIdx+detailSeq);
                                cell = row.getCell(33,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(count);
                                cell = row.getCell(34,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(df.format(beforeAmount));
                                detailSeq++;
                                count = 1;
                            }else{
                                count++;
                            }

                            row = copySheet.getRow(copyIdx+detailSeq);
                            cell = row.getCell(day+1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue("O");

                            if(rowCount==filterDetail.size()){
                                row = copySheet.getRow(copyIdx+detailSeq);
                                cell = row.getCell(33,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(count);
                                cell = row.getCell(34,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(df.format(detaillAmount));
                            }

                            beforeAmount = detaillAmount;
                        }
                        if(!holidayFlag){
                            Map<String,Object> hrcosSelect = hrcosArray.stream()
                                    .filter(v->"Y".equals(Util.getStrChk(v.get("VALUE3_CHAR")))).findFirst().orElse(null);
                            if(hrcosSelect == null){
                                throw new BizException("getNightDetail","심야교통비 코드에 휴일 교통비가 없습니다.");
                            }
                            row = copySheet.getRow(copyIdx+dtoRow.getCount()-1);
                            cell = row.getCell(33,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue("0");
                            cell = row.getCell(34,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            BigDecimal holiAmount = Util.getDecimal(hrcosSelect.get("VALUE1_CHAR"));
                            cell.setCellValue(df.format(holiAmount));
                        }

                        row = copySheet.getRow(copyIdx);
                        cell = row.getCell(35,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(df.format(detailSumAmount));

                        copySheet.addMergedRegion(
                                new CellRangeAddress(copyIdx, copyIdx+dtoRow.getCount()-1, 0, 0)
                        );
                        copySheet.addMergedRegion(
                                new CellRangeAddress(copyIdx, copyIdx+dtoRow.getCount()-1, 1, 1)
                        );
                        copySheet.addMergedRegion(
                                new CellRangeAddress(copyIdx, copyIdx+dtoRow.getCount()-1, 35, 35)
                        );


                        copyIdx = copyIdx+ dtoRow.getCount();
                        seq++;
                        assingSeq++;


                        //세부내역 처리부분

                        for(int i=0;i<dtoRow.getTotalCount();i++){
                            if(i== dtoRow.getTotalCount()-1){
                                copySheet2.copyRows(
                                        depTotalCount+5,
                                        depTotalCount+5,
                                        bodyRowIdx+i,
                                        policy
                                );
                            }else{
                                copySheet2.copyRows(
                                        depTotalCount+4,
                                        depTotalCount+4,
                                        bodyRowIdx+i,
                                        policy
                                );
                            }

                        }

                        filterDetail = filterDetail.stream()
                                .sorted(Comparator.comparingInt(
                                        v -> Util.getInteger(v.get("DAY").getObj())
                                ))
                                .toList();
                        int bodyRowSeq = bodyRowIdx;
                        BigDecimal bodyRowAmount = BigDecimal.ZERO;
                        for(Map<String,DbTypeDTO> detailRow:filterDetail){
                            String userName = Util.getStrChk(detailRow.get("USER_NAME").getObj());
                            String bodyDate = Util.getStrChk(detailRow.get("KOR_DATE").getObj());
                            String reqStart = Util.getStrChk(detailRow.get("DETAIL_START_TIME").getObj());
                            String reqEnd = Util.getStrChk(detailRow.get("DETAIL_END_TIME").getObj());
                            String capsStart = Util.getStrChk(detailRow.get("CAPS_START_TIME").getObj());
                            String capsEnd = Util.getStrChk(detailRow.get("CAPS_END_TIME").getObj());
                            BigDecimal bodyAmount = Util.getDecimal(detailRow.get("AMOUNT").getObj());
                            bodyRowAmount = bodyRowAmount.add(bodyAmount);
                            bodySumAmount   =   bodySumAmount.add(bodyAmount);

                            String bodyHolidayFlag = Util.getStrChk(detailRow.get("HOLIDAY_FLAG").getObj());

                            row = copySheet2.getRow(bodyRowSeq);

                            if(bodyRowIdx==4){
                                cell = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(teamName);
                            }

                            if(bodyRowSeq==bodyRowIdx){
                                cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(userName);
                            }
                            cell = row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(bodyDate);
                            if(bodyHolidayFlag.equals("Y")){
                                cell = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue("휴무일 근무");
                                copySheet2.addMergedRegion(
                                        new CellRangeAddress(bodyRowSeq, bodyRowSeq, 3, 4)
                                );
                            }else{
                                cell = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(reqStart);
                                cell = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                cell.setCellValue(reqEnd);
                            }
                            cell = row.getCell(5,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(capsStart);
                            cell = row.getCell(6,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(capsEnd);
                            cell = row.getCell(7,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(df.format(bodyAmount));

                            bodyRowSeq++;
                        }

                        //세부내역 처리부분
                        //이름 병합부분
                        if(filterDetail.size()>1){
                            copySheet2.addMergedRegion(
                                    new CellRangeAddress(bodyRowIdx, bodyRowSeq-1, 1, 1)
                            );
                        }


                        //소계
                        row = copySheet2.getRow(bodyRowSeq);
                        cell = row.getCell(7,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(df.format(bodyRowAmount));
                        bodyRowSeq++;


                        bodyRowIdx = bodyRowSeq;
                    }

                    if(startIdx!=srcIdx-1){
                        formSheet.addMergedRegion(
                                new CellRangeAddress(startIdx, srcIdx-1, 1, 1)
                        );
                    }

                    //수당내역 잉여행 숨기기
                    row = copySheet.getRow(payCount+5);
                    row.setZeroHeight(true);
                    row = copySheet.getRow(payCount+6);
                    row.setZeroHeight(true);
                    row = copySheet.getRow(payCount+7);
                    row.setZeroHeight(true);
                    row = copySheet.getRow(payCount+8);
                    cell= row.getCell(35);
                    cell.setCellValue(df.format(totalSumAmount));

                    //세부내역 부서병합부분
                    copySheet2.addMergedRegion(
                            new CellRangeAddress(4, bodyRowIdx-1, 0, 0)
                    );
                    //세부내역 잉여행 숨기기
                    row = copySheet2.getRow(depTotalCount+4);
                    row.setZeroHeight(true);
                    row = copySheet2.getRow(depTotalCount+5);
                    row.setZeroHeight(true);
                    //세부내역 시트별 총합
                    row = copySheet2.getRow(depTotalCount+6);
                    cell = row.getCell(7,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(df.format(bodySumAmount));

                }
                //지급명단 총합
                row = formSheet.getRow(srcIdx);
                cell = row.getCell(4);
                cell.setCellValue(df.format(sumAmount));


                //SHEET2,SHEET3숨기기
                boolean activeSet = false;

                for (int i = 0; i < srcWorkbook.getNumberOfSheets(); i++) {
                    String name = srcWorkbook.getSheetName(i);

                    if ("SHEET3".equals(name)||"SHEET2".equals(name)  ) {
                        srcWorkbook.setSheetHidden(i, true);
                    } else if (!activeSet) {
                        srcWorkbook.setActiveSheet(i);
                        srcWorkbook.setSelectedTab(i);
                        activeSet = true;
                    }
                }

                //출력부분
                srcWorkbook.write(bos);
                ret = ResponseDTO.<byte[]>builder().errFlag("N").errMsg("세부내역 다운완료").data(bos.toByteArray()).build();

            } catch (Exception e) {
                throw new BizException("getNightDetail",e.getMessage());
            }


            return ret;
        });
    }

    @Override
    public ResponseDTO<?> getWorkL010_033(String monFlag, String startDate, String endDate, String teamCode, String terminalCode, String userName, String filter) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;

            DbDto dbRet = repo.getWorkL010_033(monFlag,startDate,endDate,teamCode,terminalCode,userName,filter
                    ,info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            if(dbRet.getErrFlag().equals("Y")){
                throw new BizException("getWorkL010_033",dbRet.getErrMsg());
            }

            ret = ResponseDTO.from(dbRet);

            return ret;
        });
    }

    @Override
    public ResponseDTO<?> setWorkM010_017(WorkDTO.ApprDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;
            if(dto.array()==null||dto.array().isEmpty()){
                throw new BizException("setWorkM010_017","항목이 없습니다.");
            }

            for(WorkDTO.WorkDefaultDTO row:dto.array()){
                dbDto = repo.setWorkM010_017(row.year(),row.mon(),row.day(),row.userSid(),row.seq(),dto.type(),info.getUserId()
                        ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

                if(dbDto.getErrFlag().equals("Y")){
                    throw new BizException("setWorkM010_017",dbDto.getErrMsg());
                }
            }


            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    @Override
    public ResponseDTO<?> setWorkM010_026(WorkDTO.ApprDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo,()->{
            ResponseDTO<?> ret = null;
            DbDto dbDto = null;
            if(dto.array()==null||dto.array().isEmpty()){
                throw new BizException("setWorkM010_026","항목이 없습니다.");
            }

            for(WorkDTO.WorkDefaultDTO row:dto.array()){
                dbDto = repo.setWorkM010_026(row.year(),row.mon(),row.day(),row.userSid(),row.seq(),dto.type()
                        ,info.getUserLang(),Util.getGUID(),info.getUserId(),info.getUserIpAddress(),info.getPgmId());

                if(dbDto.getErrFlag().equals("Y")){
                    throw new BizException("setWorkM010_026",dbDto.getErrMsg());
                }
            }


            ret = ResponseDTO.from(dbDto);

            return ret;
        });
    }

    public ResponseDTO<?> getOrgExcel() {
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            ResponseDTO<byte[]> ret = fileClientService.fileRead("/IMG/TEMPLATE/ORG_TEMPLATE.xlsx");
            if ("Y".equals(ret.getErrFlag())) {
                throw new BizException("getOrgExcel", ret.getErrMsg());
            }
            OrgData data = new OrgData(repo);
            List<ExcelDTO.OrgDTO> groups = getOrg(data);
            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(ret.getData()));
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                OrgRender ctx = new OrgRender(workbook);
                if (groups.isEmpty()) throw new BizException("getOrgExcel", "조직 정보가 없습니다.");
                drawOrg(ctx, groups, data);
                workbook.write(bos);
                byte[] result = bos.toByteArray();
                ret.setData(result); // 템플릿이 아니라 생성된 파일 반환 (DTO setter 확인 필요)
                return ret;
            } catch (Exception e) {
                throw new BizException("getOrgExcel", e.getMessage());
            }
        });
    }

    private final class OrgData {
        final List<Map<String, DbTypeDTO>> departments;
        final List<Map<String, DbTypeDTO>> teams;
        final Map<String, List<Map<String, DbTypeDTO>>> normalTeams = new HashMap<>();
        final List<Map<String, DbTypeDTO>> rowTeams = new ArrayList<>();
        final Map<String, List<Map<String, DbTypeDTO>>> users = new HashMap<>();
        final Map<BigDecimal, Map<String, DbTypeDTO>> representatives = new HashMap<>();
        final Map<String, byte[]> images = new HashMap<>();

        OrgData(WorkRepo repo) {
            departments = orgRows(repo.getHrdpt());
            teams = orgRows(repo.getAllRowTeams());
            for (var row : teams) {
                if (orgStr(row, "ROW_PARENT").isEmpty()) {
                    normalTeams.computeIfAbsent(orgStr(row, "PARENT_CODE"), k -> new ArrayList<>()).add(row);
                } else {
                    rowTeams.add(row);
                }
            }
            for (var row : orgRows(repo.getAllOrgTeamUsers())) {
                users.computeIfAbsent(orgStr(row, "TEAM_CODE"), k -> new ArrayList<>()).add(row);
            }
            for (var row : orgRows(repo.getAllOrgRepresentatives())) {
                representatives.putIfAbsent(orgDecimal(row, "USER_SID").stripTrailingZeros(), row);
            }
        }

        byte[] image(String path) {
            return images.computeIfAbsent(path, key -> {
                ResponseDTO<byte[]> response = fileClientService.fileRead(key);
                if ("Y".equals(response.getErrFlag()) || response.getData() == null) {
                    throw new BizException("getOrgExcel", "이미지 조회 실패: " + key + " / " + response.getErrMsg());
                }
                return response.getData();
            });
        }
    }

    private List<Map<String, DbTypeDTO>> orgRows(DbDto dto) {
        if ("Y".equals(dto.getErrFlag())) throw new BizException("getOrg", dto.getErrMsg());
        return dto.getResult().get(0);
    }
    private Object orgValue(Map<String, DbTypeDTO> row, String key) {
        DbTypeDTO v = row.get(key);
        return v == null ? null : v.getObj();
    }
    private String orgStr(Map<String, DbTypeDTO> row, String key) {
        return Util.getStrChk(orgValue(row, key));
    }
    private int orgInt(Map<String, DbTypeDTO> row, String key) {
        return Util.getInteger(orgValue(row, key));
    }
    private BigDecimal orgDecimal(Map<String, DbTypeDTO> row, String key) {
        Object v = orgValue(row, key);
        return v == null ? BigDecimal.ZERO : Util.getDecimal(v);
    }
    private List<ExcelDTO.OrgDTO> children(ExcelDTO.OrgDTO org) {
        return org.getChildren() == null ? Collections.emptyList() : org.getChildren();
    }
    private int terminalCount(ExcelDTO.OrgDTO org) {
        return org.getTerminal() == null ? 1 : Math.max(1, org.getTerminal().size());
    }
    private int orgWidth(ExcelDTO.OrgDTO org) {
        return Math.max(1, org.getTotalCount());
    }
    private int groupWidth(List<ExcelDTO.OrgDTO> list) {
        return list.stream().mapToInt(this::orgWidth).sum() + Math.max(0, list.size()-1)*ORG_GAP;
    }

    private List<ExcelDTO.OrgDTO> getOrg(OrgData data) {
        List<ExcelDTO.OrgDTO> result = new ArrayList<>();
        Map<Integer, ExcelDTO.OrgDTO> groups = new LinkedHashMap<>();
        for (var row : data.departments) {
            int seq = orgInt(row, "VALUE1_NUMBER");
            String code = orgStr(row, "CODE_CODE");
            String name = orgStr(row, "CODE_NAME");
            ExcelDTO.OrgDTO dept = buildOrg(data, row, false, new HashSet<>(), orgInt(row,"TEAM_COUNT") > 0);
            dept.setSeq(seq);
            dept.setLineFlag(orgInt(row, "VALUE2_NUMBER") == 1);
            BigDecimal sid = orgDecimal(row, "USER_SID");
            ExcelDTO.OrgUserDTO rep = null;
            if (sid.signum() > 0) {
                var detail = data.representatives.get(sid.stripTrailingZeros());
                if (detail == null) throw new BizException("getOrg", name + "부서에 관리자는 직급이 없습니다.");
                rep = orgUser(data, detail, false);
            }
            if (seq == 1) {
                dept.setRepUser(rep);
                result.add(dept);
            } else {
                ExcelDTO.OrgDTO group = groups.get(seq);
                if (group == null) {
                    group = new ExcelDTO.OrgDTO();
                    group.setSeq(seq);
                    group.setChildren(new ArrayList<>());
                    groups.put(seq, group);
                    result.add(group);
                }
                group.getChildren().add(dept);
                // 기존 마지막 행 덮어쓰기 대신 마지막 유효 관리자 유지.
                if (rep != null) group.setRepUser(rep);
            }
        }
        for (var group : groups.values()) {
            group.setTeamCount(group.getChildren().size());
            group.setTotalCount(groupWidth(group.getChildren()));
        }
        return result;
    }

    private ExcelDTO.OrgDTO buildOrg(OrgData data, Map<String, DbTypeDTO> row,
                                     boolean rowFlag, Set<String> ancestors, boolean searchChildren) {
        String code = orgStr(row,"CODE_CODE");
        if (!ancestors.add(code)) throw new BizException("getOrg", "조직 순환 참조: " + code);
        try {
            ExcelDTO.OrgDTO org = new ExcelDTO.OrgDTO();
            org.setCode(code);
            org.setName(orgStr(row,"CODE_NAME"));
            org.setCol(Math.max(1, orgInt(row,"ORG_COL")));
            org.setRowFlag(rowFlag);
            List<ExcelDTO.OrgDTO> list = new ArrayList<>();
            if (searchChildren) {
                for (var child : data.normalTeams.getOrDefault(code, Collections.emptyList())) {
                    list.add(buildOrg(data, child, false, ancestors, true));
                }
                // 기존 INSTR(VALUE4_CHAR, code)>0 의미 그대로 유지.
                for (var child : data.rowTeams) {
                    if (orgStr(child,"ROW_PARENT").contains(code)) {
                        list.add(buildOrg(data, child, true, ancestors, true));
                    }
                }
            }
            org.setChildren(list);
            org.setTeamCount(list.size());
            LinkedHashMap<String, List<ExcelDTO.OrgUserDTO>> terminal = new LinkedHashMap<>();
            if (orgInt(row,"USER_COUNT") > 0) {
                for (var user : data.users.getOrDefault(code, Collections.emptyList())) {
                    terminal.computeIfAbsent(orgStr(user,"TERMINAL_CODE"), k -> new ArrayList<>())
                            .add(orgUser(data, user, true));
                }
            }
            org.setTerminal(terminal);
            // 기존 배정 폭 유지: 형제 조직 전체 + 1열 간격. true/false는 출력 시 별도 행으로 처리.
            org.setTotalCount(Math.max(org.getCol()*terminalCount(org), groupWidth(list)));
            return org;
        } finally {
            ancestors.remove(code);
        }
    }

    private ExcelDTO.OrgUserDTO orgUser(OrgData data, Map<String, DbTypeDTO> row, boolean fallback) {
        ExcelDTO.OrgUserDTO user = new ExcelDTO.OrgUserDTO();
        user.setOrgName(orgStr(row,"ORG_NAME"));
        user.setPhone(orgStr(row,"PHONE"));
        user.setMobile(orgStr(row,"MOBILE"));
        String path = orgStr(row,"FULL_PATH");
        if (!path.isEmpty()) user.setImg(data.image(path));
        return user;
    }

    // 2. 엑셀 출력 전용 컨텍스트: Workbook 단위 캐시
    private record OrgStyleKey(int base, String color, int edges, boolean center, int size, boolean bold) {}
    private record OrgFontKey(int base, int size, boolean bold) {}
    private final class OrgRender {
        final XSSFWorkbook workbook;
        final XSSFSheet sheet;
        final XSSFDrawing drawing;
        final Map<OrgStyleKey, XSSFCellStyle> styles = new HashMap<>();
        final Map<OrgFontKey, XSSFFont> fonts = new HashMap<>();
        final Map<String, XSSFColor> colors = new HashMap<>();
        final Map<byte[], Integer> pictures = new IdentityHashMap<>();
        OrgRender(XSSFWorkbook workbook) {
            this.workbook = workbook;
            sheet = workbook.getSheet("SHEET");
            if (sheet == null) throw new BizException("getOrgExcel", "SHEET 시트가 없습니다.");
            drawing = sheet.createDrawingPatriarch();
        }
        int picture(byte[] bytes) {
            return bytes == null || bytes.length == 0 ? -1 : pictures.computeIfAbsent(bytes,
                    v -> workbook.addPicture(v, Workbook.PICTURE_TYPE_PNG));
        }
        Cell cell(int y, int x) {
            Row row = sheet.getRow(y);
            if (row == null) row = sheet.createRow(y);
            return row.getCell(x, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        }
        void style(Cell cell, String color, int edges, boolean center, int size, boolean bold) {
            XSSFCellStyle base = (XSSFCellStyle) cell.getCellStyle();
            OrgStyleKey key = new OrgStyleKey(base.getIndex() & 0xffff, color, edges, center, size, bold);
            XSSFCellStyle style = styles.computeIfAbsent(key, k -> {
                XSSFCellStyle s = workbook.createCellStyle();
                s.cloneStyleFrom(base);
                if (!color.isEmpty()) {
                    s.setFillForegroundColor(colors.computeIfAbsent(color, c ->
                            new XSSFColor(java.awt.Color.decode(c), new DefaultIndexedColorMap())));
                    s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                if ((edges & 1) != 0) s.setBorderTop(BorderStyle.MEDIUM);
                if ((edges & 2) != 0) s.setBorderBottom(BorderStyle.MEDIUM);
                if ((edges & 4) != 0) s.setBorderLeft(BorderStyle.MEDIUM);
                if ((edges & 8) != 0) s.setBorderRight(BorderStyle.MEDIUM);
                if (center) {
                    s.setAlignment(HorizontalAlignment.CENTER);
                    s.setVerticalAlignment(VerticalAlignment.CENTER);
                }
                if (size > 0) {
                    OrgFontKey fk = new OrgFontKey(base.getFontIndex(), size, bold);
                    s.setFont(fonts.computeIfAbsent(fk, f -> {
                        XSSFFont old = workbook.getFontAt(f.base());
                        XSSFFont font = workbook.createFont();
                        font.getCTFont().set(old.getCTFont());
                        font.setFontHeightInPoints((short) size);
                        font.setBold(bold);
                        return font;
                    }));
                }
                return s;
            });
            cell.setCellStyle(style);
        }
        void border(int y1, int y2, int x1, int x2, String color) {
            if (y2 < y1 || x2 < x1) return;
            for (int y=y1; y<=y2; y++) {
                for (int x=x1; x<=x2; x++) {
                    int edges = (y==y1?1:0)|(y==y2?2:0)|(x==x1?4:0)|(x==x2?8:0);
                    if (edges != 0 || !color.isEmpty()) style(cell(y,x), color, edges, false, 0, false);
                }
            }
        }
        void header(int y1, int y2, int x1, int x2, String text, String color, int size) {
            if (y1 != y2 || x1 != x2) sheet.addMergedRegion(new CellRangeAddress(y1,y2,x1,x2));
            border(y1,y2,x1,x2,color);
            Cell cell = cell(y1,x1);
            cell.setCellValue(text);
            style(cell,"",0,true,size,true);
        }
        void text(int y, int x, String value) {
            Cell cell = cell(y,x);
            cell.setCellValue(value == null ? "" : value);
            style(cell,"",0,true,0,false);
        }
    }

    // 3. 조직도 배치. 기존 행 위치와 직원 8행 카드를 유지.
    private void drawOrg(OrgRender c, List<ExcelDTO.OrgDTO> groups, OrgData data) {
        int center = groupWidth(groups)/2;
        c.header(2,4,Math.max(0,center-10),center+10,"대표이사 이선호","",12);
        line(c,5,9,center,center,true,true);
        drawPicture(c,c.picture(data.image("/IMG/ORGIMG/부사장.png")),center,9);
        c.border(9,16,center,center,"");
        c.text(14,center,"부사장 : 조경국");
        c.text(15,center,"744-1310");
        c.text(16,center,"010-5396-7552");
        line(c,17,20,center,center,true,true);
        branch(c,groups,0,20,center);
        int x = 0;
        for (var group : groups) {
            int mid = x+orgWidth(group)/2;
            if (group.getRepUser() == null) line(c,20,32,mid,mid,true,true);
            else {
                line(c,20,22,mid,mid,true,true);
                card(c,group.getRepUser(),mid,22,true);
                line(c,30,32,mid,mid,true,true);
            }
            if (group.getSeq()==1) {
                line(c,32,34,mid,mid,true,true);
                c.header(34,34,x,x+orgWidth(group)-1,group.getName(),"#FFC000",8);
                drawDepartmentBody(c,group,x,35);
            } else {
                var departments = children(group);
                branch(c,departments,x,32,mid);
                int dx=x;
                for (var dept : departments) {
                    int dm=dx+orgWidth(dept)/2;
                    line(c,32,34,dm,dm,true,true);
                    c.header(34,34,dx,dx+orgWidth(dept)-1,dept.getName(),"#FFC000",8);
                    drawDepartmentBody(c,dept,dx,35);
                    dx+=orgWidth(dept)+ORG_GAP;
                }
            }
            x+=orgWidth(group)+ORG_GAP;
        }
    }

    private int drawDepartmentBody(
            OrgRender c,
            ExcelDTO.OrgDTO org,
            int x,
            int y
    ) {
        // VALUE2_NUMBER = 1 : 연결선 표시
        if (org.isLineFlag()) {
            return drawBody(c, org, x, y);
        }

        // VALUE2_NUMBER = 0 : 연결선 미표시
        int cursor = drawUsers(c, org, x, y);

        var rows = children(org).stream()
                .filter(ExcelDTO.OrgDTO::isRowFlag)
                .toList();

        var linked = children(org).stream()
                .filter(v -> !v.isRowFlag())
                .toList();

        if (!rows.isEmpty()) {
            cursor = drawChildren(c, rows, x, cursor);
        }

        if (!linked.isEmpty()) {
            cursor = drawChildren(c, linked, x, cursor);
        }

        return cursor;
    }

    // 마지막 사용 행의 다음 행 반환. 직원/하위 조직 겹침 방지.
    private int drawBody(
            OrgRender c,
            ExcelDTO.OrgDTO org,
            int x,
            int y
    ) {
        int cursor = drawUsers(c, org, x, y);

        var rows = children(org).stream()
                .filter(ExcelDTO.OrgDTO::isRowFlag)
                .toList();

        var linked = children(org).stream()
                .filter(v -> !v.isRowFlag())
                .toList();

        // 연결선 없는 하위 조직
        if (!rows.isEmpty()) {
            cursor = drawChildren(c, rows, x, cursor);
        }

        if (linked.isEmpty()) {
            return cursor;
        }

        // 상위 조직의 중앙 좌표
        OrgLinePoint parent = teamCenter(
                c, x, visibleTeamWidth(org)
        );

        // 하위 조직이 하나인 경우 수직선만 생성
        if (linked.size() == 1) {
            int childY = cursor + 2;

            teamLine(c, cursor, childY, parent, parent);

            return drawChildren(c, linked, x, childY);
        }

        // 하위 조직이 여러 개인 경우 기존 분기선 유지
        int childX = x + Math.max(
                0,
                (orgWidth(org) - groupWidth(linked)) / 2
        );

        List<OrgLinePoint> points = new ArrayList<>();

        int dx = childX;

        for (var child : linked) {
            points.add(
                    teamCenter(c, dx, visibleTeamWidth(child))
            );

            dx += orgWidth(child) + ORG_GAP;
        }

        int branchY = cursor + 1;

        teamLine(c, cursor, branchY, parent, parent);

        OrgLinePoint left = parent;
        OrgLinePoint right = parent;

        for (var point : points) {
            if (comparePoint(point, left) < 0) {
                left = point;
            }

            if (comparePoint(point, right) > 0) {
                right = point;
            }
        }

        teamLine(c, branchY, branchY, left, right);

        for (var point : points) {
            teamLine(c, branchY, branchY + 1, point, point);
        }

        return drawChildren(c, linked, childX, branchY + 1);
    }
    private int visibleTeamWidth(ExcelDTO.OrgDTO org) {
        return Math.max(1,org.getCol())*terminalCount(org);
    }
    private record OrgLinePoint(int col,int dx) {}
    private int comparePoint(OrgLinePoint a,OrgLinePoint b) {
        int cmp=Integer.compare(a.col(),b.col());
        return cmp!=0?cmp:Integer.compare(a.dx(),b.dx());
    }
    private OrgLinePoint teamCenter(OrgRender c,int x,int width) {
        double total=0;
        for(int i=x;i<x+width;i++) total+=c.sheet.getColumnWidthInPixels(i);
        double remaining=total/2.0;
        for(int i=x;i<x+width;i++) {
            double w=c.sheet.getColumnWidthInPixels(i);
            if(remaining<w || i==x+width-1)
                return new OrgLinePoint(i,(int)Math.round(remaining*Units.EMU_PER_PIXEL));
            remaining-=w;
        }
        return new OrgLinePoint(x,0);
    }
    private void teamLine(OrgRender c,int y1,int y2,OrgLinePoint p1,OrgLinePoint p2) {
        if(y1==y2 && comparePoint(p1,p2)==0) return;
        XSSFClientAnchor a=new XSSFClientAnchor();
        a.setCol1(p1.col()); a.setCol2(p2.col());
        a.setRow1(y1); a.setRow2(y2);
        a.setDx1(p1.dx()); a.setDx2(p2.dx());
        XSSFSimpleShape shape=c.drawing.createSimpleShape(a);
        shape.setShapeType(ShapeTypes.LINE);
        shape.setLineStyleColor(0,0,0);
        shape.setLineWidth(1.5);
    }
    private int drawChildren(OrgRender c, List<ExcelDTO.OrgDTO> list, int x, int y) {
        int next=y;
        for (var child : list) {
            int width=visibleTeamWidth(child);
            c.header(y,y,x,x+width-1,child.getName(),"#FCD5B4",8);
            next=Math.max(next,drawBody(c,child,x,y+1));
            x+=orgWidth(child)+ORG_GAP;
        }
        return next;
    }
    private int drawUsers(OrgRender c, ExcelDTO.OrgDTO org, int x, int y) {
        var terminal = org.getTerminal();
        if (terminal == null || terminal.isEmpty()) return y;
        int cols=Math.max(1,org.getCol());
        boolean multi=terminal.size()>1;
        int maxRows=terminal.values().stream().mapToInt(v->(v.size()+cols-1)/cols).max().orElse(0);
        int start=y+(multi?1:0);
        int end=start+maxRows*CARD_ROWS;
        for (var entry : terminal.entrySet()) {
            if (multi) c.header(y,y,x,x+cols-1,entry.getKey(),"#FDE9D9",8);
            var users=entry.getValue();
            for (int i=0;i<users.size();i++) card(c,users.get(i),x+i%cols,start+(i/cols)*CARD_ROWS,false);
            if (end>start) c.border(start,end-1,x,x+cols-1,"");
            x+=cols;
        }
        return end;
    }
    private void card(OrgRender c, ExcelDTO.OrgUserDTO user, int x, int y, boolean border) {
        drawPicture(c,c.picture(user.getImg()),x,y);
        if (border) c.border(y,y+7,x,x,"");
        c.text(y+5,x,user.getOrgName());
        c.text(y+6,x,user.getPhone());
        c.text(y+7,x,user.getMobile());
    }
    private double rowHeight(XSSFSheet sheet,int y) {
        Row row=sheet.getRow(y);
        return row==null?sheet.getDefaultRowHeightInPoints():row.getHeightInPoints();
    }
    private void drawPicture(OrgRender c,int idx,int x,int y) {
        if (idx<0) return;
        int width=(int)Math.round(c.sheet.getColumnWidthInPixels(x)*Units.EMU_PER_PIXEL);
        int top=(int)Math.round(rowHeight(c.sheet,y)*12700/2.0);
        int bottom=(int)Math.round(rowHeight(c.sheet,y+4)*12700/2.0);
        // 기존 여백 유지, 음수 dx2 대신 마지막 셀 내부 양수 좌표 사용.
        int pad=Math.max(0,Math.min(top,(width-1)/2));
        XSSFClientAnchor a=new XSSFClientAnchor();
        a.setCol1(x); a.setCol2(x); a.setRow1(y); a.setRow2(y+4);
        a.setDx1(pad); a.setDx2(width-pad); a.setDy1(top); a.setDy2(bottom);
        c.drawing.createPicture(a,idx);
    }
    private void branch(OrgRender c,List<ExcelDTO.OrgDTO> list,int x,int y,int parentCenter) {
        if (list.isEmpty()) return;
        int first=x+orgWidth(list.get(0))/2;
        int last=x+groupWidth(list)-orgWidth(list.get(list.size()-1))+(orgWidth(list.get(list.size()-1))/2);
        int left=Math.min(first,parentCenter), right=Math.max(last,parentCenter);
        if (left!=right) line(c,y,y,left,right,true,true);
    }
    private void line(OrgRender c,int y1,int y2,int x1,int x2,boolean center1,boolean center2) {
        if (x1==x2 && y1==y2 && center1==center2) return;
        XSSFClientAnchor a=new XSSFClientAnchor();
        a.setCol1(x1); a.setCol2(x2); a.setRow1(y1); a.setRow2(y2);
        // 기존 중앙 보정 +5px 유지. 셀 너비가 좁으면 범위 내부로 제한.
        a.setDx1(center1?centerOffset(c.sheet,x1):0);
        a.setDx2(center2?centerOffset(c.sheet,x2):0);
        XSSFSimpleShape shape=c.drawing.createSimpleShape(a);
        shape.setShapeType(ShapeTypes.LINE);
        shape.setLineStyleColor(0,0,0);
        shape.setLineWidth(1.5);
    }
    private int centerOffset(XSSFSheet sheet,int col) {
        double width=sheet.getColumnWidthInPixels(col);
        return (int)Math.round(Math.min(width,width/2.0+5.0)*Units.EMU_PER_PIXEL);
    }


    private Map<String, List<ExcelDTO.NightDetailHeaderDTO>> convHeaderGroup(List<Map<String, Object>> dto, List<Map<String,DbTypeDTO>> dbDto){
        return execute(()->{
            Map<String, List<ExcelDTO.NightDetailHeaderDTO>> retArray = new LinkedHashMap<>();
            List<Map<String,List<String>>>  ret =   new ArrayList<>();

            //지급부서 추출 및 sort
            List<Map<String,Object>> tmpSort = dto.stream()
                    .filter(v->"COV5".equals(v.get("VALUE1_CHAR")))
                    .sorted(Comparator.comparingInt(v->Util.getInteger(v.get("ORDER_SEQ")))).toList();

            for(Map<String,Object> row : tmpSort){
                String value2 = Util.getStrChk(row.get("VALUE2_CHAR"));
                String value3 = Util.getStrChk(row.get("VALUE3_CHAR"));
                if(value3.isEmpty()){
                    List<Map<String,Object>> tmpRowSort = dto.stream()
                            .filter(v->"COV4".equals(v.get("VALUE1_CHAR"))&&value2.equals(v.get("VALUE2_CHAR")))
                            .sorted(Comparator.comparingInt(v->Util.getInteger(v.get("ORDER_SEQ")))).toList();
                    List<String> tmpArray = new ArrayList<>();
                    for(Map<String,Object> sortRow : tmpRowSort){
                        String code = Util.getStrChk(sortRow.get("CODE_CODE"));
                        tmpArray.add(code);
                    }
                    Map<String,List<String>> tmpMap = new LinkedHashMap<>();
                    tmpMap.put(value2,tmpArray);
                    ret.add(tmpMap);
                }else{
                    List<String> tmpArray = new ArrayList<>();
                    tmpArray.add(value3);
                    Map<String,List<String>> tmpMap = new LinkedHashMap<>();
                    tmpMap.put(value2,tmpArray);
                    ret.add(tmpMap);
                }
            }

            // 이미 배정된 사용자
            Set<BigDecimal> assignedUsers = new HashSet<>();

            for (Map<String, List<String>> row : ret) {

                for (Map.Entry<String, List<String>> rowEntry : row.entrySet()) {

                    String key = rowEntry.getKey();
                    List<String> values = rowEntry.getValue();

                    for (String code : values) {

                        List<ExcelDTO.NightDetailHeaderDTO> result = dbDto.stream()

                                .filter(v -> {
                                    BigDecimal userSid =
                                            Util.getDecimal(v.get("USER_SID").getObj());

                                    String teamCode =
                                            Util.getStrChk(v.get("TEAM_CODE").getObj());

                                    // 코드가 일치하고 아직 추가하지 않은 사용자
                                    return !teamCode.isEmpty()
                                            && code.contains(teamCode)
                                            && assignedUsers.add(userSid);
                                })

                                .map(v -> {
                                    ExcelDTO.NightDetailHeaderDTO headerDto =
                                            new ExcelDTO.NightDetailHeaderDTO();
                                    headerDto.setUserSid(Util.getDecimal(v.get("USER_SID").getObj()));

                                    headerDto.setUserName(
                                            Util.getStrChk(v.get("USER_NAME").getObj())
                                    );

                                    headerDto.setRegion(
                                            Util.getStrChk(v.get("HRCOS_NAME").getObj())
                                    );

                                    headerDto.setAmount(
                                            Util.getStrChk(v.get("SUM_AMOUNT").getObj())
                                    );
                                    headerDto.setTerminalCode(
                                            Util.getStrChk(v.get("TERMINAL_CODE").getObj())
                                    );
                                    headerDto.setCount(
                                            Util.getInteger(v.get("AMOUNT_COUNT").getObj())
                                    );
                                    headerDto.setTotalCount(
                                            Util.getInteger(v.get("TOTAL_COUNT").getObj())
                                    );

                                    return headerDto;
                                })
                                .toList();

                        retArray.computeIfAbsent(
                                key,
                                k -> new ArrayList<>()
                        ).addAll(result);
                    }
                }
            }

            List<Map<String,DbTypeDTO>> others = dbDto.stream()
                    .filter(v -> {
                        BigDecimal userSid =
                                Util.getDecimal(v.get("USER_SID").getObj());

                        return !assignedUsers.contains(userSid);
                    })
                    .toList();

            for(Map<String,DbTypeDTO> row : others){
                String teamName = Util.getStrChk(row.get("TEAM_NAME").getObj());

                ExcelDTO.NightDetailHeaderDTO headerDto =
                        new ExcelDTO.NightDetailHeaderDTO();
                headerDto.setUserSid(Util.getDecimal(row.get("USER_SID").getObj()));

                headerDto.setUserName(
                        Util.getStrChk(row.get("USER_NAME").getObj())
                );

                headerDto.setRegion(
                        Util.getStrChk(row.get("HRCOS_NAME").getObj())
                );

                headerDto.setAmount(
                        Util.getStrChk(row.get("SUM_AMOUNT").getObj())
                );

                headerDto.setTerminalCode(
                        Util.getStrChk(row.get("TERMINAL_CODE").getObj())
                );
                headerDto.setCount(
                        Util.getInteger(row.get("AMOUNT_COUNT").getObj())
                );
                headerDto.setTotalCount(
                        Util.getInteger(row.get("TOTAL_COUNT").getObj())
                );

                retArray.computeIfAbsent(
                        teamName,
                        k -> new ArrayList<>()
                ).add(headerDto);
            }



            return retArray;
        });
    }

    private CellStyle getCellStyle_Line(Workbook workbook, boolean top, boolean bottom, boolean left, boolean right, boolean fontBold){
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

    private Map<BigDecimal,List<Map<String,Object>>> convNightData(DbDto dto){
        return execute(()->{
            Map<BigDecimal,List<Map<String,Object>>> ret = new LinkedHashMap<>();

            for(Map<String,DbTypeDTO> row : dto.getResult().get(0)){
                BigDecimal userSid = Util.getDecimal(row.get("USER_SID").getObj());


                if(!ret.containsKey(userSid)){
                    ret.put(userSid,new ArrayList<>());
                }
                Map<String,Object> tmpMap = new LinkedHashMap<>();
                for(Map.Entry<String,DbTypeDTO> entry : row.entrySet()){
                    String key = entry.getKey();
                    DbTypeDTO value = entry.getValue();
                    tmpMap.put(key,value.getObj());
                }
                ret.get(userSid).add(tmpMap);
            }

            return ret;
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

            Cell srcCell = srcRow.getCell(i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (srcCell == null) continue;

            Cell destCell = destRow.getCell(i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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

            Cell srcCell = srcRow.getCell(i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            if (srcCell == null) {
                continue;
            }

            Cell destCell = destRow.getCell(i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            if (destCell == null) {
                destCell = destRow.createCell(i);
            }

            destCell.setCellStyle(
                    srcCell.getCellStyle()
            );
        }
    }
}

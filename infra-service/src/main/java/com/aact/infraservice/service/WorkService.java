package com.aact.infraservice.service;

import com.aact.common.*;
import com.aact.common.ServiceBase;
import com.aact.commonClient.service.FileClientService;
import com.aact.infraservice.dto.CapsTimeDTO;
import com.aact.infraservice.dto.WorkDTO;
import com.aact.infraservice.repo.WorkRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkService extends ServiceBase {

    private final ObjectProvider<WorkRepo> workRepoProvider;
    private final CapsService capsService;
    private final FileClientService fileClientService;

    public ResponseDTO<?> getWorkM010_002(String date, String deptCode, String approveFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            String terminalCode = "";

            Map<String, Object> tmp = info.getRelArray().stream().filter((m) -> "TRMCD".equals(String.valueOf(m.get("CLASS_CODE")))).findFirst().orElse(null);
            if (tmp != null) {
                if (tmp.get("CODE_CODE") != null) {
                    terminalCode = String.valueOf(tmp.get("CODE_CODE"));
                }
            }

            if (terminalCode.isEmpty()) {
                throw new BizException("getWorkM010_002", info.getUserId() + "의 기본 터미널 근무지가 없습니다.");
            }

            DbDto dbRet = repo.getWorkM010_002(date, deptCode, terminalCode, Util.getStrChk(approveFlag), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getWorkM010_002", dbRet);
        });
    }

    public ResponseDTO<?> getWorkM010_006(WorkDTO.HrSearchDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkM010_006(dto.type(),dto.reqFlag(),dto.deptCode(),dto.terminalCode(),dto.toDate(),dto.fromDate(),dto.userName(),info.getUserLang(), Util.getGUID(),
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

    public ResponseDTO<?> setWorkM010_014(WorkDTO.SaveDTO dto) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {

                    if (info.getSignData() == null || info.getSignData().length == 0) {
                        throw new BizException("setWorkM010_014", "도장을 등록하여 주세요.");
                    }

                    String terminalCode = "";

                    Map<String, Object> tmpMap = info.getRelArray().stream().filter((m) -> "TRMCD".equals(String.valueOf(m.get("CLASS_CODE")))).findFirst().orElse(null);
                    if (tmpMap != null) {
                        if (tmpMap.get("CODE_CODE") != null) {
                            terminalCode = String.valueOf(tmpMap.get("CODE_CODE"));
                        }
                    }

                    if (terminalCode.isEmpty()) {
                        throw new BizException("setWorkM010_014", info.getUserId() + "의 기본 터미널 근무지가 없습니다.");
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
                        DbDto userRet = repo.callSql("SELECT USER_SID FROM TCM_USER_MASTER WHERE USER_ID = '" + row.userId() + "' AND USABLE_FLAG = 'Y'");
                        if (userRet.getErrFlag().equals("Y")) {
                            throw new BizException("setWorkM010_014", userRet.getErrMsg());
                        }
                        userSid = Util.getDecimal(userRet.getResult().get(0).get(0).get("USER_SID").getObj().toString());

                        if (row.dayArray() == null) {
                            throw new BizException("setWorkM010_014", row.userId() + "에 스케줄 작성이 안돼있습니다.");
                        }

                        for (WorkDTO.SaveDayDTO row2 : row.dayArray()) {
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
                                            } else if (beforeReg.equals(",")) {
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
                                        } else if (beforeReg.equals(",")) {
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

                                dbRet = repo.setWorkM010_013(yyyy, mon, userSid,row2.day() , new BigDecimal(i), code,"","" ,ot,BigDecimal.ZERO,BigDecimal.ZERO,
                                        "",terminal,terminalCode,dto.teamCode(),"G",info.getUserLang(), Util.getGUID(),
                                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                                if (dbRet.getErrFlag().equals("Y")) {
                                    throw new BizException("setWorkM010_014", dbRet.getErrMsg());
                                }
                            }

                            dbRet = repo.setStatusUpdate(yyyy, mon, userSid,row2.day() ,info.getUserLang(), Util.getGUID(),
                                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                            if (dbRet.getErrFlag().equals("Y")) {
                                throw new BizException("setWorkM010_014", dbRet.getErrMsg());
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

            char[] symbols = {'-', ','};

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

    public ResponseDTO<?> setWorkM010_017(String date,String teamCode) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkM010_017", "도장을 등록하여 주세요.");
            }
            DbDto dbRet = null;

            dbRet = repo.setWorkM010_017(date.substring(0, 4), date.substring(4, 6), teamCode, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("setWorkM010_017", dbRet.getErrMsg());
            }
            return okOrThrow("setWorkM010_017", dbRet);
        });

    }

    public ResponseDTO<?> getWorkCapsData(String date) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkM010_003(date, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            if (dbRet.getErrFlag().equals("Y")) {
                throw new BizException("getWorkCapsData", dbRet.getErrMsg());
            }

            if (dbRet.getResult().get(0).isEmpty()) {
                throw new BizException("getWorkCapsData", "해당날짜의 근무코드가 없습니다.");
            }


            ResponseDTO<List<CapsTimeDTO.SearchDTO>> ret = ResponseDTO.<List<CapsTimeDTO.SearchDTO>>builder().errFlag("N").errMsg("").data(new ArrayList<>()).build();


            for (Map<String, DbTypeDTO> row : dbRet.getResult().get(0)) {
                if (row.get("CLOSE_FLAG").getObj() == null || !row.get("CLOSE_FLAG").getObj().equals("Y")) {
                    throw new BizException("getWorkCapsData", "스케줄을 마감하여주세요.");
                }
                String start = Util.getStrChk(row.get("START_TIME").getObj());
                String end = Util.getStrChk(row.get("END_TIME").getObj());
                if (!Util.getStrChk(row.get("HOLI_TYPE").getObj()).equals("Y") && (start.isEmpty() || end.isEmpty())) {
                    throw new BizException("getWorkCapsData", Util.getStrChk(row.get("CODE_NAME").getObj()) + " OT 신청 불가능");
                }

                if (start.length() == 2) {
                    start += "00";
                }
                if (end.length() == 2) {
                    end += "00";
                }

                String tmpStartDate = date;
                String tmpEndDate = date;
                int plusDay = 0;

                if (Util.getDecimal(row.get("ADD_DAY").getObj()).compareTo(new BigDecimal(1)) == 0) {
                    LocalDate next = LocalDate.parse(date, formatter).plusDays(1);
                    tmpEndDate = next.format(formatter);
                    plusDay = Util.getDecimal(row.get("ADD_DAY").getObj()).intValue();
                }

                if (Util.getStrChk(row.get("CAPS_FLAG").getObj()).equals("Y")) {
                    ResponseDTO<CapsTimeDTO.CapsRangeResult> capDto = capsService.findDateToId(info.getUserId(), date, start, end, plusDay);
                    CapsTimeDTO.SearchDTO tmpNew = new CapsTimeDTO.SearchDTO(date,
                            Util.getDecimal(row.get("SEQ").getObj()),
                            Util.getStrChk(row.get("CODE_CODE").getObj()),
                            Util.getStrChk(row.get("CODE_NAME").getObj()),
                            tmpStartDate,
                            start,
                            tmpEndDate,
                            end,
                            capDto.getData().capsOrgStartTime(),
                            capDto.getData().capsOrgEndTime(),
                            capDto.getData().capsStartDate(), capDto.getData().capsStartTime(), capDto.getData().capsEndDate(), capDto.getData().capsEndTime(), Util.getStrChk(row.get("REMARK").getObj()), null);

                    ret.getData().add(tmpNew);

                } else {
                    LocalDate startDate = LocalDate.parse(date, formatter);

                    LocalDate endDate = startDate.plusDays(Util.getDecimal(row.get("ADD_DAY").getObj()).longValue());
                    CapsTimeDTO.SearchDTO tmpNew = new CapsTimeDTO.SearchDTO(date,
                            Util.getDecimal(row.get("SEQ").getObj()),
                            Util.getStrChk(row.get("CODE_CODE").getObj()),
                            Util.getStrChk(row.get("CODE_NAME").getObj()),
                            tmpStartDate,
                            start,
                            tmpEndDate,
                            end,
                            Util.getStrChk(row.get("CAPS_START_TIME").getObj()),
                            Util.getStrChk(row.get("CAPS_END_TIME").getObj()),
                            startDate.format(formatter), Util.getStrChk(row.get("START_TIME").getObj()), endDate.format(formatter), Util.getStrChk(row.get("END_TIME").getObj()), Util.getStrChk(row.get("REMARK").getObj()), null);

                    ret.getData().add(tmpNew);
                }


            }

            return okOrThrow("getWorkCapsData", ret);
        });

    }

    public ResponseDTO<?> setWorkM010_019(CapsTimeDTO.SearchDTO dto) {

        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = null;
            if (dto.capsStartDate().length() != 8) {
                throw new BizException("setWorkM010_019", "날짜가 잘못됐습니다.");
            }
            String yyyy = dto.capsStartDate().substring(0, 4);
            String mon = dto.capsStartDate().substring(4, 6);
            String day = String.valueOf(Integer.parseInt(dto.capsStartDate().substring(6, 8)));
            WorkTimeResult ret = calcHourDiff(dto.capsStartDate(), dto.capsStartTime(), dto.capsEndDate(), dto.capsEndTime());

            dbRet = repo.setWorkM010_019(yyyy, mon, day, info.getUserSid(), dto.seq(), dto.capsOrgStartTime(), dto.capsOrgEndTime(), dto.capsStartTime(), dto.capsEndTime(),
                    Util.getDecimal(ret.days), Util.getDecimal(ret.totalHours), Util.getDecimal(ret.nightHours), dto.remark(), info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
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

    public ResponseDTO<?> getWorkM010_004(String date) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            DbDto dbRet = repo.getWorkM010_004(date, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getWorkM010_004", dbRet);
        });
    }

    public ResponseDTO<?> getWorkM010_005(String date, String deptCode, String userName, String approveFlag) {
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
            DbDto dbRet = repo.getWorkM010_005(yyyy, mon, day, deptCode, userName, approveFlag, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("getWorkM010_005", dbRet);
        });
    }


    public ResponseDTO<?> setWorkM010_022(List<CapsTimeDTO.DeleteDTO> dtos) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            DbDto dbRet = null;
            for (CapsTimeDTO.DeleteDTO row : dtos) {
                String day = String.valueOf(Integer.parseInt(row.date().substring(6, 8)));
                dbRet = repo.setWorkM010_022(row.date().substring(0, 4), row.date().substring(4, 6), day, row.userSid(), row.seq(), info.getUserLang(), Util.getGUID(),
                        info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                if (dbRet.getErrFlag().equals("Y")) {
                    throw new BizException("setWorkM010_022", dbRet.getErrMsg());
                }
            }
            return okOrThrow("setWorkM010_022", dbRet);
        });
    }

    public ResponseDTO<?> setWorkM010_031(List<CapsTimeDTO.DeleteDTO> dtos, String approveFlag) {
        ClsUserInfo info = UserContext.get();
        WorkRepo repo = workRepoProvider.getObject();
        return execute(repo, () -> {
            if (info.getSignData() == null || info.getSignData().length == 0) {
                throw new BizException("setWorkM010_031", "도장을 등록하여 주세요.");
            }
            DbDto dbRet = null;
            for (CapsTimeDTO.DeleteDTO row : dtos) {
                String day = String.valueOf(Integer.parseInt(row.date().substring(6, 8)));
                dbRet = repo.setWorkM010_031(row.date().substring(0, 4), row.date().substring(4, 6), day, row.userSid(), row.seq(), approveFlag, info.getUserLang(), Util.getGUID(),
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
            int day = Integer.parseInt(dto.day());
            for (BigDecimal row : dto.userArray()) {

                for(int i = 1;i<=day; i++){
                    dbRet = repo.setWorkM010_032(dto.date().substring(0, 4), dto.date().substring(4, 6), String.valueOf(i), row, info.getUserLang(), Util.getGUID(),
                            info.getUserId(), info.getUserIpAddress(), info.getPgmId());
                    if (dbRet.getErrFlag().equals("Y")) {
                        throw new BizException("setWorkM010_032", dbRet.getErrMsg());
                    }
                }


            }
            return okOrThrow("setWorkM010_032", dbRet);
        });
    }

    WorkTimeResult calcHourDiff(String startDate, String startTime,
                                String endDate, String endTime) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        LocalDateTime start = LocalDateTime.parse(startDate + startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate + endTime, formatter);

        long diffMinutes = Duration.between(start, end).toMinutes();

        if (diffMinutes < 0) {
            throw new BizException("calcHourDiff", "종료 시간이 시작 시간보다 빠릅니다.");
        }

        if (diffMinutes % 30 != 0) {
            throw new BizException("calcHourDiff", "시간 차이는 30분 단위여야 합니다.");
        }
        long days = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());

        double totalHours = diffMinutes / 60.0;

        // 🔥 야간 시간 계산
        long nightMinutes = 0;

        LocalDateTime cursor = start;

        while (cursor.isBefore(end)) {

            LocalDateTime next = cursor.plusMinutes(30);

            if (next.isAfter(end)) {
                next = end;
            }

            int hour = cursor.getHour();

            // 야간 시간 조건 (22~24 or 0~6)
            if (hour >= 22 || hour <= 6) {
                nightMinutes += Duration.between(cursor, next).toMinutes();
            }

            cursor = next;
        }

        double nightHours = nightMinutes / 60.0;

        double deduction = Math.floor(totalHours / 4.5) * 0.5;
        totalHours = totalHours - deduction;

        if (nightHours > 8) {
            throw new BizException("calcHourDiff", "야간근무 계산 에러");
        } else if (nightHours >= 4.5 && nightHours < 8) {
            nightHours = nightHours - 0.5;
        } else if (nightHours == 8) {
            nightHours = nightHours - 1;
        }


        return new WorkTimeResult(totalHours, nightHours, days);
    }

    record WorkTimeResult(double totalHours, double nightHours, long days) {
    }
}

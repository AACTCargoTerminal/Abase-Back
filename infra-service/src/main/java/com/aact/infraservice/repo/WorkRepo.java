package com.aact.infraservice.repo;

import com.aact.common.DbDto;
import com.aact.common.DbTypeDTO;
import com.aact.common.DbTypeDTO.*;
import com.aact.common.SourcName;
import com.aact.common.BizBase;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkRepo extends BizBase {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public WorkRepo(Map<SourcName, DataSource> multiDataSource, String mainSelect){
        super(multiDataSource,mainSelect);
        connect();
    }

    public DbDto getWorkM010_002(String date, String deptCode, String terminalCode, String approveFlag, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYYMM", date));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_APPROVE_FLAG", approveFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_DETAIL", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_002", input);

    }


    public DbDto getWorkM010_003(String date, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYYMMDD", date));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_003", input);

    }

    public DbDto getWorkM010_004(String date, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYYMMDD", date));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_004", input);

    }

    public DbDto getWorkM010_005(String yyyy, String mon, String day, String deptCode, String userName, String approveFlag, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YEAR", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME", userName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_APPROVE_FLAG", approveFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_005", input);

    }

    public DbDto getWorkM010_006(String type,String reqFlag,String deptCode,String terminalCode,String toDate,String fromDate,String userName,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TYPE", type));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_FLAG", reqFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TO_DATE", toDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FROM_DATE", fromDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME", userName));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_006", input);

    }

    public DbDto getWorkM010_007(String yyyy,String mon,BigDecimal userSid,String day,BigDecimal seq,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YEAR", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_2", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_3", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_007", input);

    }

    public DbDto setWorkM010_013(String yyyy, String mm,BigDecimal userSid, String day, BigDecimal seq, String workTypeCode,String reqStart,String reqEnd,BigDecimal addHour
            ,BigDecimal nightHour,BigDecimal holidayHour,String remark,String tmpTerminalCode,String terminalCode,String teamCode,String reqFlag,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_WORK_TYPE_CODE", workTypeCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_START_TIME", reqStart));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_END_TIME", reqEnd));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_WORK_HOUR", addHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_NIGHT_WORK_HOUR", nightHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_HOLIDAY_WORK_HOUR", holidayHour));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REMARK", remark));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TMP_TERMINAL_CODE", tmpTerminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_FLAG", reqFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_013", input);

    }

    public DbDto setStatusUpdate(String yyyy, String mm,BigDecimal userSid, String day, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_STATUS_UPDATE", input);

    }

    public DbDto setWorkM010_014(String yyyy, String mm, String workUserId, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", workUserId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_014", input);

    }

    public DbDto setWorkM010_015(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String workTypeCode, BigDecimal addHour, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_WORK_TYPE_CODE", workTypeCode));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_WORK_HOUR", addHour));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_015", input);

    }

    public DbDto setWorkM010_016(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String classCode, String code, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_CODE", classCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_CODE", code));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_016", input);

    }

    public DbDto setWorkM010_022(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_022", input);

    }

    public DbDto setWorkM010_031(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String approveFlag, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_APPROVE_FLAG", approveFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_031", input);

    }

    public DbDto setWorkM010_032(String yyyy, String mm, String day, BigDecimal workUserSid, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_032", input);

    }

    public DbDto setWorkM010_017(String yyyy, String mon, String teamCode, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();


        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_017", input);

    }

    public DbDto setWorkM010_018(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,
                                 String imgType, String dir, String originName, String changeName, String fullPath,
                                 BigDecimal fileSize, String mime, String ext, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_IMG_TYPE", imgType));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DIR", dir));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_ORIGIN_NAME", originName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CHANGE_NAME", changeName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FULL_PATH", fullPath));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_FILE_SIZE", fileSize));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MIME", mime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_EXT", ext));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_018", input);

    }

    public DbDto setWorkM010_019(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String capsStart, String capsEnd, String startTime,
                                 String endTime, BigDecimal addDay, BigDecimal addWorkHour, BigDecimal nightWorkHour, String remark, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_START_TIME", capsStart));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_END_TIME", capsEnd));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_START_TIME", startTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_END_TIME", endTime));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_DAY", addDay));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_TOTAL_HOUR", addWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_NIGHT_WORK_HOUR", nightWorkHour));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REMARK", remark));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_019", input);

    }
}

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

    public DbDto getWorkL010_005(String date, String deptCode, String terminalCode, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYYMM", date));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));

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

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_005", input);

    }


    public DbDto getWorkM010_003(String date, BigDecimal userSid,BigDecimal seq,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYYMMDD", date));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

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

    public DbDto getWorkL010_007(String date, String monFlag,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYYMMDD", date));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON_FLAG", monFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_007", input);

    }

    public DbDto getWorkL010_006(String yyyy, String mon, String day, String deptCode, String terminalCode,String userName, String approveFlag, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YEAR", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
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

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_006", input);

    }

    public DbDto getWorkL010_008(String type,String reqFlag,String deptCode,String terminalCode,String toDate,String fromDate,String date,String userName,String otFlag,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TYPE", type));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_FLAG", reqFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TO_DATE", toDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FROM_DATE", fromDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DATE", date));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME", userName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_OT_FLAG", otFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_008", input);

    }

    public DbDto getWorkL010_009(String yyyy,String mon,BigDecimal userSid,String day,BigDecimal seq,String langCode, String guid, String userId, String ipAddr, String pgmId) {

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

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_009", input);

    }

    public DbDto getWorkM010_008(String yyyyTo,String monTo,String dayTo,String yyyyFrom,String monFrom,String dayFrom ,String date, String teamCode,String terminalCode,String type,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TO_YEAR", yyyyTo));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TO_MON", monTo));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TO_DAY", dayTo));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FROM_YEAR", yyyyFrom));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FROM_MON", monFrom));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FROM_DAY", dayFrom));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DATE", date));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TYPE", type));


        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_HEADER", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_2", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_008", input);

    }

    public DbDto getWorkM010_009(String toDate,String fromDate,String date ,String teamCode,String terminalCode,String userName,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TO_DATE", toDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FROM_DATE", fromDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DATE", date));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME", userName));


        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_2", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_009", input);

    }

    public DbDto getWorkM010_010(String toDate,String fromDate ,String date, String teamCode,String terminalCode,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TO_DATE", toDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FROM_DATE", fromDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DATE", date));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));


        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_010", input);

    }

    public DbDto setWorkL010_012(String yyyy, String mm,BigDecimal userSid, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_012", input);

    }

    public DbDto setWorkM010_021(String yyyy,
                                 String mm,
                                 String day,
                                 BigDecimal workUserSid,
                                 BigDecimal seq,
                                 String delFlag,
                                 String langCode,
                                 String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEL_FLAG", delFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_021", input);

    }

    public DbDto setWorkM010_022(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,String adminFlag, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_ADMIN_FLAG", adminFlag));

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

    public DbDto setWorkL010_014(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_014", input);

    }

    public DbDto setWorkL010_015(String yyyy, String mm, String day, BigDecimal workUserSid,BigDecimal seq,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_015", input);

    }

    public DbDto setWorkM010_037(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,
                                 String otStatus, String workTypeCode,String reqStartTime,String reqEndTime,String capsStartTime,String capsEndTime,
                                 BigDecimal addDay, BigDecimal addWorHour, BigDecimal nightWorkHour, BigDecimal holidayHour,BigDecimal holiAddHour,
                                 String deductFlag, String remark, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_OT_STATUS", otStatus));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_WORK_TYPE_CODE", workTypeCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_START_TIME", reqStartTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_END_TIME", reqEndTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_START_TIME", capsStartTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_END_TIME", capsEndTime));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_DAY", addDay));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_WORK_HOUR", addWorHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_NIGHT_WORK_HOUR", nightWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_HOLIDAY_WORK_HOUR", holidayHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_HOLIDAY_ADD_HOUR", holiAddHour));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEDUCT_FLAG", deductFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REMARK", remark));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_037", input);

    }

    public DbDto setWorkM010_034(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,String reqFlag,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_FLAG", reqFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_034", input);

    }

    public DbDto setWorkL010_016(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_016", input);

    }

    public DbDto setWorkL010_013(String yyyy, String mon, String teamCode,String terminalCode, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();


        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_013", input);

    }

    public DbDto setWorkM010_018(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,BigDecimal imgSeq,
                                 String imgType, String dir, String originName, String changeName, String fullPath,
                                 BigDecimal fileSize, String mime, String ext, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_IMG_TYPE", imgType));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_IMG_SEQ", imgSeq));
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

    public DbDto setWorkM010_019(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,String workTypeCode, String capsStart, String capsEnd, String startTime,
                                 String endTime, BigDecimal addDay,String deductFlag,String remark, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));//I_WORK_TYPE_CODE
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_WORK_TYPE_CODE", workTypeCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_START_TIME", capsStart));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_END_TIME", capsEnd));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_START_TIME", startTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_END_TIME", endTime));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_DAY", addDay));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEDUCT_FLAG", deductFlag));
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

    public DbDto setWorkM010_020(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String workTypeCode,String capsStart, String capsEnd, String startTime,
                                 String endTime, BigDecimal addDay, BigDecimal addWorkHour, BigDecimal nightWorkHour, BigDecimal holiWorkHour, BigDecimal holiAddHour,
                                 String remark, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_WORK_TYPE_CODE", workTypeCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_START_TIME", capsStart));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_END_TIME", capsEnd));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_START_TIME", startTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_END_TIME", endTime));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_DAY", addDay));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_WORK_HOUR", addWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_NIGHT_WORK_HOUR", nightWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_HOLIDAY_WORK_HOUR", holiWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_HOLIDAY_ADD_HOUR", holiAddHour));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REMARK", remark));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_020", input);

    }

    public DbDto setWorkL010_017(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq,
                                 String remark, String type,String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", workUserSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REMARK", remark));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TYPE", type));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_017", input);

    }

    public DbDto setWorkM010_038(String yyyy, String mm, String day, BigDecimal workUserSid, BigDecimal seq, String capsStart, String capsEnd, String startTime,
                                 String endTime, BigDecimal addDay, BigDecimal addWorkHour, BigDecimal nightWorkHour, BigDecimal holiWorkHour, BigDecimal holiAddHour,
                                 String remark, String langCode, String guid, String userId, String ipAddr, String pgmId) {

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
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_WORK_HOUR", addWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_NIGHT_WORK_HOUR", nightWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_HOLIDAY_WORK_HOUR", holiWorkHour));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_HOLIDAY_ADD_HOUR", holiAddHour));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REMARK", remark));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_038", input);

    }

    public DbDto setWorkL010_011(String yyyy, String mm,String workUserId, String day, BigDecimal seq,
                                 String workTypeCode,BigDecimal addHour,String tmpTerminalCode,String terminalCode,String teamCode,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", workUserId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_WORK_TYPE_CODE", workTypeCode));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ADD_WORK_HOUR", addHour));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TMP_TERMINAL_CODE", tmpTerminalCode));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_USER_SID", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_011", input);

    }

    public DbDto setWorkL010_018(String yyyy, String mm,BigDecimal userSid, String day, BigDecimal seq,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_018", input);

    }

    public DbDto setWorkM010_042(String yyyy, String mm,BigDecimal userSid, String day, BigDecimal seq,String imgType,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_IMG_TYPE", imgType));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_042", input);

    }

    public DbDto setWorkM010_043(String yyyy, String mm,BigDecimal userSid, String day, BigDecimal seq,String startTime,String endTime,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mm));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_START_TIME", startTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_END_TIME", endTime));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_043", input);

    }

    public DbDto getWorkL010_031(String yyyymm, BigDecimal userSid,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DATE", yyyymm));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_031", input);

    }

    public DbDto getWorkM010_004(String yyyy,String mon,String day, BigDecimal userSid,BigDecimal seq,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

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

    public DbDto setWorkM010_014(String yyyy,String mon,String day, BigDecimal userSid,BigDecimal seq,
                                 String code,String value1,String remark,String capsStartTime,String capsEndTime,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_CODE", code));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE1", value1));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REMARK", remark));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_START_TIME", capsStartTime));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CAPS_END_TIME", capsEndTime));

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

    public DbDto setWorkM010_025(String yyyy,String mon,String day, BigDecimal userSid,BigDecimal seq,
                                 String adminFlag,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_ADMIN_FLAG", adminFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_025", input);

    }

    public DbDto getWorkL010_032(String yyyy,String mon,String teamCode,String terminalCode,String userName,
                                 String approveFlag,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
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

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_032", input);

    }

    public DbDto setWorkM010_017(String yyyy,String mon,String day,BigDecimal userSid,BigDecimal seq,String typeCode,String approveId,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TYPE_CODE", typeCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_APPROVE_ID", approveId));

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

    public DbDto setWorkM010_026(String yyyy,String mon,String day,BigDecimal userSid,BigDecimal seq,String typeCode,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TYPE_CODE", typeCode));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_026", input);

    }

    public DbDto setWorkL010_019(String yyyy,String mon,String day,BigDecimal userSid,BigDecimal seq,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DAY", day));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_019", input);

    }

    public DbDto setWorkM010_005(String yyyy,String mon,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_HEADER", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_005", input);

    }

    public DbDto setWorkM010_006(String yyyy,String mon,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON", mon));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_HEADER", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_CAL", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_006", input);

    }

    public DbDto getWorkL010_033(String monFllag,String startDate,String endDate,String teamCode,String terminalCode,
                                 String userName,String fileter,
                                 String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MON_FLAG", monFllag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_START_DATE", startDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_END_DATE", endDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TEAM_CODE", teamCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE", terminalCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME", userName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FILTER", fileter));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_L010.PHM_WORK_REQUEST_L010_033", input);

    }

    public DbDto getWorkM010_007(String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR_HEADER", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_WORK_REQUEST_M010.PHM_WORK_REQUEST_M010_007", input);

    }

    public DbDto getHrdpt(){
        String sql="WITH TEAM_CNT AS (\n" +
                "    SELECT  TCM.VALUE2_CHAR AS HRDPT_CODE\n" +
                "        ,   COUNT(DISTINCT TCM.CODE_CODE) AS TEAM_COUNT\n" +
                "    FROM    TCM_CODE_MASTER TCM\n" +
                "    WHERE   TCM.CLASS_CODE = 'HRPAT'\n" +
                "    AND     TCM.USABLE_FLAG = 'Y'\n" +
                "    AND     TCM.CODE_CODE != TCM.VALUE2_CHAR\n" +
                "    GROUP BY TCM.VALUE2_CHAR\n" +
                "),\n" +
                "LATEST_HRPAT AS (\n" +
                "    SELECT  T.*\n" +
                "        ,   ROW_NUMBER() OVER (\n" +
                "                PARTITION BY T.USER_SID\n" +
                "                ORDER BY NVL(T.VALUE1, '00010101') DESC,\n" +
                "                         T.CREATED_TIME DESC\n" +
                "            ) AS RN\n" +
                "    FROM    TCM_INTRA_USER_RELATION T\n" +
                "    WHERE   T.CLASS_CODE = 'HRPAT'\n" +
                "    AND     T.USABLE_FLAG = 'Y'\n" +
                "),\n" +
                "USER_CNT AS (\n" +
                "    SELECT  IUR.CODE_CODE AS HRDPT_CODE\n" +
                "        ,   COUNT(DISTINCT IUR.USER_SID) AS USER_COUNT\n" +
                "        ,   COUNT(DISTINCT IUR2.CODE_CODE) AS TERMINAL_COUNT\n" +
                "    FROM    LATEST_HRPAT IUR\n" +
                "    LEFT JOIN TCM_INTRA_USER_RELATION IUR2\n" +
                "    ON      IUR2.USER_SID = IUR.USER_SID\n" +
                "    AND     IUR2.CLASS_CODE = 'TRMCD'\n" +
                "    AND     IUR2.USABLE_FLAG = 'Y'\n" +
                "    WHERE   IUR.RN = 1\n" +
                "    GROUP BY IUR.CODE_CODE\n" +
                ")\n" +
                "SELECT  TCM.CODE_CODE\n" +
                "    ,   TCM.CODE_NAME\n" +
                "    ,   TCM.ORDER_SEQ\n" +
                "    ,   TCM.VALUE1_NUMBER\n" +
                "    ,   TUM.USER_SID\n" +
                "    ,   NVL(TC.TEAM_COUNT, 0) AS TEAM_COUNT\n" +
                "    ,   CASE WHEN NVL(UC.USER_COUNT, 0) > 0\n" +
                "    \tAND\tUSER_SID\tIS\tNOT\tNULL\n" +
                "    \tTHEN \tNVL(UC.USER_COUNT, 0)-1\n" +
                "    \tELSE\tNVL(UC.USER_COUNT, 0)\n" +
                "    \tEND\t\tAS USER_COUNT" +
                "    ,   NVL(UC.TERMINAL_COUNT, 0) AS TERMINAL_COUNT\n" +
                "    ,   TCM3.VALUE4_NUMBER AS ORG_COL\n" +
                "FROM    TCM_CODE_MASTER TCM\n" +
                "LEFT JOIN TEAM_CNT TC\n" +
                "ON      TC.HRDPT_CODE = TCM.CODE_CODE\n" +
                "LEFT JOIN USER_CNT UC\n" +
                "ON      UC.HRDPT_CODE = TCM.CODE_CODE\n" +
                "LEFT JOIN TCM_USER_MASTER TUM\n" +
                "ON      TUM.USER_ID = TCM.VALUE2_CHAR\n" +
                "AND     TUM.USABLE_FLAG = 'Y'\n" +
                "LEFT JOIN TCM_CODE_MASTER TCM3\n" +
                "ON      TCM3.CLASS_CODE = 'HRPAT'\n" +
                "AND     TCM3.CODE_CODE = TCM.CODE_CODE\n" +
                "AND     TCM3.USABLE_FLAG = 'Y'\n" +
                "WHERE   TCM.CLASS_CODE = 'HRDPT'\n" +
                "AND     TCM.USABLE_FLAG = 'Y'\n" +
                "AND     TCM.VALUE1_NUMBER > 0\n" +
                "ORDER BY TCM.ORDER_SEQ";
        return callSql(sql);
    }

    public DbDto getOrgUserInfo(BigDecimal userSid){
        String sql = "SELECT  TCM.CODE_NAME || ' : ' || SYS_FUNCTION.FCM_GET_USER_NAME_BY_SID(IUR.USER_SID,'ENG') ORG_NAME\n" +
                "\t,\tIUR.VALUE1 \tMOBILE\n" +
                "\t,\tIUR.VALUE2\tPHONE\n" +
                "\t,\tURF.FULL_PATH\n" +
                "FROM\tTCM_INTRA_USER_RELATION IUR\n" +
                "JOIN\tTCM_CODE_MASTER\tTCM\n" +
                "ON\t\tTCM.CLASS_CODE\t=\t'HRORG'\n" +
                "AND\t\tTCM.CODE_CODE\t=\tIUR.CODE_CODE\n" +
                "AND\t\tTCM.USABLE_FLAG\t=\t'Y'\n" +
                "LEFT\n" +
                "JOIN\tTCM_USER_RELATION_FILE URF\n" +
                "ON\t\tIUR.USER_SID\t=\tURF.USER_SID\n" +
                "AND\t\tURF.USABLE_FLAG\t=\t'Y'\n" +
                "AND\t\tURF.TYPE\t\t=\t'A'\n" +
                "WHERE\tIUR.CLASS_CODE\t=\t'HRORG'\n" +
                "AND\t\tIUR.USER_SID\t= " +userSid.toString()+" "+
                "AND\t\tIUR.USABLE_FLAG\t=\t'Y'";

        return callSql(sql);
    }

    public DbDto getOrgTeamUser(String teamCode){
        String sql = "WITH TARGET AS (\n" +
                "    SELECT  T.*\n" +
                "        ,   ROW_NUMBER() OVER (\n" +
                "                PARTITION BY T.USER_SID\n" +
                "                ORDER BY NVL(T.VALUE1, '00010101') DESC,\n" +
                "                         T.CREATED_TIME DESC\n" +
                "            ) AS RN\n" +
                "    FROM    TCM_INTRA_USER_RELATION T\n" +
                "    WHERE   T.CLASS_CODE = 'HRPAT'\n" +
                "    AND     T.USABLE_FLAG = 'Y'\n" +
                ")\n" +
                "SELECT  TCM.CODE_NAME || ' : ' || TUM.USER_NAME AS ORG_NAME\n" +
                "    ,   IUR.VALUE1 AS MOBILE\n" +
                "    ,   IUR.VALUE2 AS PHONE\n" +
                "    ,   URF.FULL_PATH\n" +
                "    ,   IUR2.CODE_CODE AS TERMINAL_CODE\n" +
                "FROM    TCM_USER_MASTER TUM\n" +
                "JOIN    TARGET\n" +
                "ON      TARGET.USER_SID = TUM.USER_SID\n" +
                "AND     TARGET.RN = 1\n" +
                "JOIN    TCM_INTRA_USER_RELATION IUR2\n" +
                "ON      IUR2.CLASS_CODE = 'TRMCD'\n" +
                "AND     IUR2.USER_SID = TUM.USER_SID\n" +
                "AND     IUR2.USABLE_FLAG = 'Y'\n" +
                "JOIN\tTCM_INTRA_USER_RELATION\tIUR4\n" +
                "ON\t\tIUR4.CLASS_CODE\t=\t'HRWDT'\n" +
                "AND\t\tIUR4.CODE_CODE\t=\t'A'\n" +
                "AND\t\tIUR4.USER_SID\t=\tTUM.USER_SID\n" +
                "AND\t\tIUR4.USABLE_FLAG\t=\t'Y'\n" +
                "LEFT JOIN TCM_INTRA_USER_RELATION IUR\n" +
                "ON      IUR.USER_SID = TUM.USER_SID\n" +
                "AND     IUR.CLASS_CODE = 'HRORG'\n" +
                "AND     IUR.USABLE_FLAG = 'Y'\n" +
                "LEFT JOIN TCM_CODE_MASTER TCM\n" +
                "ON      TCM.CODE_CODE = IUR.CODE_CODE\n" +
                "AND     TCM.USABLE_FLAG = 'Y'\n" +
                "AND     TCM.CLASS_CODE = 'HRORG'\n" +
                "LEFT JOIN TCM_USER_RELATION_FILE URF\n" +
                "ON      URF.USER_SID = TUM.USER_SID\n" +
                "AND     URF.TYPE = 'A'\n" +
                "AND     URF.USABLE_FLAG = 'Y'\n" +
                "LEFT JOIN TCM_INTRA_USER_RELATION IUR3\n" +
                "ON\t\tIUR3.CLASS_CODE\t=\t'HRTAU'\n" +
                "AND\t\tIUR3.CODE_CODE\t=\tTARGET.CODE_CODE\n" +
                "AND\t\tIUR3.USER_SID\t=\tTUM.USER_SID\n" +
                "AND\t\tIUR3.USABLE_FLAG\t=\t'Y'\n" +
                "LEFT JOIN TCM_CODE_MASTER\tTCM2\n" +
                "ON\t\tTCM2.CLASS_CODE\t=\t'POSTN'\n" +
                "AND\t\tTCM2.CODE_CODE\t=\tIUR3.VALUE2\n" +
                "AND\t\tTCM2.USABLE_FLAG\t=\t'Y'\n" +
                "WHERE   NVL(TCM.VALUE1_NUMBER, 0) = 0\n" +
                "AND     TARGET.CODE_CODE = '" +teamCode+"' "+
                "ORDER BY IUR2.CODE_CODE\n" +
                "\t,\tCASE\tWHEN\tTCM2.CODE_CODE\tIS\tNULL\n" +
                "\t\tTHEN\t999\n" +
                "\t\tELSE\tTCM2.ORDER_SEQ\n" +
                "\t\tEND\n" +
                "\t,\tCASE\tWHEN\tTCM.CODE_CODE\tIS\tNULL\n" +
                "\t\tTHEN\t999\n" +
                "\t\tELSE\tTCM.ORDER_SEQ\n" +
                "\t\tEND\n" +
                "\t,\tNVL(IUR4.VALUE1,'00010101')\n" +
                "\t,\tTUM.USER_NAME";

        return callSql(sql);
    }

    public DbDto getRowTeam(String code){
        String sql = "SELECT TCM.CODE_CODE\n" +
                "\t,\tTCM.CODE_NAME\n" +
                "\t,\tTCM.ORDER_SEQ\n" +
                "\t,\tTCM.VALUE4_NUMBER\tORG_COL\n" +
                "\t,\tCOUNT(DISTINCT IUR.USER_SID)\tUSER_COUNT\n" +
                "FROM\tTCM_CODE_MASTER\tTCM\n" +
                "LEFT\n" +
                "JOIN\t(\n" +
                "\t\t\t\t\tSELECT\tT.*\n" +
                "\t\t\t\t\t\t,\tROW_NUMBER() OVER (\n" +
                "\t\t\t\t\t\t\t\t   PARTITION BY USER_SID\n" +
                "\t\t\t\t\t\t\t\t   ORDER BY NVL(VALUE1,'00010101')\tDESC\n" +
                "\t\t\t\t\t\t\t   ) AS RN\t\n" +
                "\t\t\t\t\tFROM\tTCM_INTRA_USER_RELATION T\n" +
                "\t\t\t\t\tWHERE\tT.CLASS_CODE\t=\t'HRPAT'\n" +
                "\t\t\t\t\tAND\t\tT.USABLE_FLAG\t=\t'Y'\n" +
                "\t\t\t)\tIUR\n" +
                "ON\t\tIUR.CODE_CODE\t=\tTCM.CODE_CODE\t\t\t\n" +
                "AND\t\tIUR.RN\t=\t1\n" +
                "LEFT\n" +
                "JOIN\tTCM_INTRA_USER_RELATION IUR2\n" +
                "ON\t\tIUR2.USER_SID\t=\tIUR.USER_SID\n" +
                "AND\t\tIUR2.CLASS_CODE\t=\t'TRMCD'\n" +
                "AND\t\tIUR2.USABLE_FLAG\t=\t'Y'\n" +
                "LEFT\n" +
                "JOIN\tTCM_INTRA_USER_RELATION IUR3\n" +
                "ON\t\tIUR3.USER_SID\t=\tIUR.USER_SID\n" +
                "AND\t\tIUR3.CLASS_CODE\t=\t'HRORG'\n" +
                "AND\t\tIUR3.USABLE_FLAG\t=\t'Y'\n" +
                "LEFT\n" +
                "JOIN\tTCM_CODE_MASTER\tTCM2\n" +
                "ON\t\tTCM2.CLASS_CODE\t=\t'HRORG'\n" +
                "AND\t\tTCM2.CODE_CODE\t=\tIUR3.CODE_CODE\n" +
                "AND\t\tTCM2.USABLE_FLAG\t=\t'Y'\n" +
                "WHERE TCM.CLASS_CODE\t=\t'HRPAT'\n" +
                "AND\tTCM.USABLE_FLAG\t=\t'Y'\n" +
                "AND\tTCM.CODE_CODE != TCM.VALUE2_CHAR\n" +
                "AND TCM.VALUE2_CHAR = '" +code+"' "+
                "AND\tTCM.CODE_CODE\t!=\t'1'\n" +
                "AND\tNVL(TCM2.VALUE1_NUMBER,0) = 0\n" +
                "GROUP BY TCM.CODE_CODE,\tTCM.CODE_NAME, TCM.VALUE4_NUMBER, TCM.ORDER_SEQ\n" +
                "ORDER BY TCM.ORDER_SEQ";

        return callSql(sql);
    }
}

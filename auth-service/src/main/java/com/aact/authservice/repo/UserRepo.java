package com.aact.authservice.repo;

import com.aact.common.BizBase;
import com.aact.common.DbDto;
import com.aact.common.DbTypeDTO;
import com.aact.common.DbTypeDTO.Type;
import com.aact.common.DbTypeDTO.Inout;
import com.aact.common.SourcName;
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
public class UserRepo extends BizBase {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public UserRepo(Map<SourcName, DataSource> multiDataSource, String mainSelect){
        super(multiDataSource,mainSelect);
        connect();
    }

    public DbDto getLogin(String userId, String userPassword) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_PASSWORD", userPassword));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("SYS_LOGIN.PCM_LOGIN", input);

    }

    public DbDto getMenu(String userId, String code) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_RELATION_CODE", code));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("SYS_LOGIN.PCM_GET_MENU", input);
    }

    public DbDto getUserInfo(String userId, String lang, String prgressGuid, String requestId, String requestIp,
                             String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_M010.PCM_USER_M010_001", input);
    }

    public DbDto getSch(String fltDate, String inoutFlag, String usableFlag, String carrier, String lang,
                        String prgressGuid, String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FLIGHT_DATE", fltDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_INOUT_FLAG", inoutFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USABLE_FLAG", usableFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_OPERATION_CARRIER", carrier));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_SCHEDULE_L010.PWM_SCHEDULE_L010_002", input);
    }

    public DbDto getSch003(String fltDate, String carrier, String inoutFlag, String oPcarrier, String usableFlag,
                           String lang, String prgressGuid, String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FLIGHT_DATE", fltDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CARRIER_CODE", carrier));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_INOUT_FLAG", inoutFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_OPERATION_CARRIER", oPcarrier));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USABLE_FLAG", usableFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_SCHEDULE_L010.PWM_SCHEDULE_L010_003", input);
    }

    public DbDto setUserInfo(String userId, String paas, String passHp, String userName1, String userName2,
                             String companyCode, String branchCode, String deptCode, String langCode, String email, String phone,
                             String mobile, String fax, String trmCode, String trmName, String workYn, String boardYn, String inYn,
                             String boardHpYn, String itYn, String lang, String prgressGuid, String requestId, String requestIp,
                             String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_PASSWORD", paas));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_PASSWORD_HP", passHp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME1", userName1));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME2", userName2));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_COMPANY_CODE", companyCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_BRANCH_CODE", branchCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPARTMENT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEFAULT_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_EMAIL_ADDRESS", email));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PHONE_NO", phone));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MOBILE_NO", mobile));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FAX_NO", fax));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_CODE_WORK", trmCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TERMINAL_NAME_WORK", trmName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_AUTH_WORKTIMELINE_YN", workYn));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_AUTH_BOARD_WRITE_YN", boardYn));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_AUTH_IN_CANCEL_YN", inYn));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_AUTH_BOARDHP_WRITE_YN", boardHpYn));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_AUTH_IT_BOARD_YN", itYn));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_USER_SID", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_M010.PCM_USER_M010_011", input);
    }

    public DbDto getUserList(String companyCode, String branchCode, String deptCode, String username, String lang,
                             String prgressGuid, String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_COMPANY_CODE", companyCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_BRANCH_CODE", branchCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPARTMENT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME", username));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_P010.PCM_USER_P010_001", input);
    }

    public DbDto setUserSign(BigDecimal userSid, String fileName, String mimeType, BigDecimal fileSize, byte[] data, String lang, String prgressGuid, String requestId, String requestIp,
                             String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FILE_NAME", fileName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MIME_TYPE", mimeType));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_FILE_SIZE", fileSize));
        input.add(new DbTypeDTO(Type.BLOB, Inout.IN, "I_DATA", data));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_M010.PCM_USER_M010_012", input);
    }

    public DbDto setUserRel(BigDecimal userSid,String classCode, String code,String yyyy,String value1,String value2,String value3,String value4, String lang, String prgressGuid, String requestId, String requestIp,
                            String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_CODE", classCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_CODE", code));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE1", value1));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE2", value2));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE3", value3));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE4", value4));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_M010.PCM_USER_M010_013", input);
    }

    public DbDto getUserRel(BigDecimal userSid, String usableFlag,String lang, String prgressGuid, String requestId, String requestIp,
                            String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USABLE_FLAG", usableFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_M010.PCM_USER_M010_002", input);
    }

    public DbDto delUserRel(BigDecimal userSid, String classCode,String code,String yyyy,String delFlag,String lang, String prgressGuid, String requestId, String requestIp,
                            String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_USER_SID", userSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_CODE", classCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_CODE", code));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_YYYY", yyyy));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PERMANENT_FLAG", delFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_M010.PCM_USER_M010_022", input);
    }

    public DbDto getUserList(String companyCode,String branchCode,String deptCode,String userId,String userName,String usableFlag, String lang, String prgressGuid, String requestId, String requestIp,
                             String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_COMPANY_CODE", companyCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_BRANCH_CODE", branchCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPARTMENT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_NAME", userName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USABLE_FLAG", usableFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_L010.PCM_USER_L010_001", input);
    }

    public DbDto setUserDelete(String userId,String usableFlag, String lang, String prgressGuid, String requestId, String requestIp,
                               String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USABLE_FLAG", usableFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_USER_M010.PCM_USER_M010_021", input);
    }
}

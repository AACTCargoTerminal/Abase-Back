package com.aact.infraservice.repo;

import com.aact.common.BizBase;
import com.aact.common.DbDto;
import com.aact.common.DbTypeDTO;
import com.aact.common.DbTypeDTO.*;
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
public class ApprRepo extends BizBase {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ApprRepo(Map<SourcName, DataSource> multiDataSource, String mainSelect){
        super(multiDataSource, mainSelect);
        connect();
    }

    public DbDto getApprM010_001(String title, String reqDeptCode, BigDecimal statusSid, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TITLE", title));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_DEPT_CODE", reqDeptCode));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_STATUS_SID", statusSid));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_APPR_M010.PHM_APPR_M010_001", input);
    }

    public DbDto getApprM010_002(BigDecimal apprId, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_APPR_ID", apprId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_APPR_M010.PHM_APPR_M010_002", input);
    }

    public DbDto getApprM010_003(BigDecimal apprId, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_APPR_ID", apprId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_APPR_M010.PHM_APPR_M010_003", input);
    }

    public DbDto setApprM010_010(BigDecimal apprId, String title, String reqDeptCode, String refDeptCodes, BigDecimal statusSid, String statusReason, BigDecimal currentApprSid, BigDecimal writerSid, String rejectReason, BigDecimal rejectBySid, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_APPR_ID", apprId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TITLE", title));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQ_DEPT_CODE", reqDeptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REF_DEPT_CODES", refDeptCodes));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_STATUS_SID", statusSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_STATUS_REASON", statusReason));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_CURRENT_APPR_SID", currentApprSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_WRITER_SID", writerSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REJECT_REASON", rejectReason));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_REJECT_BY_SID", rejectBySid));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_APPR_ID", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_APPR_M010.PHM_APPR_M010_010", input);
    }

    public DbDto setApprM010_011(BigDecimal apprId, String deptCode, String langCode, String guid, String userId, String ipAddr, String pgmId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_APPR_ID", apprId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_APPR_M010.PHM_APPR_M010_011", input);
    }

    public DbDto setApprM010_020(BigDecimal apprId, String langCode, String guid, String userId, String ipAddr, String pgmId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_APPR_ID", apprId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_APPR_M010.PHM_APPR_M010_020", input);
    }

    public DbDto setApprM010_021(BigDecimal apprId, String deptCode, String langCode, String guid, String userId, String ipAddr, String pgmId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_APPR_ID", apprId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_DEPT_CODE", deptCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", langCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", guid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", userId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", ipAddr));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", pgmId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_APPR_M010.PHM_APPR_M010_021", input);
    }
}

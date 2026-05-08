package com.aact.sysservice.repo;

import com.aact.common.DbDto;
import com.aact.common.DbTypeDTO;
import com.aact.common.DbTypeDTO.Inout;
import com.aact.common.DbTypeDTO.Type;
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
public class SysRepo extends BizBase {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SysRepo(Map<SourcName, DataSource> multiDataSource, String mainSelect){
        super(multiDataSource,mainSelect);
        connect();
    }

    public DbDto getAllCode(){
        DbDto ret = null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT  COD.* ");
        sql.append(",   NVL(OBL.OBJECT_NAME, COD.CODE_NAME)     CODE_NAME2 ");
        sql.append("FROM    TCM_CODE_MASTER                 COD ");
        sql.append("LEFT JOIN    TCM_OBJECT_LANGUAGE             OBL ");
        sql.append("ON      OBL.OBJECT_SID          =       COD.CODE_SID ");
        sql.append("AND     OBL.LANGUAGE_CODE       =       'KOR' ");
        sql.append(" WHERE   COD.USABLE_FLAG         =       'Y' ");
        sql.append("ORDER   BY COD.CLASS_CODE,COD.ORDER_SEQ");
        ret = callSql(sql.toString());
        return ret;
    }

    public DbDto getCodeP010(String classCode, String codeName, String value1Char, String value2Char, String value3Char,
                             String value4Char, String value5Char, String value6Char, String value7Char, String value8Char,
                             String value9Char, String lang, String prgressGuid, String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_CODE", classCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_NAME", codeName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE1_CHAR", value1Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE2_CHAR", value2Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE3_CHAR", value3Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE4_CHAR", value4Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE5_CHAR", value5Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE6_CHAR", value6Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE7_CHAR", value7Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE8_CHAR", value8Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE9_CHAR", value9Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_CODE_P010.PCM_CODE_P010_001", input);
    }

    public String getEDMValue() {
        String ret = "";
        String sql = "SELECT  SCM_OBJECT_SID.NEXTVAL AS COL1 FROM DUAL";

        DbDto dto = callSql(sql);
        if (dto.getErrFlag().equalsIgnoreCase("N")) {
            ret = dto.getResult().get(0).get(0).get("COL1").getObj().toString();
        }
        return ret;
    }

    public DbDto getSchP010_001(String fltDate, String inoutFlag, String lang, String prgressGuid, String requestId,
                                String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_INOUT_FLAG", inoutFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_FLIGHT_DATE", fltDate));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_SCHEDULE_P010.PWM_SCHEDULE_P010_001", input);
    }

    public DbDto getSchM010_001(BigDecimal schSid, String lang, String prgressGuid, String requestId, String requestIp,
                                String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SCHEDULE_SID", schSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_SCHEDULE_M010.PWM_SCHEDULE_M010_001", input);
    }

    public DbDto setUldBuildUpM010_013(BigDecimal cargoSid, String mfstWt, String lang, String prgressGuid,
                                       String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_CARGO_CONTROL_SID", cargoSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MFST_WEIGHT", mfstWt));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_ULD_BUILD_UP_M010.PWM_ULD_BUILD_UP_M010_013", input);
    }

    public DbDto getCustomerP010_001(String customerName, String registrationNo, String customerFlag, String vendorFlag,
                                     String carrierFlag, String agencyFlag, String customsFlag, String iataFlag, String lang, String prgressGuid,
                                     String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CUSTOMER_NAME", customerName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REGISTRATION_NO", registrationNo));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CUSTOMER_FLAG", customerFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VENDOR_FLAG", vendorFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CARRIER_FLAG", carrierFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_AGENCY_FLAG", agencyFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CUSTOMS_FLAG", customsFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_IATA_FLAG", iataFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_CUSTOMER_P010.PCM_CUSTOMER_P010_001", input);
    }

    public DbDto getCustomerL010_002(BigDecimal customerSid, String usableFlag, String lang, String prgressGuid,
                                     String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_CUSTOMER_SID", customerSid));
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

        return callProc("USR_CUSTOMER_L010.PCM_CUSTOMER_L010_002", input);
    }

    public DbDto setEmailManageL010_012(String emailGuid, String msgGuid, BigDecimal schSid, String mawb,
                                        String mailTitle, String mailContents, String mailType, String senderEmail, String lang, String prgressGuid,
                                        String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_EMAIL_GUID", emailGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MESSAGE_GUID", msgGuid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_SCHEDULE_SID", schSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MASTER_AWB_NO", mawb));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MAIL_TITLE", mailTitle));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MAIL_CONTENTS", mailContents));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_MAIL_TYPE", mailType));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_SENDER_EMAIL", senderEmail));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_EMAIL_MANAGEMENT.PWM_EMAIL_MANAGEMENT_L010_012", input);
    }

    public DbDto setEmailManageL010_013(String emailGuid, String refGuid, String refType, String refEmail, String lang,
                                        String prgressGuid, String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_EMAIL_GUID", emailGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REFERENCE_GUID", refGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REF_TYPE", refType));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REF_EMAIL", refEmail));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_EMAIL_MANAGEMENT.PWM_EMAIL_MANAGEMENT_L010_013", input);
    }

    public DbDto getBoardL010_001(String usableFlag, String lang,
                                  String prgressGuid, String requestId, String requestIp, String programId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

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

        return callProc("USR_BOARD_L010.PCM_BOARD_L010_001", input);

    }

    public DbDto getCodeA010_001(String className,String sysFlag,String usableFlag, String lang,String prgressGuid, String requestId, String requestIp, String programId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_NAME", className));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_SYSTEM_FLAG", sysFlag));
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

        return callProc("USR_CODE_A010.PCM_CODE_A010_001", input);

    }

    public DbDto getCodeA010_002(String classCode, String codeName, String usableFlag, String lang, String prgressGuid, String requestId, String requestIp, String programId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_CODE", classCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_NAME", codeName));
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

        return callProc("USR_CODE_A010.PCM_CODE_A010_002", input);

    }

    public DbDto setCodeA010_012(String classCode,String code,String asisCode,String codeName1,String codeName2,String codeDesc,String value1Char,
                                 String value2Char,String value3Char,String value4Char,String value5Char,String value6Char,String value7Char,String value8Char,
                                 String value9Char,BigDecimal value1Num,BigDecimal value2Num,BigDecimal value3Num,BigDecimal value4Num,BigDecimal value5Num,BigDecimal value6Num
            ,BigDecimal value7Num,BigDecimal value8Num,BigDecimal value9Num,BigDecimal seq,
                                 String lang,String prgressGuid, String requestId, String requestIp, String programId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_CODE", classCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_CODE", code));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_ASIS_CODE", asisCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_NAME1", codeName1));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_NAME2", codeName2));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_DESCRIPTION", codeDesc));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE1_CHAR", value1Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE2_CHAR", value2Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE3_CHAR", value3Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE4_CHAR", value4Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE5_CHAR", value5Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE6_CHAR", value6Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE7_CHAR", value7Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE8_CHAR", value8Char));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_VALUE9_CHAR", value9Char));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE1_NUMBER", value1Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE2_NUMBER", value2Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE3_NUMBER", value3Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE4_NUMBER", value4Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE5_NUMBER", value5Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE6_NUMBER", value6Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE7_NUMBER", value7Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE8_NUMBER", value8Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_VALUE9_NUMBER", value9Num));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_ORDER_SEQ", seq));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_CODE_A010.PCM_CODE_A010_012", input);

    }

    public DbDto setCodeA010_022(String classCode,String code,String delFlag, String lang,String prgressGuid, String requestId, String requestIp, String programId) {

        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CLASS_CODE", classCode));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_CODE_CODE", code));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PERMANENT_FLAG", delFlag));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));

        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_CODE_A010.PCM_CODE_A010_022", input);

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
}

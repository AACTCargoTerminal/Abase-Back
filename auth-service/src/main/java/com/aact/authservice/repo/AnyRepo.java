package com.aact.authservice.repo;

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
public class AnyRepo extends BizBase {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public AnyRepo(Map<SourcName, DataSource> multiDataSource, String mainSelect){
        super(multiDataSource,mainSelect);
        connect();
    }

    public DbDto getAnyList(String usableFlag, String lang, String prgressGuid, String requestId, String requestIp,
                            String programId) {
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

        return callProc("ANY_BOARD_L010.ANY_BOARD_L010_001", input);
    }

    public DbDto getAnyData(BigDecimal boardSid, String boardPass, String lang, String prgressGuid, String requestId,
                            String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_BOARD_SID", boardSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_BOARD_PASSWORD", boardPass));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR", ""));
        input.add(new DbTypeDTO(Type.CURSOR, Inout.OUT, "O_RESULT_CURSOR2", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("ANY_BOARD_M010.ANY_BOARD_M010_001", input);
    }

    public DbDto setAnyData(BigDecimal boardSid, String boardTitle, String boardPass, String boardMsg, String lang,
                            String prgressGuid, String requestId, String requestIp, String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.INOUT, "I_BOARD_SID", boardSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_BOARD_TITLE", boardTitle));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_BOARD_PASSWORD", boardPass));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_BOARD_MESSAGE", boardMsg));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("ANY_BOARD_M010.ANY_BOARD_M010_011", input);
    }

    public DbDto setAnyFile(BigDecimal boardSid, BigDecimal edmSid, String originName, String filePath,
                            BigDecimal fileSize, String usableFlag, String lang, String prgressGuid, String requestId, String requestIp,
                            String programId) {
        List<DbTypeDTO> input = new ArrayList<DbTypeDTO>();

        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_BOARD_SID", boardSid));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_EDM_SID", edmSid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_SOURCE_FILE_NAME", originName));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_TARGET_FILE_NAME", filePath));
        input.add(new DbTypeDTO(Type.DECIMAL, Inout.IN, "I_FILE_SIZE", fileSize));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_USABLE_FLAG", usableFlag));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_LANGUAGE_CODE", lang));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_PROGRESS_GUID", prgressGuid));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_USER_ID", requestId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_IP_ADDRESS", requestIp));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.IN, "I_REQUEST_PROGRAM_ID", programId));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_ERROR_FLAG", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_CODE", ""));
        input.add(new DbTypeDTO(Type.VARCHAR, Inout.OUT, "O_RETURN_MESSAGE", ""));

        return callProc("USR_BOARD_M010.PCM_BOARD_M010_012", input);
    }
}

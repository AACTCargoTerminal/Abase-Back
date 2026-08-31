package com.aact.infraservice.service;

import com.aact.common.*;
import com.aact.infraservice.dto.ApprDTO;
import com.aact.infraservice.repo.ApprRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApprService extends ServiceBase {

    private final ObjectProvider<ApprRepo> apprRepoProvider;

    public ResponseDTO<?> getApprM010_001(ApprDTO apprDTO) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        DbDto dbRet = repo.getApprM010_001(apprDTO.getTitle(), apprDTO.getReqDeptCode(), apprDTO.getStatusSid(),
                info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());

        return okOrThrow("getApprM010_001", dbRet);
    }

    public ResponseDTO<?> getApprM010_002(BigDecimal apprId) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        DbDto dbRet = repo.getApprM010_002(apprId, info.getUserLang(), Util.getGUID(),
                info.getUserId(), info.getUserIpAddress(), info.getPgmId());

        return okOrThrow("getApprM010_002", dbRet);
    }

    public ResponseDTO<?> getApprM010_003(BigDecimal apprId) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        DbDto dbRet = repo.getApprM010_003(apprId, info.getUserLang(), Util.getGUID(),
                info.getUserId(), info.getUserIpAddress(), info.getPgmId());

        return okOrThrow("getApprM010_003", dbRet);
    }

    public ResponseDTO<?> setApprM010_010(ApprDTO apprDTO) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        return execute(repo, () -> {
            String guid = Util.getGUID();
            String refDeptCodes = apprDTO.getRefDeptCodes() == null ? "" : String.join(",", apprDTO.getRefDeptCodes());
            DbDto dbRet = repo.setApprM010_010(apprDTO.getApprId(), apprDTO.getTitle(), apprDTO.getReqDeptCode(), refDeptCodes, apprDTO.getStatusSid(), apprDTO.getStatusReason(), apprDTO.getCurrentApprSid(), apprDTO.getWriterSid(), apprDTO.getRejectReason(), apprDTO.getRejectBySid(), info.getUserLang(), guid, info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("setApprM010_010", dbRet);
        });
    }

    public ResponseDTO<?> setApprM010_020(BigDecimal apprId) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        return execute(repo, () -> {
            String guid = Util.getGUID();
            DbDto dbRet = repo.setApprM010_020(apprId, info.getUserLang(), guid, info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("setApprM010_020", dbRet);
        });
    }

    public ResponseDTO<?> setApprM010_021(BigDecimal apprId, String deptCode) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        return execute(repo, () -> {
            String guid = Util.getGUID();
            DbDto dbRet = repo.setApprM010_021(apprId, deptCode, info.getUserLang(), guid, info.getUserId(), info.getUserIpAddress(), info.getPgmId());
            return okOrThrow("setApprM010_021", dbRet);
        });
    }
}
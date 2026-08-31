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

    // 기안 목록 조회
    public ResponseDTO<?> getList(ApprDTO apprDTO) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getList(apprDTO.getTitle(), apprDTO.getReqDeptCode(), apprDTO.getStatusSid(),
                    info.getUserLang(), Util.getGUID(), info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getList", dbRet);
        });
    }

    // 기안 상세 조회
    public ResponseDTO<?> getDetail(BigDecimal apprId) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getDetail(apprId, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getDetail", dbRet);
        });
    }

    // 기안 참조부서 조회
    public ResponseDTO<?> getRefList(BigDecimal apprId) {
        ClsUserInfo info = UserContext.get();
        ApprRepo repo = apprRepoProvider.getObject();

        return execute(repo, () -> {
            DbDto dbRet = repo.getRefList(apprId, info.getUserLang(), Util.getGUID(),
                    info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("getRefList", dbRet);
        });
    }


    // 기안 등록 / 수정
    public ResponseDTO<?> save(ApprDTO apprDTO) {
        ClsUserInfo info = UserContext.get();

        ApprRepo repo = apprRepoProvider.getObject();
        checkAdminDept(repo, info);

        return execute(repo, () -> {
            String guid = Util.getGUID();
            String refDeptCodes = apprDTO.getRefDeptCodes() == null ? "" : String.join(",", apprDTO.getRefDeptCodes());

            DbDto dbRet = repo.save(apprDTO.getApprId(), apprDTO.getTitle(), apprDTO.getReqDeptCode(), refDeptCodes, apprDTO.getStatusSid(), apprDTO.getStatusReason(), apprDTO.getCurrentApprSid(), apprDTO.getWriterSid(), apprDTO.getRejectReason(), apprDTO.getRejectBySid(), info.getUserLang(), guid, info.getUserId(), info.getUserIpAddress(), info.getPgmId());

            return okOrThrow("save", dbRet);
        });
    }

    public ResponseDTO<?> delete(BigDecimal apprId) {
        ClsUserInfo info = UserContext.get();
        String guid = Util.getGUID();

        ApprRepo repo = apprRepoProvider.getObject();
        checkAdminDept(repo, info);

        DbDto dbRet = repo.delete(apprId, info.getUserLang(), guid, info.getUserId(), info.getUserIpAddress(), info.getPgmId());

        return okOrThrow("disable", dbRet);
    }

    public ResponseDTO<?> deleteRef(BigDecimal apprId, String deptCode) {
        ClsUserInfo info = UserContext.get();
        String guid = Util.getGUID();

        ApprRepo repo = apprRepoProvider.getObject();
        checkAdminDept(repo, info);

        DbDto dbRet = repo.deleteRef(apprId, deptCode, info.getUserLang(), guid, info.getUserId(), info.getUserIpAddress(), info.getPgmId());

        return okOrThrow("deleteRef", dbRet);
    }

    // 접근 권한 확인
    private void checkAdminDept(ApprRepo repo, ClsUserInfo info) {
        DbDto dbRet = repo.getAdminDept(info.getUserId());

        if ("Y".equalsIgnoreCase(dbRet.getErrFlag())) {
            throw new BizException("checkAdminDept", dbRet.getErrMsg());
        }

        ResponseDTO<Map<Integer, List<Map<String, Object>>>> ret = ResponseDTO.from(dbRet);
        BigDecimal adminCount = Util.getDecimal(ret.getData().get(0).get(0).get("ADMIN_COUNT"));

        if (adminCount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BizException("checkAdminDept", "처리 권한이 없습니다.");
        }
    }
}
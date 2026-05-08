package com.aact.common;

import java.util.function.Supplier;

public abstract class ServiceBase {

    protected <T> T execute(Supplier<T> action) {
        try {

            T result = action.get();

            return result;

        } catch (Exception e) {

            throw e;
        }
    }

    protected <T> T execute(BizBase repo, Supplier<T> action) {
        try {
            repo.beginTrans();

            T result = action.get();

            repo.commit();
            return result;

        } catch (Exception e) {

            repo.rollback();
            throw e;
        } finally {
            repo.close();
        }
    }

    protected <T> T execute(BizBase repo, SourcName sourcType, Supplier<T> action) {
        try {
            repo.beginTrans(sourcType);

            T result = action.get();

            repo.commit();
            return result;

        } catch (Exception e) {
            repo.rollback();
            throw e;
        } finally {
            repo.close();
        }
    }

    // ResponseDTO 전용 오버로드 (편의)
    public ResponseDTO<?> okOrThrow(String type, DbDto dbRet) {
        if (dbRet == null)
            throw new SysException(type, "Server 에러: service return is null");
        if ("N".equalsIgnoreCase(dbRet.getErrFlag()))
            return ResponseDTO.from(dbRet);
        throw new BizException(type, dbRet.getErrMsg());
    }

    // ResponseDTO 전용 오버로드 (편의)
    public <T> ResponseDTO<T> okOrThrow(String type, ResponseDTO<T> dbRet) {
        if (dbRet == null)
            throw new SysException(type, "Server 에러: service return is null");
        if ("N".equalsIgnoreCase(dbRet.getErrFlag()))
            return dbRet;
        throw new BizException(type, dbRet.getErrMsg());
    }
}

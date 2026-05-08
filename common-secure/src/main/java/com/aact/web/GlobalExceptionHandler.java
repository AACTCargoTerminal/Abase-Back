package com.aact.web;

import com.aact.common.BizException;
import com.aact.common.ResponseDTO;
import com.aact.common.SysException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<?> handleBiz(BizException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ResponseDTO.setError(e.getType(), e).getErrMsg());
    }

    @ExceptionHandler(SysException.class)
    public ResponseEntity<?> handleSys(SysException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.setError(e.getType(), e).getErrMsg());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDB(DataAccessException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.setError("CapsDB", e).getErrMsg());
    }

    // 혹시 남는 예외는 전부 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.setError("", e).getErrMsg());
    }

}
package com.inrsystem.exception;

import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class LocalExceptionHandler {
    @ExceptionHandler(LocalRunTimeException.class)
    public Response<String> handleCustomException(LocalRunTimeException ex) {
        log.error("自定义异常信息: ex={}", ex.getMessage(), ex);
        return Response.fail(ex.getErrorEnum().getErrCode(), ex.getMessage());
    }

    /**
     * 默认全局异常处理。
     *
     * @param e the e
     * @return Response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<String> exception(Exception e) {
        log.error("全局异常信息: ex={}", e.getMessage(), e);
        return Response.fail(ErrorEnum.COMMON_ERROR.getErrCode(), e.getMessage());
    }
}


package com.jwell56.security.cloud.service.ids.common;

import com.jwell56.security.cloud.common.ResultObject;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * @author wsg
 * @since 2020/11/24
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResultObject globalException(Exception e) {
        return ResultObject.exception(e);
    }

    @ExceptionHandler(IOException.class)
    public ResultObject iOException(Exception e) {
        return ResultObject.exception(e);
    }
}
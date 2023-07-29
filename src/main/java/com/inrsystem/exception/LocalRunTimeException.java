package com.inrsystem.exception;

import com.inrsystem.enums.ErrorEnum;
import lombok.Data;

@Data
public class LocalRunTimeException extends RuntimeException {
    private ErrorEnum errorEnum;

    public LocalRunTimeException(String message) {
        super(message);
    }

    public LocalRunTimeException(ErrorEnum errorEnum) {
        super(errorEnum.getErrMsg());
        this.errorEnum = errorEnum;
    }
}

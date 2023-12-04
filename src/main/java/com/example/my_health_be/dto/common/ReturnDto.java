package com.example.my_health_be.dto.common;

import com.example.my_health_be.domain.enums.ReturnCode;
import lombok.Data;

@Data
public class ReturnDto<T> {
    private final Integer code;
    private final String httpStatus;
    private final String message;
    private final T data;

    public static <T> ReturnDto<T> ok(T data) {
        return new ReturnDto<>(ReturnCode.OK.getCode(), ReturnCode.OK.getHttpStatus(), ReturnCode.OK.getMessage(), data);
    }

    public static ReturnDto<Void> ok() {
        return new ReturnDto<>(ReturnCode.OK.getCode(), ReturnCode.OK.getHttpStatus(), ReturnCode.OK.getMessage(), null);
    }

    public static ReturnDto<?> fail() {
        return new ReturnDto<>(ReturnCode.BAD_REQUEST.getCode(), ReturnCode.BAD_REQUEST.getHttpStatus(), ReturnCode.BAD_REQUEST.getMessage(), null);
    }
}

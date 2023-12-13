package com.example.my_health_be.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnCode {

    OK(200, "OK", "요청에 성공하였습니다."),
    BAD_REQUEST(400, "Bad Request", "잘못된 요청입니다."),
    FORBIDDEN(403, "Forbidden", "권한이 없습니다");


    private final Integer code;
    private final String httpStatus;
    private final String message;
}

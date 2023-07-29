package com.inrsystem.response;

import lombok.Data;

@Data
public class Response<T> {
    private Boolean success;
    private Integer errCode;
    private String errMsg;
    private T data;

    public static <T> Response<T> success(T data) {
        Response<T> Response = new Response<>();
        Response.setSuccess(true);
        Response.setData(data);
        return Response;
    }

    public static <T> Response<T> fail(int code, String message) {
        Response<T> Response = new Response<>();
        Response.setSuccess(false);
        Response.setErrCode(code);
        Response.setErrMsg(message);
        return Response;
    }
}

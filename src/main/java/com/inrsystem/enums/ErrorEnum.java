package com.inrsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    COMMON_ERROR(1000, "错误"),
    UNFILLED_ID(4001, "没有填写账号"),
    UNFILLED_PASSWORD(4002, "没有填写密码"),
    INVALID_ID(4003, "账号或密码错误"),
    TOKEN_ERROR(4004, "TOKEN错误"),
    EMPTY_TOKEN(4005, "TOKEN为空"),
    AUTHORITY_ERROR(4006, "权限不足"),
    GRADE_NO_EXIST(5001, "成绩不存在"),

    REDIS_ERROR(9001, "redis错误"),
    REDIS_NOT_EXIST(9002, "登录过期"),
    OBJECT_TO_MAP_ERROR(10001, "自定义类转化为map出错"),
    NOT_EXIST(10002,"该用户不存在"),
    EVENT_NOT_FIND(10003,"未查询到任何项目详细内容"),
    ERROR_REMARK(10004,"未打分成功"),
    ERROR_GET_ACHIEVEMENT_INFORMATION(10005,"获取科研成果失败"),
    ERROR_INSERT(10006,"insert失败"),
    NAME_EXIST(10007,"该队名已存在"),
    ERROR_GET_MEMBER_INFORMATION(10008,"未获取到成员信息"),
    ERROR_CREAT_TEAM(10009,"创建团队失败"),

    ERROR_ADD_ACHIEVEMENT(20002,"添加成就失败"),
    NOT_REMARK_THIS_EVENT(20003,"尚未审核"),
   MULTIPLY_NAME(40003,"该队名已存在"),
   MULTIPLY_ENTER(40004,"该成员已加入其他科研团队"),
   NOT_SIGN(40005,"账号密码不存在"),
   NOT_END(40006,"尚未结束投标进程");


    private final Integer errCode;
    private final String errMsg;
}
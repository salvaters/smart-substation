package com.smartsubstation.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "资源冲突"),

    // 服务器错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 业务错误码 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    TOKEN_INVALID(1004, "Token无效或已过期"),
    TOKEN_EXPIRED(1005, "Token已过期"),

    // 业务错误码 2xxx
    STATION_NOT_FOUND(2001, "变电站不存在"),
    DEVICE_NOT_FOUND(2002, "设备不存在"),
    DEVICE_QR_CODE_EXISTS(2003, "设备二维码已存在"),
    TASK_NOT_FOUND(2004, "巡检任务不存在"),
    TASK_ALREADY_COMPLETED(2005, "任务已完成"),
    DEFECT_NOT_FOUND(2006, "缺陷记录不存在"),
    REPORT_NOT_FOUND(2007, "报告不存在"),
    TEMPLATE_NOT_FOUND(2008, "巡检模板不存在"),

    // 文件错误码 3xxx
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),
    FILE_SIZE_EXCEEDED(3002, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(3003, "文件类型不允许"),
    FILE_NOT_FOUND(3004, "文件不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

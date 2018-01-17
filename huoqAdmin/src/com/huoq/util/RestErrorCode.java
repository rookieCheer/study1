package com.huoq.util;

/**
 * 错误代码
 *
 * @author
 */
public enum RestErrorCode {

    /** ------------------ 全局异常信息 ----------------- */

    SYS_SUCCESS(0, "操作成功"),
    
    OPERATION_FAILURE(1, "操作失败"),

    SYS_ERROR(9999, "系统异常"),

    MISSING_ARGS(9998, "缺失参数"),
    
    SIGN_CHECK_FAIL(9997, "签名校验失败"),
    
    NO_AUTHORITY(9998, "非法请求用户"),

    /** ------------------ 打款以11开头 ----------------- */
    LOAN_SUCCESS(11018, "放款成功"),
    
    LOAN_FAILURE(11019, "放款失败"),
    
    
    
    
    
    SERVICE_IS_BUSY(19006, "服务繁忙, 请稍后再试");
    private final int    code;      // 错误码
    private final String message; // 错误原因

    private RestErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static RestErrorCode valueOf(int errorCode) {
        for (RestErrorCode status : values()) {
            if (status.code == errorCode) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + errorCode + "]");
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

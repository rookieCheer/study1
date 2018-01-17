package com.huoq.assetSide.action;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String            message;
    private Integer           code;
    private Object            data;

    public BaseResponse() {
        this.setCode(0);
        this.setMsg("成功");
        this.setData("");
    }

    public BaseResponse(String message, Integer code) {
        super();
        this.message = message;
        this.code = code;
    }

    public void setMsg(String message) {
        if (StringUtils.isNotBlank(message) && (message.indexOf("Exception") != -1 || message.equals("null"))) {
            this.message = "网络连接超时";
        } else {
            this.message = message;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

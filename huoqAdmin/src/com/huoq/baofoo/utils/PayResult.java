/**
 * www.xinhuajindian.com Inc. Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.baofoo.utils;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.io.Serializable;

/**
 * 宝付支付结果集
 * 
 * @author guoyin.yi
 * @version $Id: PayResult.java, v 0.1 2017/12/27 Exp $
 */
public class PayResult implements Serializable{

    private String code;   // 编码
    private String message;// 信息
    private JSONObject dataModel; //返回的数据

    public PayResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public PayResult(BaofooResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.dataModel = null;
    }
    public PayResult(JSONObject dataModel) {
        this.dataModel = dataModel;
        this.code = "0000";
        this.message = "成功";
    }
    public PayResult(BaofooResultCode resultCode,JSONObject dataMode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.dataModel = dataModel;
    }

    public PayResult(String resp_code, String resp_msg, JSONObject jsonMap) {
        this.code = resp_code;
        this.message = resp_msg;
        this.dataModel = jsonMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getDataModel() {
        return dataModel;
    }

    public void setDataModel(JSONObject dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public String toString() {
        return "PayResult{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", dataModel=" + dataModel +
                '}';
    }
}

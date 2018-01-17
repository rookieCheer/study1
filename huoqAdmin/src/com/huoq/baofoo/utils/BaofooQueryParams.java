/**
 * www.xinhuajindian.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.baofoo.utils;

import java.io.Serializable;

/**
 * 查询参数
 * @author guoyin.yi
 * @version $Id: BaofooQueryParams.java, v 0.1  2017/12/28 Exp $
 * @email guoyin.yi@xinhuajindian.com
 */
public class BaofooQueryParams implements Serializable{
    private static final long serialVersionUID = -1501018631439440820L;

    private String transBatchid;//批次号
    private String transNo;//订单号

    public BaofooQueryParams() {
    }

    public String getTransBatchid() {
        return transBatchid;
    }

    public void setTransBatchid(String transBatchid) {
        this.transBatchid = transBatchid;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    @Override
    public String toString() {
        return "BaofooQueryParams{" +
                "transBatchid='" + transBatchid + '\'' +
                ", transNo='" + transNo + '\'' +
                '}';
    }
}

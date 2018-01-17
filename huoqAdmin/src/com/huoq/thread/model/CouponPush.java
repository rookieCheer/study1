/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.model;

import java.io.Serializable;

/**
 * @author guoyin.yi
 * @version $Id: CouponPush.java, v 0.1  2017/12/16 Exp $
 */
public class CouponPush implements Serializable{

    private Long usersId;//用户Id;
    private Long couponCount;

    public CouponPush() {
    }

    public Long getUsersId() {
        return usersId;
    }

    public void setUsersId(Long usersId) {
        this.usersId = usersId;
    }

    public Long getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(Long couponCount) {
        this.couponCount = couponCount;
    }
}

package com.huoq.orm;

import java.util.Date;

/**
 * 投资卷发放规则
 *
 * Created by 陈冬明 on 19/09/2017.
 */
public class CouponReleaseRecord {
    private Integer id;
    private Date createTime; //创建时间
    private Users fromUser; // 来源人
    private Users toUser; // 使用人
    private Coupon coupon; // 对应产生的Coupon
    private CouponReleaseRuleItem couponReleaseRuleItem; // 根据哪个发放规则项产生

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Users getFromUser() {
        return fromUser;
    }

    public void setFromUser(Users fromUser) {
        this.fromUser = fromUser;
    }

    public Users getToUser() {
        return toUser;
    }

    public void setToUser(Users toUser) {
        this.toUser = toUser;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public CouponReleaseRuleItem getCouponReleaseRuleItem() {
        return couponReleaseRuleItem;
    }

    public void setCouponReleaseRuleItem(CouponReleaseRuleItem couponReleaseRuleItem) {
        this.couponReleaseRuleItem = couponReleaseRuleItem;
    }
}

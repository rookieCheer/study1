package com.huoq.orm;

/**
 * 优惠券发放规则项
 * Created by 陈冬明 on 19/09/2017.
 */
public class CouponReleaseRuleItem {
    private Integer id;
    private CouponReleaseRule rule;
    private String itemType;
    private String itemTypeValue;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CouponReleaseRule getRule() {
        return rule;
    }

    public void setRule(CouponReleaseRule rule) {
        this.rule = rule;
    }

    public String getItemTypeValue() {
        return itemTypeValue;
    }

    public void setItemTypeValue(String itemTypeValue) {
        this.itemTypeValue = itemTypeValue;
    }
}

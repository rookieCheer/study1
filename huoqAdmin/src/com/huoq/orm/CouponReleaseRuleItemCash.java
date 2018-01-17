package com.huoq.orm;

/**
 * www.bigbug.tech
 * Created by 陈冬明 on 19/09/2017.
 */
public class CouponReleaseRuleItemCash extends CouponReleaseRuleItem {
    private Double value;
    private Integer valueType;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }
}

package com.huoq.orm;

/**
 * www.bigbug.tech
 * Created by 陈冬明 on 19/09/2017.
 */
public class CouponReleaseRuleItemMoreMoney extends CouponReleaseRuleItem {
    private Double value; // 面值
    private Integer valueType; // 面值类型, 1:固定, 2:动态百分比计算
    private Double conditionValue; // 使用条件数值
    private Integer conditionType; // 使用条件类型, 1:固定, 2:动态百分比计算
    private Integer requiredPeriod; // 产品的必须满足周期
    private Integer expireDay; // 卷的过期天数

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

    public Double getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(Double conditionValue) {
        this.conditionValue = conditionValue;
    }

    public Integer getConditionType() {
        return conditionType;
    }

    public void setConditionType(Integer conditionType) {
        this.conditionType = conditionType;
    }

    public Integer getRequiredPeriod() {
        return requiredPeriod;
    }

    public void setRequiredPeriod(Integer requiredPeriod) {
        this.requiredPeriod = requiredPeriod;
    }

    public Integer getExpireDay() {
        return expireDay;
    }

    public void setExpireDay(Integer expireDay) {
        this.expireDay = expireDay;
    }
}

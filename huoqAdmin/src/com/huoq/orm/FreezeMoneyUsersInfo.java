package com.huoq.orm;

import com.huoq.common.util.QwyUtil;

/**
 * 冻结资金用户信息
 * Created by yks on 2017/1/13.
 */
public class FreezeMoneyUsersInfo {

    private String usersId; //用户id

    private String phone; //手机号

    private String realName;//真实姓名

    private String freezeMoney; //冻结资金

    private String productMoney; //投资金额

    private String diff;//投资金额 -冻结资金 =  差额

    private String isDiff; //是否存在偏差  0，存在偏差 ，1不存在偏差 ，为空的话，该用户还未投资

    public String getUsersId() {
        return this.usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealName() {
        return this.realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getFreezeMoney() {
        return this.freezeMoney;
    }

    public void setFreezeMoney(String freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public String getProductMoney() {
        return this.productMoney;
    }

    public void setProductMoney(String productMoney) {
        this.productMoney = productMoney;
    }

    public String getDiff() {
        return this.diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getIsDiff() {
        if ("0".equals(isDiff)) {
            return "异常";
        }
        if ("1".equals(isDiff)) {
            return "正常";
        }
        if (QwyUtil.isNullAndEmpty(isDiff)) {
            return "";
        }
        return "";
    }

    public void setIsDiff(String isDiff) {
        this.isDiff = isDiff;
    }
}

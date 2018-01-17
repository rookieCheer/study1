package com.huoq.util;

/**
 * 投资规则枚举
 * @author guoyin.yi
 * @version $Id: CouponReleaseRuleEnum.java, v 0.1  2017/9/21 Exp $
 */
public enum CouponReleaseRuleEnum {
    CASH("5","现金"),
    RED("3","红包"),
    MOREMONEY("1","理财券"),
    MORERATE("4","加息券");

    private String code;
    private String desc;

    CouponReleaseRuleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code){
        for (CouponReleaseRuleEnum cp : CouponReleaseRuleEnum.values()){
            if (cp.code.equals(code)){
                return cp.getDesc();
            }
        }
        return "";
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

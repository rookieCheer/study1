package com.huoq.newbranch.enums;
/**
 * Description:图标模块的枚举类
 * @author  changhaipeng
 * @params 
 * @date 2017年6月15日
 */
public enum ModuleEnum {
    /**
     * 兼容code和name
     */
    HOMEPAGE("shouye","首页","0","首页"),
    FINDPAGE("faxian","发现","1","发现"),
    OTHER("other","其它","2","其它");

    private final String code;
    private final String name;
    private final String value;
    private final String desc;

    ModuleEnum(String code, String name, String value, String desc){
        this.code = code;
        this.name = name;
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据编码获取信息
     * @param code
     * @return
     */
    public static ModuleEnum fromCode(String code){
        for (ModuleEnum v : ModuleEnum.values()){
            if (v.getCode().equals(code)){
                return v;
            }
        }
        return ModuleEnum.OTHER;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}

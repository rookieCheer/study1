/**  
 * Project Name:apiV1.0.2  
 * File Name:LogsEnum.java  
 * Package Name:com.huoq.newbranch.enums  
 * Date:2017年8月21日下午4:00:32  
 * Copyright (c) 2017,  All Rights Reserved.  
 *  
*/  
  
package com.huoq.util;
/**  
 * ClassName:LogsEnum  
 * Function: 记录日志的code和msg  
 * Date:     2017年8月21日 下午4:00:32   
 * @author   changhaipeng 
 * @version    
 * @since    JDK 1.8  
 * @see      业务code说明，10000开头的为支付相关  
 */
public enum LogsEnum {
	BF_CALBACK_RETEPT("10001","回调报文为空","10001","回调报文为空"),
	BF_PUB_KEY_EPT("10002","公钥文件不存在","10002","公钥文件不存在"),
	BF_PUB_KEY_ERR("10003","解密公匙不正确","10003","检查解密公钥不正确"),
	BF_CALBACK_CONTENT_ORG("10004","回调请求报文为：","10004","回调请求报文为："),
	BF_CALBACK_CONTENT("10005","回调返回数据解密结果为：","10005","回调返回数据解密结果:"),
	BF_CALBACK_RSPCODE_EPT("10006","回调报文中对象没有返回码resp_code","10006","回调报文中对象没有返回码resp_code"),
	BF_CALBACK_CZRECORD_EPT("10007","回调请求时，平台充值记录信息异常","10007","回调请求时，平台充值记录信息异常"),
	BF_CALBACK_CZRD_STUERR("10008","回调请求时，平台的充值订单状态异常","10008","回调请求时，平台的充值订单状态异常"),
	BF_CALBACK_USER_INFOERR("10009","用户信息异常","10009","用户信息异常"),
	BF_CALBACK_USER_ISBINDED("10010","用户已经绑定银行卡","10010","用户已经绑定银行卡"),
	BF_CALBACK_USER_ACC_ERR("10011","用户账户信息异常，未查到需要认证的账户记录","10011","用户账户信息异常，未查到需要认证的账户记录"),
	BF_CALBACK_CZ_SUCCESS("10012","充值成功","10012","充值成功"),
	BF_CALBACK_DBERR("10013","宝付已经充值成功，但平台入库失败","10013","宝付已经充值成功，但平台入库失败"),
	BF_CALBACK_CZ_FAIL("10014","充值失败","10014","充值失败"),
	BF_CALBACK_USER_UNBINDED("10015","用户未绑定银行卡","10015","用户未绑定银行卡"),
	BF_CALBACK_USER_ACC_INFOERR("10016","用户账户信息异常，有多个可用的账户","10016","用户账户信息异常，有多个可用的账户"),
	BF_CALBACK_RET_MISSIMSG("10017","回调信息缺少主要信息","10017","回调信息缺少主要信息"),
	BF_CALBACK_INV_EMP("10018","没有相关的投资记录","10018","没有相关的投资记录"),
	PRO_OVER_ERROR("10019","产品状态已经改变，或产品已经售罄","10019","产品状态已经改变，或产品已经售罄"),
	PRO_UPDATE_ERROR("10020","更新产品信息失败","10020","更新产品信息失败"),	
	PRO_BUY_ERROR("10021","宝付充值成功，但是购买失败，已返款至个人账户!","10021","宝付充值成功，但是购买失败，已返款至个人账户!"),
	
	OTHER("90000404","未知的业务异常","90000404","未知的业务异常");
	
	
	private LogsEnum(String code, String name, String value, String desc) {
		this.code = code;
		this.name = name;
		this.value = value;
		this.desc = desc;
	}
	private final String code;
	private final String name;
    private final String value;
    private final String desc;
    
    /**  
     * fromCode:通过code查找msg
     * @author changhaipeng
     * @param code
     * @return  
     * @since JDK 1.8  
     */
    public static LogsEnum fromCode(String code){
        for (LogsEnum val : LogsEnum.values()){
            if (val.getCode().equals(code)){
                return val;
            }
        }
        return OTHER;
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
  

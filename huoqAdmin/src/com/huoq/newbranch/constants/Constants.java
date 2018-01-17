package com.huoq.newbranch.constants;

import com.huoq.common.util.PropertiesUtil;

/**
 * Description:记录公共的常量
 * @author  changhaipeng
 * @params 
 * @date 2017年6月16日
 */
public class Constants {
	
	/**
	 * 项目名称，用于记录系统入参日志
	 */
	public static final String API_PROJECT_NAME = "api"; 
	public static final String ADMIN_PROJECT_NAME = "admin"; 
	
	/**
	 * 通知短信号码
	 */
	public static final String  TONGZHI_PHONE = "13588242687";
	
	private static final String PROJECT = "fy_"; 
	
	 /**
	 * redis存储图标 ,首頁的图标
	 */
	public static final String FENGYE_SYSICONS_ICONS = PROJECT+"sys_icons_loadicons_";

	/**
	 * redis版本信息
	 */
	public  static final String VERSIONKEY = PROJECT+"versions_";
	
	/**
	 * 配置管理
	 */
	
	public static final String PROOJECTNNAME =  PROJECT +"sysConfig_";
	
	/**
	 * H5接口
	 */
	public static final String H5_SYQXT = "/#/syqxt";//收益曲线
	public static final String H5_SHARE = "/#/share";//分享
	public static final String H5_GOODS = "/#/goods_agreement";//货贷宝
	public static final String H5_CAR = "/#/car_agreement";//车贷宝
	
	//自动续标url
	public static final String PRODUCT_OPERATE_URL = PropertiesUtil.getProperties("admin_url")+"/Product/callBack!autoReleaseProductOperate.action?flag=mobile";//车贷宝
	
	
	
	/***************************短信模板模块 常海鹏 2017年6月29日16:02:07************
	 * 解绑银行卡成功短信模板
	 */
	public static final String  SMS_UNBINDSUC ="%s解绑银行卡成功，感谢您使用国内领先的金融理财服务！%s";
	
	/**
	 * 注册成功的模板
	 */
	public static final String SMS_REGSUC= "尊敬的用户您好，您的注册验证码为%s(验证码30分钟内有效)%s";

	/**
	 * 找回登录密码的模板
	 */
	public static final String SMS_FINDLOGINPW= "尊敬的用户您好，您找回登录密码的验证码为%s(验证码30分钟内有效)%s";
	/**
	 * 找回交易密码的模板
	 */
	public static final String SMS_CHANGETRADEPW= "尊敬的用户您好，您找回交易密码的验证码为%s(验证码30分钟内有效)%s";
	/**
	 * 解绑银行卡短信验证码的模板
	 */
	public static final String SMS_UNBINDBANKCARD= "尊敬的用户您好，您正在进行解绑银行卡尾号为%s的操作，如非本人操作，请尽快修改账户密码。验证码:%s(验证码30分钟有效)%s";
	/**
	 * 更换绑定的手机号码验证码的模板
	 */
	public static final String SMS_CHANGEPHONE= "尊敬的用户您好，您正在更换绑定的手机号码,验证码:%s(验证码30分钟有效)%s";
	
	/**
	 * 通知短信：补充理财产品
	 */
	public static final String SMS_ADDPRD_TZ = "缺少（%s）理财产品，请尽快补全。%s";
	
	//******************************************************END******************************
	
	/**
	 * redis释放锁出现异常，发送短信
	 */
	public static final String SMS_REDISERROR_PHONE = "17730070607";
	
	/**
	 * 黑名单拦截后，反馈的公司电话
	 */
	public static final String COMP_PHONE ="400-806-5993";
	/**
	 * 版本展示
	 */
	public static final String VERSIONS_DOWNLOAD_PATH = "download/android/apk/";
	public static final String VERSIONS_UPLOAD_PATH = "/download/android/apk/";
	public static final String VERSIONS_DOWNLOAD_FOLDER = "/default/";

	/**
	 * 图标上传路径
	 */
	public static final String UPLOAD_ICONS_PATH = "/Images/icons/";

}

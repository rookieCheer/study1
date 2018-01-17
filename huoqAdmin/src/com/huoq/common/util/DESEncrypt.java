package com.huoq.common.util;


import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.huoq.common.ApplicationContexts;
import com.huoq.common.bean.PlatformBean;
import com.huoq.orm.Account;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**DES加密工具类
 * @author qwy
 *
 * @createTime 2015-04-17 01:03:49
 */
public class DESEncrypt {

	private static Logger log = Logger.getLogger(DESEncrypt.class); 
	
	/**
	 * 加密
	 * @param src 要加密的数据
	 * @param key 加密取用的key。八位字符串
	 * @return
	 * @throws Exception
	 */
	public static final String encrypt(String src, String key)throws Exception {
		if(src==null)
			return null;
		if("".equals(src))
			return "";
		SecureRandom sr = new SecureRandom(); 
		
		DESKeySpec dks = new DESKeySpec(key.getBytes()); 
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 

        SecretKey securekey = keyFactory.generateSecret(dks); 
        
        Cipher cipher = Cipher.getInstance("DES"); 

        // 用密匙初始化Cipher对象 

        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr); 
        byte bb[] = cipher.doFinal(src.getBytes());
        StringBuffer buff = new StringBuffer(bb.length);
		String sTemp;
		for(int i=0;i<bb.length;i++){
				sTemp = Integer.toHexString(0xFF &bb[i]);
				if(sTemp.length()<2){
					buff.append(0);
				}
				
				buff.append(sTemp.toUpperCase());
			
		}
        
        return buff.toString();
        
	}
	
	/**
	 * 解密
	 * @param src 要解密的数据源
	 * @param key 加密时取用的key，八位字符串
	 * @return
	 * @throws Exception
	 */
	public static final String decrypt(String src, String key)throws Exception { 
		if(src==null)
			return null;
		if("".equals(src))
			return "";
		try {
			int len = (src.length()/2);
			byte [] result = new byte[len];
			char[] achar = src.toString().toCharArray();
			for(int j=0;j<len;j++){
				int pos = j*2;
				result[j]= ((byte)(toByte(achar[pos])<<4|toByte(achar[pos+1])));
			}
			
			
			// DES算法要求有一个可信任的随机数源 

			SecureRandom sr = new SecureRandom(); 

			// 从原始密匙数据创建一个DESKeySpec对象 

			DESKeySpec dks = new DESKeySpec(key.getBytes()); 

			// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成 

			// 一个SecretKey对象 

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 

			SecretKey securekey = keyFactory.generateSecret(dks); 

			// Cipher对象实际完成解密操作 

			Cipher cipher = Cipher.getInstance("DES"); 

			// 用密匙初始化Cipher对象 

			cipher.init(Cipher.DECRYPT_MODE, securekey, sr); 

			// 现在，获取数据并解密 

			// 正式执行解密操作 

			return new String(cipher.doFinal(result));
		} catch (Exception e) {
			log.error("操作异常: ",e);
		} 
		return src;

     } 
	
	private static byte toByte(char c){
		
		byte  b = (byte)"0123456789ABCDEF".indexOf(c);
		return b;
	}
	
	/**加密字符串;<br>
	 * 登录密码;支付密码;
	 * @param string 需要加密的字符串;
	 * @return 加密过后的字符串;
	 * @throws Exception
	 */
	public static String jiaMiPassword(String string){
		try {
			String str = encrypt(string,"bym*_*@@");
			String str2 = encrypt(str,"baiyimao");
			return str2;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**解密字符串;<br>
	 * 登录密码;支付密码;
	 * @param string 加密过后的字符串;
	 * @return 解密过后的字符串;
	 * @throws Exception
	 */
	public static String jieMiPassword(String string){
		String de2=null;
		try {
			String de = decrypt(string, "baiyimao");
			de2 = decrypt(de, "bym*_*@@");
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return de2;
	}
	
	/**加密字符串;<br>
	 * 帐号,手机号码,邮箱;
	 * @param string 需要加密的字符串;
	 * @return 加密过后的字符串;
	 * @throws Exception
	 */
	public static String jiaMiUsername(String string){
		try {
			String str = encrypt(string,"userName");
			String str2 = encrypt(str,"bym-_-@@");
			return str2;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	/**解密字符串;<br>
	 * 帐号,手机号码,邮箱;
	 * @param string 加密过后的字符串;
	 * @return 解密过后的字符串;
	 * @throws Exception
	 */
	public static String jieMiUsername(String string){
		String de2=null;
		try {
			String de = decrypt(string, "bym-_-@@");
			de2 = decrypt(de, "userName");
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return de2;
	}
	
	
	/**加密字符串;<br>
	 * 身份证号
	 * @param string 需要加密的字符串;
	 * @return 加密过后的字符串;
	 * @throws Exception
	 */
	public static String jiaMiIdCard(String string){
		try {
			String str = encrypt(string,"@*IdCard*");
			String str2 = encrypt(str,"bymCards");
			return str2;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	/**解密字符串;<br>
	 * 身份证号
	 * @param string 加密过后的字符串;
	 * @return 解密过后的字符串;
	 * @throws Exception
	 */
	public static String jieMiIdCard(String string){
		String de2=null;
		try {
			String de = decrypt(string, "bymCards");
			de2 = decrypt(de, "@*IdCard*");
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return de2;
	}
	
	
	
	/**加密字符串;<br>
	 * 银行卡
	 * @param string 需要加密的字符串;
	 * @return 加密过后的字符串;
	 * @throws Exception
	 */
	public static String jiaMiBankCard(String string){
		try {
			String str = encrypt(string,"bymBanks");
			String str2 = encrypt(str,"$_$_bank");
			return str2;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	/**解密字符串;<br>
	 * 银行卡
	 * @param string 加密过后的字符串;
	 * @return 解密过后的字符串;
	 * @throws Exception
	 */
	public static String jieMiBankCard(String string){
		String de2=null;
		try {
			String de = decrypt(string, "$_$_bank");
			de2 = decrypt(de, "bymBanks");
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return de2;
	}
	
	/**加密字符串;<br>
	 * 数据库jdbc加密;
	 * @param string 需要加密的字符串;
	 * @return 加密过后的字符串;
	 * @throws Exception
	 */
	public static String jiaMiProperties(String string){
		try {
			String str = encrypt(string,"properties");
			String str2 = encrypt(str,"baiyimao");
			return str2;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**解密字符串;<br>
	 * 数据库jdbc;
	 * @param string 加密过后的字符串;
	 * @return 解密过后的字符串;
	 * @throws Exception
	 */
	public static String jieMiProperties(String string){
		String de2=null;
		try {
			String de = decrypt(string, "baiyimao");
			de2 = decrypt(de, "properties");
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return de2;
	}
	
	/**加密字符串;<br>
	 * 登录密码;支付密码;
	 * @param string 需要加密的字符串;
	 * @return 加密过后的字符串;
	 * @throws Exception
	 */
	public static String jiaMiToken(String string){
		try {
			String str = encrypt(string,"tkn*_*@@");
			return str;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	/**解密字符串;<br>
	 * 登录密码;支付密码;
	 * @param string 加密过后的字符串;
	 * @return 解密过后的字符串;
	 * @throws Exception
	 */
	public static String jieMiToken(String string){
		String de2=null;
		try {
			de2 = decrypt(string, "tkn*_*@@");
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return de2;
	}
	
}

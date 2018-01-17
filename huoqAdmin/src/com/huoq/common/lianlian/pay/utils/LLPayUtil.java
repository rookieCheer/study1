package com.huoq.common.lianlian.pay.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.huoq.common.lianlian.pay.utils.enums.SignTypeEnum;

import net.sf.json.JSONObject;
/**
* 常用工具函数
* @author guoyx e-mail:guoyx@lianlian.com
* @date:2013-6-25 下午05:23:05
* @version :1.0
*
*/
public class LLPayUtil{
	private static Logger log = Logger.getLogger(LLPayUtil.class); 
    /**
     * str空判断
     * @param str
     * @return
     * @author guoyx
     */
    public static boolean isnull(String str)
    {
        if (null == str || str.equalsIgnoreCase("null") || str.equals(""))
        {
            return true;
        } else
            return false;
    }

    /**
     * 获取当前时间str，格式yyyyMMddHHmmss
     * @return
     * @author guoyx
     */
    public static String getCurrentDateTimeStr()
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String timeString = dataFormat.format(date);
        return timeString;
    }

    /**
     * 
     * 功能描述：获取真实的IP地址
     * @param request
     * @return
     * @author guoyx
     */
    public static String getIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        if (!isnull(ip) && ip.contains(","))
        {
            String[] ips = ip.split(",");
            ip = ips[ips.length - 1];
        }
        //转换IP 格式
        if(!isnull(ip)){
            ip=ip.replace(".", "_");
        }
        return ip;
    }

    /**
     * 生成待签名串
     * @param paramMap
     * @return
     * @author guoyx
     */
    public static String genSignData(JSONObject jsonObject)
    {
        StringBuffer content = new StringBuffer();

        // 按照key做首字母升序排列
        List<String> keys = new ArrayList<String>(jsonObject.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < keys.size(); i++)
        {
            String key = (String) keys.get(i);
            if ("sign".equals(key))
            {
                continue;
            }
            String value = jsonObject.getString(key);
            // 空串不参与签名
            if (isnull(value))
            {
                continue;
            }
            content.append((i == 0 ? "" : "&") + key + "=" + value);

        }
        String signSrc = content.toString();
        if (signSrc.startsWith("&"))
        {
            signSrc = signSrc.replaceFirst("&", "");
        }
        return signSrc;
    }




    /**
     * MD5签名验证
     * 
     * @param signSrc
     * @param sign
     * @return
     * @author guoyx
     */
    @SuppressWarnings("unused")
	public static boolean checkSignMD5(JSONObject reqObj, String md5_key)
    {
        if (reqObj == null){
            return false;
        }
        log.info("进入商户[" + reqObj.getString("oid_partner")
        + "]MD5签名验证");
        String sign = reqObj.getString("sign");
        // 生成待签名串
        String sign_src = genSignData(reqObj);
        log.info("商户[" + reqObj.getString("oid_partner") + "]待签名原串"
                + sign_src);
        log.info("商户[" + reqObj.getString("oid_partner") + "]签名串"
                + sign);
        sign_src += "&key=" + md5_key;
        try
        {
        	log.info(sign_src);
        	log.info("最终返回结果："+Md5Algorithm.getInstance().md5Digest(
                    sign_src.getBytes("utf-8")));
            if (sign.equals(Md5Algorithm.getInstance().md5Digest(
                    sign_src.getBytes("utf-8"))))
            {
                log.info("商户[" + reqObj.getString("oid_partner")
                        + "]MD5签名验证通过");
                return true;
            } else
            {
                log.info("商户[" + reqObj.getString("oid_partner")
                        + "]MD5签名验证未通过");
                return false;
            }
        } catch (UnsupportedEncodingException e)
        {
            log.info("商户[" + reqObj.getString("oid_partner")
                    + "]MD5签名验证异常" + e.getMessage());
            return false;
        }
    }


    /**
     * MD5加签名
     * 
     * @param reqObj
     * @param md5_key
     * @return
     * @author guoyx
     */
    private static String addSignMD5(JSONObject reqObj, String md5_key)
    {
        if (reqObj == null)
        {
            return "";
        }
        log.info("进入商户[" + reqObj.getString("oid_partner")
        + "]MD5加签名");
        // 生成待签名串
        String sign_src = genSignData(reqObj);
        log.info("商户[" + reqObj.getString("oid_partner") + "]加签原串"
                + sign_src);
        sign_src += "&key=" + md5_key;
        try
        {
            return Md5Algorithm.getInstance().md5Digest(
                    sign_src.getBytes("utf-8"));
        } catch (Exception e)
        {
            log.info("商户[" + reqObj.getString("oid_partner")
                    + "]MD5加签名异常" + e.getMessage());
            return "";
        }
    }

    /**
     * 读取request流
     * @param req
     * @return
     * @author guoyx
     */
    public static String readReqStr(HttpServletRequest request)
    {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(request
                    .getInputStream(), "utf-8"));
            String line = null;

            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        } catch (IOException e)
        {
            log.error("操作异常: ",e);
        } finally
        {
            try
            {
                if (null != reader)
                {
                    reader.close();
                }
            } catch (IOException e)
            {

            }
        }
        return sb.toString();
    }
}

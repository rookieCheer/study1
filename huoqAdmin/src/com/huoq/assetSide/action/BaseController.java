package com.huoq.assetSide.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoq.common.util.Des;
import com.huoq.common.util.IoUtils;
import com.huoq.datejson.handle.DateJsonObjectProcessor;
import com.huoq.payUtils.httpUtil.Md5Algorithm;
import com.huoq.util.MyLogger;
import com.huoq.util.RestErrorCode;

import net.sf.json.JsonConfig;


public abstract class BaseController {

    private static MyLogger logger = MyLogger.getLogger(BaseController.class);

    private String          basePath;

    @ModelAttribute()
    public void init(ModelMap model) {
        model.addAttribute("basePath", getBasePath());
        model.addAttribute("staticPath", getBasePath() + "/assets");
    }

    public String getBasePath() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        this.basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * 向客户端输出指定格式的文档
     * 
     * @param response 响应
     * @param text 要发的内容
     * @param contentType 发送的格式
     */
    public void doPrint(HttpServletResponse response, String text, String contentType) {
        try {
            response.setContentType(contentType);
            response.getWriter().write(text);
        } catch (IOException e) {
            logger.error("向客户端输出时发生异常 : " + e.getMessage());
        }
    }

    /**
     * 直接输出二进制
     */
    public void responseBin(HttpServletResponse response, byte[] buf, int off, int len) {
        try {
            response.setContentType("application/octet-stream");
            response.getOutputStream().write(buf, off, len);
        } catch (IOException e) {
            logger.error("向客户端输出时发生异常 : " + e.getMessage());
        }
    }

    /**
     * 直接输出json
     */
    public void responseJson(HttpServletResponse response, String json) {
        doPrint(response, json, "application/json;charset=UTF-8");
    }

    /**
     * @param request
     * @return
     */
    public JSONObject getParamFromRequest(HttpServletRequest request) throws Exception {
        System.out.println("--------------- request url----------------" + request.getRequestURI());
        InputStream is = request.getInputStream();
        String paramString = IoUtils.convertStream2String(is);
        if (null == paramString) {
            throw new Exception(RestErrorCode.MISSING_ARGS.getMessage());
        }
        paramString = Des.decode(Des.secretKey, paramString);
        JSONObject jSONObject = JSON.parseObject(paramString);
        if (null == jSONObject) {
            throw new Exception(RestErrorCode.MISSING_ARGS.getMessage());
        }
        // IP地址白名单校验
        System.out.println("=============== request.getRemoteAddr() ==================="+request.getRemoteAddr());
       /* List<WhiteList> whiteLis = this.whiteListBean.getWhiteListBySourceAndType(jSONObject.getString("source"), DEF_ENUM.DEF_ONE.getnCode());
        if (null != whiteLis && !whiteLis.isEmpty()) {
            for(WhiteList whiteList : whiteLis) {
                if(!request.getRemoteAddr().endsWith(whiteList.getValues())) {
                    throw new Exception(RestErrorCode.NO_AUTHORITY.getMessage());
                }
            }
        }*/
        if(!jSONObject.getString("sign").equals(Md5Algorithm.encoderByMd5(Md5Algorithm.secretKey + jSONObject.getString("params")))) {
            throw new Exception(RestErrorCode.SIGN_CHECK_FAIL.getMessage());
        }
        return jSONObject;
    }

    public BaseResponse procJsonMap(HttpServletRequest request, Map<String, Object> map, BaseResponse bre) throws Exception {
        if (!map.isEmpty()) {
            net.sf.json.JsonConfig config = new JsonConfig();
            config.registerJsonValueProcessor(java.util.Date.class, new DateJsonObjectProcessor());
            net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(map, config);
            bre.setData(Des.encode(Des.secretKey, json.toString()));
            bre.setData(json.toString());
        }
        return bre;
    }

    public BaseResponse resResult(Integer errorCode, String errorMsg) {
        BaseResponse ent = new BaseResponse();
        ent.setCode(errorCode);
        ent.setMsg(errorMsg);
        return ent;
    }
    
    public BaseResponse resResult(String errorMsg) {
        if (RestErrorCode.MISSING_ARGS.getMessage().equals(errorMsg)) {
            return this.resResult(RestErrorCode.MISSING_ARGS.getCode(), RestErrorCode.MISSING_ARGS.getMessage());
        } else if (RestErrorCode.SIGN_CHECK_FAIL.getMessage().equals(errorMsg)) {
            return this.resResult(RestErrorCode.SIGN_CHECK_FAIL.getCode(), RestErrorCode.SIGN_CHECK_FAIL.getMessage());
        } else if (RestErrorCode.NO_AUTHORITY.getMessage().equals(errorMsg)) {
            return this.resResult(RestErrorCode.NO_AUTHORITY.getCode(), RestErrorCode.NO_AUTHORITY.getMessage());
        } else {
            return this.resResult(RestErrorCode.SYS_ERROR.getCode(), RestErrorCode.SYS_ERROR.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString("A"));
    }
}

/**
 * www.xinhuajindian.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.baofoo.bean;

import com.baofoo.rsa.RsaCodingUtil;
import com.baofoo.util.JXMConvertUtil;
import com.baofoo.util.SecurityUtil;
import com.huoq.baofoo.utils.BaofooPayParams;
import com.huoq.baofoo.utils.BaofooQueryParams;
import com.huoq.baofoo.utils.PayResult;
import com.huoq.common.util.BaofooClient;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.util.RequestParams;
import com.huoq.common.util.SimpleHttpResponse;
import com.huoq.util.MyLogger;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * 宝付提现接口
 *
 * @author guoyin.yi
 * @version $Id: BaofooBean.java, v 0.1  2017/12/28 Exp $
 * @email guoyin.yi@xinhuajindian.com
 */
@Service
public class BaofooBean {

    private MyLogger logger = MyLogger.getLogger(BaofooBean.class);

    // 配置信息
    private static ResourceBundle resb = ResourceBundle.getBundle("app");

    /**
     * 宝付代付接口
     * @param payParams
     * @return
     */
    public PayResult remittance(List<BaofooPayParams> payParams){

        if (payParams.size()>5){
            return new PayResult("1001","交易请求记录条数超过上限!",null);
        }

        List<Object> list = new ArrayList<Object>();
        for (BaofooPayParams params: payParams) {
            Map<String, Object> trans_reqData = new HashMap<>();
            trans_reqData.put("trans_no", params.getTransNo());		// 商户订单
            trans_reqData.put("trans_money", params.getTransMoney());		// 转账金额
            trans_reqData.put("to_acc_name", params.getToAccName());	// 收款人姓名
            trans_reqData.put("to_acc_no", params.getToAccNo());			// 收款人银行帐号
            trans_reqData.put("to_bank_name", params.getToBankName());	// 收款人银行名称
            trans_reqData.put("to_pro_name", QwyUtil.isNullAndEmpty(params.getToProName()) ? "" : params.getToProName());			// 收款人开户行省名
            trans_reqData.put("to_city_name",QwyUtil.isNullAndEmpty(params.getToCityName()) ? "" : params.getToCityName());			// 收款人开户行市名
            trans_reqData.put("to_acc_dept", QwyUtil.isNullAndEmpty(params.getToAccDept()) ? "" : params.getToAccDept());			// 收款人开户行机构
            trans_reqData.put("trans_card_id", params.getTransCardId());	// 银行卡身份证件号
            trans_reqData.put("trans_mobile", params.getTransMobile());		// 银行卡预留手机号
            trans_reqData.put("trans_summary", QwyUtil.isNullAndEmpty(params.getTransSummary()) ? "" : params.getTransSummary());		// 摘要
            list.add(trans_reqData);
        }

        Map<String, Object> trans_reqDatas = new HashMap<>();
        trans_reqDatas.put("trans_reqData", list);

        List<Object> lists = new ArrayList<>();
        lists.add(trans_reqDatas);

        Map<String, Object> trans_content = new HashMap<>();
        trans_content.put("trans_reqDatas", lists);

        Map<Object, Object> requestData = new HashMap<>();
        requestData.put("trans_content", trans_content);

        // 将集合转成json
        JSONObject jsonObjectFromMap = JSONObject.fromObject(requestData);

        String requestUrl = resb.getString("withdrawals_url");

        return requestBaofoo(jsonObjectFromMap,requestUrl);
    }

    /**
     * 查询订单状态
     * @param params
     * @return
     */
    public PayResult queryPayOrder(List<BaofooQueryParams> params){
        if (params.size() > 5){
            return new PayResult("1001","交易请求记录条数超过上限!",null);
        }
        List<Object> list = new ArrayList<Object>();
        for (BaofooQueryParams par: params) {
            Map<String, Object> trans_reqData = new HashMap<String, Object>();
            trans_reqData.put("trans_batchid", par.getTransBatchid());		// 批次号
            trans_reqData.put("trans_no", par.getTransNo());		// 商户订单号
            list.add(trans_reqData);
        }

        Map<String, Object> trans_reqDatas = new HashMap<>();
        trans_reqDatas.put("trans_reqData", list);

        List<Object> lists = new ArrayList<>();
        lists.add(trans_reqDatas);

        Map<String, Object> trans_content = new HashMap<>();
        trans_content.put("trans_reqDatas", lists);

        Map<Object, Object> requestData = new HashMap<>();
        requestData.put("trans_content", trans_content);

        // 将集合转成json
        JSONObject jsonObjectFromMap = JSONObject.fromObject(requestData);
        String requestUrl = resb.getString("baofoo_queryOrder_url");

        return requestBaofoo(jsonObjectFromMap,requestUrl);

    }


    /**
     * 请求宝付接口
     * @param requestData
     * @return
     */
    public PayResult requestBaofoo(Map<Object, Object> requestData,String requestUrl){

        String path = this.getClass().getClassLoader().getResource("/").getPath();
        // 商户私钥
        String pfxpath = path.substring(0,path.indexOf("WEB-INF")) +"CER2/" + resb.getString("daifusy2.name");
        // 获取公钥
        String cerpath = path.substring(0,path.indexOf("WEB-INF")) + "CER2/" + resb.getString("daifugy2.name");
        // 请求宝付地址
//        String requestUrl = resb.getString("withdrawals_url");
        // 商户
        String memberId = resb.getString("daifuM2.Id");
        // 终端
        String terminalId = resb.getString("daifuT2.id");
        // 私钥密码
        String pfxpwd = resb.getString("pfxdf2.pwd");
        // 加密参数类型
        String dataType = resb.getString("datadf.type");

        // 将集合转成json
        JSONObject jsonObjectFromMap = JSONObject.fromObject(requestData);
        String Json = jsonObjectFromMap.toString();
        logger.info("====宝付请求明文:" + Json);
        try {
            // 加密
            String base64str = SecurityUtil.Base64Encode(Json);
            String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str, pfxpath, pfxpwd);
            logger.info("=====data_content:"+data_content);

            // 接口的公共参
            RequestParams params = new RequestParams();
            params.setMemberId(Integer.parseInt(memberId));
            params.setTerminalId(Integer.parseInt(terminalId));
            params.setDataType(dataType);
            params.setDataContent(data_content);// 加密后数
            params.setVersion("4.0.0");
            params.setRequestUrl(requestUrl);
            // 调用代付接口
            SimpleHttpResponse res = BaofooClient.doRequest(params);
            logger.info("====宝付公共参数表单:" + params.toString());

            String reslut = res.getEntityString();
            logger.info("======Result密文:"+reslut);
            // 解密
            reslut = RsaCodingUtil.decryptByPubCerFile(reslut, cerpath);
            reslut = SecurityUtil.Base64Decode(reslut);
            logger.info("====宝付响应的报文:" + reslut);
            if (reslut.isEmpty()) {
                logger.info("====宝付同步返回报文为空");
            }
            Map<String, Object> result = JXMConvertUtil.JsonConvertHashMap(reslut);// 将JSON转化为Map对象
            JSONObject data = JSONObject.fromObject(result);

            if (!result.containsKey("return_code")) {
                logger.info("解析参数[return_code]不存在");
                return new PayResult("10003","解析参数[return_code]不存在",null);
            }
            logger.info("====宝付接口支付成功："+data.toString());
            return new PayResult(result.get("return_code").toString(),result.get("return_msg").toString(),data);
        }catch (Exception e){
            logger.info("====宝付接口异常："+e.getMessage());
            return new PayResult("9999","系统异常",null);
        }
    }

    public PayResult getContent(String dataContent,String dataType){
        try {
            String path = this.getClass().getClassLoader().getResource("/").getPath();

            if (dataContent.isEmpty()) {// 判断参数是否为空
                logger.info("=====异步通知数据为空");
                return new PayResult("30001", "异步通知数据为空", null);
            }

//            String dataType = resb.getString("data.type"); // 加密报文的数据类型（xml/json）

            logger.info("=====返回数据:" + dataContent);
            String cerpath = path.substring(0,path.indexOf("WEB-INF")) +"CER2/" + resb.getString("cer2.name");

            File cerfile = new File(cerpath);
            if (!cerfile.exists()) {// 判断宝付公钥是否为空
                System.out.println("宝付公钥文件不存在！");
                logger.info("=====公钥文件不存在！======");
                return new PayResult("30002", "公钥文件不存在！", null);
            }

            dataContent = RsaCodingUtil.decryptByPubCerFile(dataContent, cerpath);
            if (dataContent.isEmpty()) {// 判断解密是否正确。如果为空则宝付公钥不正确
                return new PayResult("30001", "检查解密公钥是否正确！", null);
            }
            dataContent = SecurityUtil.Base64Decode(dataContent);
            logger.info("=====返回数据解密结果:" + dataContent);

            if (dataType.equals("xml")) {
                dataContent = JXMConvertUtil.XmlConvertJson(dataContent);
                logger.info("=====返回结果转JSON:" + dataContent);
            }

            Map<String, Object> data = JXMConvertUtil.JsonConvertHashMap(dataContent);// 将JSON转化为Map对象。

            if (!data.containsKey("resp_code")) {
                return new PayResult("30003", "返回参数异常！XML解析参数[resp_code]不存在", null);
            }
            JSONObject jsonMap = JSONObject.fromObject(data);
            return new PayResult(data.get("resp_code").toString(), data.get("resp_msg").toString(), jsonMap);
        } catch (Exception e) {
            logger.error("异步通知接口异常：" + e.getMessage());
            return new PayResult("99999","系统异常", null);
        }
    }
}

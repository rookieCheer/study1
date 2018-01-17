package com.huoq.common.bean;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.baofoo.GetMyProperties;
import com.baofoo.rsa.RsaCodingUtil;
import com.baofoo.util.HttpUtil;
import com.baofoo.util.JXMConvertUtil;
import com.baofoo.util.MapToXMLString;
import com.baofoo.util.SecurityUtil;

import net.sf.json.JSONObject;

public class BaoFuQueryBean {

	public Map<String, Object> CzQuery_test(String appPropertiesUrl, String CER_URL, String orderId,
			String insert_time, String queryId) throws Exception {
		System.out.println("========================调用宝付订单查询接口接口============================");
		Properties p = GetMyProperties.getProperties(appPropertiesUrl);
		System.out.println(p.toString());
		String pfxpath = CER_URL + "/" + p.getProperty("pfx.name");
		System.out.println("pfxpath=============>" + pfxpath);

		File pfxfile = new File(pfxpath);
		if (!pfxfile.exists()) {
			return getCommonMap("私钥文件不存在！");
		}

		String cerpath = CER_URL + "/" + p.getProperty("cer.name");

		System.out.println("cerpath=============>" + cerpath);

		File cerfile = new File(cerpath);
		if (!cerfile.exists()) {
			return getCommonMap("=====公钥文件不存在！======");
		}

		String version = "4.0.0.1";
		String terminal_id = p.getProperty("terminal.id");
		String member_id = p.getProperty("member.id");
		String pfxpwd = p.getProperty("pfx.pwd");

		System.out.println("terminal_id=============>" + terminal_id);
		System.out.println("member_id=============>" + member_id);
		System.out.println("pfxpwd=============>" + pfxpwd);

		String input_charset = "1";
		String language = "1";
		String data_type = p.getProperty("data.type");

		String request_url = "http://tgw.baofoo.com/apipay/queryQuickOrder";
		if ("1".equals(p.getProperty("runtime"))) {
			request_url = p.getProperty("real_baofu_queryOrder");
		} else {
			request_url = p.getProperty("test_baofu_queryOrder");
		}

		System.out.println("request_url=============>" + request_url);

		Map ArrayData = new HashMap();
		ArrayData.put("txn_sub_type", "31");
		ArrayData.put("biz_type", "0000");
		ArrayData.put("terminal_id", terminal_id);
		ArrayData.put("member_id", member_id);
		ArrayData.put("trans_serial_no", queryId);
		ArrayData.put("orig_trans_id", orderId);
		ArrayData.put("orig_trade_date", insert_time);
		ArrayData.put("additional_info", "queryOrder");
		ArrayData.put("req_reserved", "queryOrder");

		Map ArrayData1 = new HashMap();

		String XmlOrJson = "";
		if (data_type.equals("xml")) {
			ArrayData1.putAll(ArrayData);
			XmlOrJson = MapToXMLString.converter(ArrayData1, "data_content");
		} else {
			JSONObject jsonObjectFromMap = JSONObject.fromObject(ArrayData);
			XmlOrJson = jsonObjectFromMap.toString();
		}
		System.out.println("XmlOrJson=============>" + XmlOrJson);

		String base64str = SecurityUtil.Base64Encode(XmlOrJson);

		System.out.println("base64str##########################" + base64str);

		String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str, pfxpath, pfxpwd);
		System.out.println("data_content##########################" + data_content);

		Map Post = new HashMap();

		Post.put("version", version);
		Post.put("terminal_id", terminal_id);
		Post.put("txn_type", "0431");
		Post.put("txn_sub_type", "31");
		Post.put("member_id", member_id);
		Post.put("data_type", data_type);
		Post.put("data_content", data_content);

		System.out.println("Post##########################" + Post.toString());

		String PostString = HttpUtil.RequestForm(request_url, Post);

		System.out.println("PostString 1# #########################" + PostString);

		PostString = RsaCodingUtil.decryptByPubCerFile(PostString, cerpath);

		System.out.println("PostString2  ===  ##########################" + PostString);

		if (PostString.isEmpty()) {
			return getCommonMap("返回空报文！");
		}
		PostString = SecurityUtil.Base64Decode(PostString);
		System.out.println("PostString3 ===  ##########################" + PostString);
		if (data_type.equals("xml")) {
			PostString = JXMConvertUtil.XmlConvertJson(PostString);
		}
		System.out.println("PostString4  ===  ##########################" + PostString);

		Map ArrayDataString = JXMConvertUtil.JsonConvertHashMap(PostString);
		System.out.println("ArrayDataString  ===  ##########################" + ArrayDataString);
		if (!ArrayDataString.containsKey("resp_code")) {
			return getCommonMap("解析参数[resp_code]不存在");
		}

		return ArrayDataString;
	}

	private Map<String, Object> getCommonMap(String message) {
		Map resultMap = new HashMap();
		resultMap.put("resp_code", "9999");
		resultMap.put("resp_msg", message);
		return resultMap;
	}
}

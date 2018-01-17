package com.huoq.admin.webView.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.account.bean.BuyCarBean;
import com.huoq.account.bean.FundRecordBean;
import com.huoq.account.bean.UserInfoBean;
import com.huoq.admin.product.bean.VirtualInsRecordBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.PlatformBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.PropertiesUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.FundRecord;
import com.huoq.orm.Investors;
import com.huoq.orm.Product;
import com.huoq.orm.SystemConfig;
import com.huoq.product.bean.IndexBean;
import com.huoq.product.bean.IndexNoticeBean;

/**后台管理--虚拟投资;
 * @author 覃文勇
 *
 * @createTime 2015年7月31日 16:47:44
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product")
	@Results({ @Result(name = "virtualIns", value = "/Product/Admin/productManager/xntz.jsp"),
		@Result(name = "virtualInsRecord", value = "/Product/Admin/productManager/xntzRecord.jsp"),
		@Result(name = "findvirtualIns", value = "/Product/Admin/productManager/xntzList.jsp"),
		@Result(name = "tzfx", value = "/wap/product/index_tzfx.jsp"),
		@Result(name = "hkbz", value = "/wap/product/index_hkbz.jsp"),
		@Result(name = "hkbz2", value = "/wap/product/fund_hkbz.jsp"),
		@Result(name = "bdxx", value = "/wap/product/fund_bdxx.jsp"),
		@Result(name = "description", value = "/wap/product/productDescription.jsp"),
		@Result(name = "info", value = "/wap/product/productInfo.jsp"),
		@Result(name = "moneyDetails", value = "/wap/product/fundRecord.jsp"),
		
			  
})
public class WebViewAction extends BaseAction {

	private static Logger log = Logger.getLogger(WebViewAction.class);
	@Resource
	private PlatformBean platformBean;
	
	@Resource
	private IndexBean bean;
	@Resource
	private UserInfoBean uibean;
	@Resource
	private RegisterUserBean rubean;
	@Resource
	private IndexNoticeBean indexNoticeBean;
	@Resource
	private VirtualInsRecordBean virtualInsRecordBean;
	@Resource
	private RegisterUserBean registerUserBean;
	private Integer currentPage = 1;
	private Integer pageSize = 7;
	private String productId;
	private String email;
	private Long zlq;
	private String status;//1 :项目详情     2 ： 投资记录     3 ：信息披露    4： 法律意见书 
	@Resource
	private BuyCarBean buyCarBean;
	private String url;
	private String type="all";
	private String startDate="";
	private String endDate="";
	private String usersId="";
	@Resource
	private FundRecordBean fundRecordBean;
	
	
	public void getProductImg(Product product){
		//信息披露;
		String infoImg = product.getInfoImg();
		if(!QwyUtil.isNullAndEmpty(infoImg)){
			List infoList = new ArrayList();
			SystemConfig config =(SystemConfig) getRequest().getServletContext().getAttribute("systemConfig");
			String[] infoImgs = infoImg.split(";");
			for (String str : infoImgs) {
				String url = config.getHttpUrl()+config.getFileName()+"/web_img/info/"+str;
				infoList.add(url);
			}
			getRequest().setAttribute("infoList",infoList);
		}
		//法律意见书
		String lawImg = product.getLawImg();
		if(!QwyUtil.isNullAndEmpty(lawImg)){
			List lawList = new ArrayList();
			SystemConfig config =(SystemConfig) getRequest().getServletContext().getAttribute("systemConfig");
			String[] lawImgs = lawImg.split(";");
			for (String str : lawImgs) {
				String url = config.getHttpUrl()+config.getFileName()+"/web_img/law/"+str;
				lawList.add(url);
			}
			getRequest().setAttribute("lawList",lawList);
		}
	}
	/**
	 * 跳转到还款保障页面
	 */
	public String goHkbz(){
		try {
			if (QwyUtil.isNullAndEmpty(productId)) {
				//没有产品id;
				return "hkbz";
			}
			Product product = bean.getProductById(productId);
			if (QwyUtil.isNullAndEmpty(product)) {
				//产品id无效
				return "hkbz";
			}
			
			getRequest().setAttribute("hkly", product.getHkly());
			getRequest().setAttribute("zjbz", product.getZjbz());
			getRequest().setAttribute("product", product);
			if("2".equals(product.getProductType())){
				return "hkbz2";
			}
			if (product.getInsertTime().before(
					QwyUtil.fmyyyyMMddHHmmss.parse(PropertiesUtil
							.getProperties("time") + ""))) {
				return "info";
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "hkbz";
		
	}
	
	/**
	 * 跳转到投资方向页面
	 */
	public String goTzfx(){
		try {
			if (QwyUtil.isNullAndEmpty(productId)) {
				//没有产品id;
				return "tzfx";
			}
			Product product = bean.getProductById(productId);
			if (QwyUtil.isNullAndEmpty(product)) {
				//产品id无效
				return "tzfx";
			}
			getProductImg(product);
			getRequest().setAttribute("description", product.getDescription());
			getRequest().setAttribute("cplxjs", product.getCplxjs());
			getRequest().setAttribute("product", product);
			if (product.getInsertTime().before(QwyUtil.fmyyyyMMddHHmmss.parse(PropertiesUtil.getProperties("time") + ""))){
				return "description";
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "tzfx";
		
	}
	
	/**
	 * 跳转到标的页面
	 */
	public String goBdxx(){
		try {
			if (QwyUtil.isNullAndEmpty(productId)) {
				//没有产品id;
				return "bdxx";
			}
			Product product = bean.getProductById(productId);
			if (QwyUtil.isNullAndEmpty(product)) {
				//产品id无效
				return "bdxx";
			}
			getProductImg(product);
			getRequest().setAttribute("zjbz", product.getZjbz());
			getRequest().setAttribute("cplxjs", product.getCplxjs());
			getRequest().setAttribute("product", product);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "bdxx";
		
	}
	
	
	/**获取资金流水记录;
	 * @return
	 */
	public String loadFundRecord(){
		try {
			PageUtil<FundRecord> pageUtil = new PageUtil<FundRecord>();
			pageUtil.setPageSize(10);
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageUrl(getRequest().getServletContext().getContextPath()+"/Product/User/fundRecord!loadFundRecord.action?type="+type+"&startDate="+startDate+"&endDate="+endDate);
			pageUtil = fundRecordBean.loadFundRecord(Long.parseLong(usersId),null,type,startDate,endDate,pageUtil);
			getRequest().setAttribute("usersId", usersId);
			if(!QwyUtil.isNullAndEmpty(pageUtil)){
				getRequest().setAttribute("pageUtil", pageUtil);
				List<FundRecord> list = pageUtil.getList();
				if(!QwyUtil.isNullAndEmpty(list)){
					getRequest().setAttribute("fundRecordList", list);
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "moneyDetails";
	}
	
	
	/**Ajax加载更多资金流水记录;
	 * @return
	 */
	public String loadFundRecordMore(){
		String json = "";
		try {
			PageUtil<FundRecord> pageUtil = new PageUtil<FundRecord>();
			pageUtil.setPageSize(10);
			pageUtil.setCurrentPage(currentPage);
			//pageUtil.setPageUrl(getRequest().getServletContext().getContextPath()+"/Product/User/fundRecord!loadFundRecord.action?type="+type+"&startDate="+startDate+"&endDate="+endDate);
			pageUtil = fundRecordBean.loadFundRecord(Long.parseLong(usersId),null,type,startDate,endDate,pageUtil);
			if(!QwyUtil.isNullAndEmpty(pageUtil)){
				getRequest().setAttribute("pageUtil", pageUtil);
				List<FundRecord> list = pageUtil.getList();
				if(!QwyUtil.isNullAndEmpty(list)){
					JSONArray jsonArray = JSONArray.fromObject(list);
					json = QwyUtil.getJSONString("ok", jsonArray);
				}else{
					json = QwyUtil.getJSONString("noData", "没有更多数据");
				}
			}else{
				json = QwyUtil.getJSONString("noData", "没有更多数据");
			}
		} catch (Exception e) {
			json = QwyUtil.getJSONString("error", "加载异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("loadFundRecordMore",e);
		}
		return null;
	}
	
	public String getProductId() {
		return productId;
	}


	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	
	
	public Integer getCurrentPage() {
		return currentPage;
	}


	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}


	public Integer getPageSize() {
		return pageSize;
	}


	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	




	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Long getZlq() {
		return zlq;
	}


	public void setZlq(Long zlq) {
		this.zlq = zlq;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	

	public String getUsersId() {
		return usersId;
	}
	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}


	class MyComparator implements Comparator<Investors>{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Investors o1, Investors o2) {
			if(o1.getPayTime().before(o2.getPayTime())){
				return 1;
			}else{
				return -1;
			}
		}
	}
}

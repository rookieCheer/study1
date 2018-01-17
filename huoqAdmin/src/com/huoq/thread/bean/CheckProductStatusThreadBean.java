package com.huoq.thread.bean;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.Product;
import com.huoq.thread.dao.ThreadDAO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**后台线程bean--更新理财产品;
 * @author yks
 *
 * @createTime 2016-9-25
 */
@Service
public class CheckProductStatusThreadBean {
	private Logger log = Logger.getLogger(CheckProductStatusThreadBean.class);
	@Resource
	private ThreadDAO dao;
	@Resource
	private RegisterUserBean registerUserBean;
	

	/**
	 * 按状态查找产品，即将售罄，已售罄
	 * @param investType 投资类别  0:车无忧 1:贸易通
	 * @param status 产品状态 0:营销中  1:已售罄  2:结算中   3:已还款
	 * @param type 类别  0为普通项目,1为:新手专享
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<Product> findSQProductByStatus(PageUtil<Product> pageUtil,String[] investType,String[] status,String[] type)throws Exception{
		StringBuffer hql = new StringBuffer();
		String myStatus = QwyUtil.packString(status);
		hql.append(" FROM Product pro ");
		hql.append(" WHERE 1=1 ");
		if(!QwyUtil.isNullAndEmpty(investType)){
			String myInvestType = QwyUtil.packString(investType);
			hql.append(" AND pro.investType IN ("+myInvestType+") ");
		}
		if(!QwyUtil.isNullAndEmpty(status)){
			hql.append(" AND pro.productStatus IN ("+myStatus+") ");
		}
		if(!QwyUtil.isNullAndEmpty(type)){
			String myType = QwyUtil.packString(type);
			hql.append(" AND pro.productType IN ("+myType+") ");
		}
		if(myStatus.equals("0")){
			hql.append(" AND pro.leftCopies * 10 <= pro.allCopies");//当产品未售罄时且剩余份数小于等于总份数的10%时
		}
		if(myStatus.equals("1")){
			hql.append(" AND pro.leftCopies = 0 AND pro.hasCopies = pro.allCopies");//当产品已售罄时
		}
		hql.append(" ORDER BY pro.insertTime ASC, pro.id DESC ");
		return (PageUtil<Product>)dao.getPage(pageUtil, hql.toString(), null);
	}
	
	
	/**自动售罄理财产品
	 * @param product
	 */
	public void autoClearProduct(Product product){
		if(QwyUtil.isNullAndEmpty(product)){
			return;
		}
		try {
			//项目结束时间;日期格式yyyy-MM-dd
			Date finishTime = QwyUtil.fmyyyyMMdd.parse(QwyUtil.fmyyyyMMdd.format(product.getFinishTime()));
			//当前时间;日期格式yyyy-MM-dd
			Date nowDate = QwyUtil.fmyyyyMMdd.parse(QwyUtil.fmyyyyMMdd.format(new Date()));
			boolean isUpdate = false;
			if(nowDate.getTime()>=finishTime.getTime()){
				isUpdate = true;
				//项目的结束时间已经到了;把项目进入"结算中"状态;
				product.setProductStatus("2");
				product.setIsRecommend("0");
				product.setClearingTime(new Date());
				log.info("产品: "+product.getTitle()+"进入结算中; id: "+product.getId());
			}else {
				isUpdate = true;
				//产品自动售罄
				product.setProductStatus("1");//状态改为"已售罄"
				product.setLeftCopies(0L); //剩余份数清0
				product.setHasCopies(product.getAllCopies());//跟投份数变为总份数，进度达到100%
				product.setIsRecommend("0");//已售罄的时候,把推荐的属性给去掉;
				log.info("产品: "+product.getTitle()+"已自动售罄，准备续约id: "+product.getId());
			}
			if(isUpdate){
				dao.saveOrUpdate(product);
				if(product.getProductStatus().equals("1")){
			/*		//发送短信到对发布产品人员的手机上通知产品是否到期
					boolean b = registerUserBean.sendSms("13588872099", "产品:"+product.getTitle()+" 已到期请尽快处理【新华金典】");
					int temp = 0;
					while (!b == true){
						if(temp < 10 ){
							b = registerUserBean.sendSms("13588872099", "产品:" + product.getTitle() + " 已到期请尽快处理【新华金典】");
						}else{
							break;
						}
						temp++;
					}*/
				}
			}
			
		} catch (ParseException e) {
			log.error("操作异常: ",e);
		}
		
	}

	/**
	 * 模糊查找预约中的理财产品
	 * 按照 ORDER BY pro.insertTime ASC, pro.id DESC 来排序
	 *
	 * @param titleLike      产品名称
	 * @param investType    投资类别  0:车无忧 1:贸易通
	 * @param productStatus //产品状态 -3:排队中 -2：审核不通过 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款
	 * @param type          类别  0为普通项目,1为:新手专享;
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Product> getYYProducts(String titleLike, String[] investType,
										   String[] productStatus, String[] type) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM Product pro ");
		hql.append(" WHERE 1=1 ");
		if (!QwyUtil.isNullAndEmpty(titleLike)) {
			hql.append(" AND pro.title LIKE CONCAT('%" + titleLike + "%')");
		}
		if (!QwyUtil.isNullAndEmpty(investType)) {
			String myInvestType = QwyUtil.packString(investType);
			hql.append(" AND pro.investType IN (" + myInvestType + ") ");
		}
		if (!QwyUtil.isNullAndEmpty(productStatus)) {
			String myStatus = QwyUtil.packString(productStatus);
			hql.append(" AND pro.productStatus IN (" + myStatus + ") ");
		}
		if (!QwyUtil.isNullAndEmpty(type)) {
			String myType = QwyUtil.packString(type);
			hql.append(" AND pro.productType IN (" + myType + ") ");
		}
		hql.append(" AND NOW() <= pro.finishTime  ");
		hql.append(" ORDER BY pro.insertTime ASC, pro.id DESC ");
		return dao.LoadAll(hql.toString(), null);
	}

	/**
	 * 检查是否有预约中的理财产品
	 * 按照 ORDER BY pro.insertTime ASC, pro.id DESC 来排序
	 *
	 * @param investType    投资类别  0:车无忧 1:贸易通
	 * @param productStatus //产品状态 -3:排队中 -2：审核不通过 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款
	 * @param type          类别  0为普通项目,1为:新手专享;
	 * @param titleLike 模糊名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasYYProducts(String[] investType,
								 String[] productStatus, String[] type,String titleLike) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM Product pro ");
		hql.append(" WHERE 1=1 ");
		if (!QwyUtil.isNullAndEmpty(investType)) {
			String myInvestType = QwyUtil.packString(investType);
			hql.append(" AND pro.investType IN (" + myInvestType + ") ");
		}
		if (!QwyUtil.isNullAndEmpty(productStatus)) {
			String myStatus = QwyUtil.packString(productStatus);
			hql.append(" AND pro.productStatus IN (" + myStatus + ") ");
		}
		if (!QwyUtil.isNullAndEmpty(type)) {
			String myType = QwyUtil.packString(type);
			hql.append(" AND pro.productType IN (" + myType + ") ");
		}
		if (!QwyUtil.isNullAndEmpty(titleLike)) {
			hql.append(" AND pro.title LIKE CONCAT('%" + titleLike + "%')");
		}
		hql.append(" AND NOW() <= pro.finishTime  ");
		hql.append(" ORDER BY pro.insertTime ASC, pro.id DESC ");
		List<Product> yyProducts = dao.LoadAll(hql.toString(),null);
		if(null != yyProducts && yyProducts.size() > 0){
			return true;
		}else {
			return false;
		}

	}


	/**
	 * 更新理财产品状态
	 *
	 * @param product
	 */
	public void updateProductStatus(Product product) {
		if (QwyUtil.isNullAndEmpty(product)) {
			return;
		}
		try {
			boolean isUpdate = false;
			if ("-3".equals(product.getProductStatus())) {
				isUpdate = true;
				//续约，将状态改为"0"：营销中
				product.setProductStatus("0");
				product.setUpdateTime(new Date());
				//更新产品结束时间
				long fTime = (product.getLcqx())*24*60*60*1000 + new Date().getTime();
				product.setFinishTime(new Date(fTime));
				long eTime = (product.getLcqx()-2)*24*60*60*1000 + new Date().getTime();
				product.setEndTime(new Date(eTime));
				log.info("产品: " + product.getTitle() + "已进入营销中; id: " + product.getId());
			}
			if (isUpdate) {
				dao.saveOrUpdate(product);
			}

		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
	}

	/**
	 * 是否有在售中理财产品
	 * @param titleLike 模糊名称
	 * @param investType 投资类别  0:车无忧 1:贸易通
	 * @param type 类别  0为普通项目,1为:新手专享
	 * @return
	 * @throws Exception
	 */
    public boolean hasZSProduct(String titleLike,String[] investType,String[] type) throws Exception{
    	boolean result = false;
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM Product pro ");
		hql.append(" WHERE 1=1 ");
		if (!QwyUtil.isNullAndEmpty(titleLike)) {
			hql.append(" AND pro.title LIKE CONCAT('%" + titleLike + "%')");
		}
		if(!QwyUtil.isNullAndEmpty(investType)){
			String myInvestType = QwyUtil.packString(investType);
			hql.append(" AND pro.investType IN ("+myInvestType+") ");
		}
		if(!QwyUtil.isNullAndEmpty(type)){
			String myType = QwyUtil.packString(type);
			hql.append(" AND pro.productType IN ("+myType+") ");
		}
		hql.append(" AND pro.productStatus = 0 ");
		hql.append(" AND NOW() <= pro.finishTime  ");
		hql.append(" ORDER BY pro.insertTime ASC, pro.id DESC ");
		List<Product> zsProducts = dao.LoadAll(hql.toString(),null);
		if(null != zsProducts && zsProducts.size() > 0){
			result = true;
		}
		return result;
	}


	
}

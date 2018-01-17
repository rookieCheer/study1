package com.huoq.admin.product.bean;

import com.huoq.admin.product.dao.ReleaseProductDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import com.huoq.product.bean.IndexBean;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**发布产品Bean层
 * @author qwy
 *
 * @createTime 2015-04-16 13:49:00
 */
@Service
public class ReleaseProductBean {
	private static Logger log = Logger.getLogger(ReleaseProductBean.class);
	@Resource
	ReleaseProductDAO dao;
	@Resource
	IndexBean bean;
	@Resource
	private VirtualInsRecordBean virtualInsRecordBean;
	
	/**添加新产品
	 * @param product Product 实体;
	 * @return 返回新生成的id;
	 */
	public String saveProduct(Product product){
		try {
			product.setAllCopies(QwyUtil.calcNumber(product.getFinancingAmount(), 100, "/").longValue());
			product.setHasCopies(0L);
			product.setLeftCopies(product.getAllCopies());
			product.setProductType("0");
			product.setProductStatus("0");
			product.setUserCount(0L);
			product.setInsertTime(new Date());
			product.setProgress(0d);
			product.setJiangLiEarnings(QwyUtil.isNullAndEmpty(product.getJiangLiEarnings())?0:product.getJiangLiEarnings());

		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return dao.save(product);
	}

    /**添加预约新产品
     * @param yyproduct Product 实体;
     * @return 返回新生成的id;
     */
	  public String saveYYProduct(Product yyproduct){
	        try {
	            yyproduct.setAllCopies(QwyUtil.calcNumber(yyproduct.getFinancingAmount(), 100, "/").longValue());
	            yyproduct.setHasCopies(0L);
	            yyproduct.setLeftCopies(yyproduct.getAllCopies());
	            yyproduct.setProductStatus("-3");
	            yyproduct.setUserCount(0L);
	            yyproduct.setInsertTime(new Date());
	            yyproduct.setBookingTime(yyproduct.getInsertTime());
	            yyproduct.setProgress(0d);
	            yyproduct.setJiangLiEarnings(QwyUtil.isNullAndEmpty(yyproduct.getJiangLiEarnings())?0:yyproduct.getJiangLiEarnings());
	            yyproduct.setAnnualEarnings( yyproduct.getBaseEarnings());
				yyproduct.setiStatus(0);
				yyproduct.setDisplayBaseEarnings(yyproduct.getDisplayBaseEarnings());
				yyproduct.setDisplayExtraEarnings(yyproduct.getDisplayExtraEarnings());
	        } catch (Exception e) {
	            log.error("操作异常: ",e);
	        }
	        return dao.save(yyproduct);
	    }
	
	/**
	 * 
	 */
	/**
	 * 得到JASPER
	 * @param product 产品
	 * @param finishTime 项目到期时间
	 * @param insertTime 项目插入时间 
	 * @param username 用户名
	 * @param productStatus 产品状态
	 * @param sourceFileName 路径
	 * @return
	 * @throws Exception
	 */
	public List<JasperPrint> getProductJasperPrintList(Product product, String finishTime, String insertTime, String username, String productStatus,String sourceFileName) throws Exception{
		List<JasperPrint> list=new ArrayList<JasperPrint>();
		List<Product> products=findProductPageUtil(product, finishTime, insertTime, username, productStatus);
		List<ProductTable> productTables=productTables(products);
		JRBeanCollectionDataSource ds=new JRBeanCollectionDataSource(productTables);	
		//JasperPrint 	 js=JasperFillManager.fillReport(context.getRealPath(path) +File.separator+getJxmlStr(), map, ds);"D:\\table"+File.separator+"releaseProduct.jasper"
		JasperPrint js=JasperFillManager.fillReport(sourceFileName, null, ds);
		list.add(js);
		return list;
	}
	
	/**
	 * 得到利息总表JASPER
	 * @param product 产品
	 * @param finishTime 项目到期时间
	 * @param sourceFileName 路径
	 */
	public List<JasperPrint> getFXJasperPrintList(Product product, String finishTime,String sourceFileName) throws Exception{
		List<JasperPrint> list=new ArrayList<JasperPrint>();
		List<Product> products=findProductPageUtil(product, finishTime, null, null, null,"finishTime");
		List<Fxzb> fxzbTables=FxzbTables(products);
		JRBeanCollectionDataSource ds=new JRBeanCollectionDataSource(fxzbTables);	
		//JasperPrint 	 js=JasperFillManager.fillReport(context.getRealPath(path) +File.separator+getJxmlStr(), map, ds);"D:\\table"+File.separator+"releaseProduct.jasper"
		JasperPrint js=JasperFillManager.fillReport(sourceFileName, null, ds);
		list.add(js);
		return list;
	}
	
	/**
	 * 转换为付息表格bean
	 */
	public List<Fxzb> FxzbTables(List<Product> products){
		Map<String, String> map=findInvestorsByProductId();
		List<Fxzb>  fxzbTables=new ArrayList<Fxzb>();
		try {
			if (!QwyUtil.isNullAndEmpty(products)) {
				for (int i = 0; i < products.size(); i++) {
					Product product = products.get(i);
					Fxzb fxzb = new Fxzb();
					fxzb.setAllCopies(product.getAllCopies() + "");
					fxzb.setAnnualEarnings((product.getAnnualEarnings())+ "%");
					fxzb.setCplx(product.getCplx());
					fxzb.setFinishTime(QwyUtil.fmyyyyMMdd.format(product.getFinishTime()));
					fxzb.setQtje(QwyUtil.calcNumber(product.getQtje(),
							0.01, "*",2) + "");
					fxzb.setTitle(product.getTitle());
					fxzb.setSflx(QwyUtil.calcNumber(map.get(product.getId()),100, "/",2)+"");
					fxzb.setFxfs(product.getFxfs());
					fxzb.setJxfs(product.getJxfs());
					fxzb.setInsertTime(QwyUtil.fmyyyyMMddHHmmss.format(product.getInsertTime()));
					fxzbTables.add(fxzb);
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return  fxzbTables;	
	}
	
	
	/**
	 * 转换为表格bean
	 */
	public List<ProductTable> productTables(List<Product> products){
		Map<String, String> map=findInvestorsByProductId();
		List<ProductTable>  productTables=new ArrayList<ProductTable>();
		Map<String,Double> mapProductToVirtual=virtualInsRecordBean.MapProductToVirtual();
		try {
			if (!QwyUtil.isNullAndEmpty(products)) {
				for (int i = 0; i < products.size(); i++) {
					Product product = products.get(i);
					ProductTable productTable = new ProductTable();
					productTable.setIndex(i + "");
					productTable.setAllCopies(product.getAllCopies() + "");
					productTable
							.setAnnualEarnings((product.getAnnualEarnings())
									+ "%");
					productTable.setCplx(product.getCplx());
					productTable.setCpzt(product.getCpzt());
					productTable.setFinishTime(QwyUtil.fmyyyyMMdd
							.format(product.getFinishTime()));
					productTable.setInsertTime(QwyUtil.fmyyyyMMddHHmmss
							.format(product.getInsertTime()));
					productTable.setLcqx(product.getLcqx() + "");
					productTable.setLeftCopies(product.getLeftCopies() + "");
					productTable.setQtje(QwyUtil.calcNumber(product.getQtje(),
							0.01, "*",2) + "");
					productTable.setTitle(product.getTitle());
					productTable.setTzqx(product.getTzqx() + "");
					productTable.setWcjd((product.getWcjd() * 100) + "%");
					productTable.setHasCopies(product.getHasCopies() + "");
					productTable.setSflx(QwyUtil.calcNumber(map.get(product.getId()),100, "/",2)+"");
					productTable.setYflx(product.getYflx().toString());
					productTable.setSjHashCopies((product.getHasCopies()-QwyUtil.calcNumber(mapProductToVirtual.get(product.getId()),100, "/",0).longValue())+"");
					productTable.setXnCopies(QwyUtil.calcNumber(mapProductToVirtual.get(product.getId()),100, "/",2)+"");
					productTables.add(productTable);
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return  productTables;	
	}
/**
 * 转换单个实体
 */
	/*public ProductTable productTable(Product product){
		productTable.
		return o;
		
	}*/
	
	
	/**获取已发布的产品总数;(包含新手产品,包含预约中的产品)
	 * @param type   0为普通项目,1为:新手专享;
	 * @return
	 */
	public int getProductCount(String[] type){
		String myType = QwyUtil.packString(type);
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(*) FROM Product pro ");
		hql.append("WHERE pro.productStatus IN ('-3','0','1','2','3') ");
		if(!QwyUtil.isNullAndEmpty(myType)){
			hql.append("AND pro.productType IN ("+myType+")");
		}
		Object obj = dao.findJoinActive(hql.toString(), null);
		return obj==null?0:Integer.parseInt(obj.toString())>0?Integer.parseInt(obj.toString()):0;
	}
	
	/**获取已发布的产品的金额;(包含新手产品)
	 * @param type   0为普通项目,1为:新手专享;
	 * @return
	 */
	public Double getProductAllMoney(String[] type){
		String myType = QwyUtil.packString(type);
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT SUM(pro.financingAmount) FROM Product pro ");
		hql.append("WHERE pro.productStatus IN ('0','1','2','3') ");
		if(!QwyUtil.isNullAndEmpty(myType)){
			hql.append("AND pro.productType IN ("+myType+")");
		}
		Object obj = dao.findJoinActive(hql.toString(), null);
		return obj==null?0:Double.parseDouble(obj.toString())>0?Double.parseDouble(obj.toString()):0L;
	}
	
	
	
	/**获取已发布的产品总数;(包含新手产品)
	 * @param type   0为普通项目,1为:新手专享;
	 * @param status 产品状态 0:营销中  1:已售罄  2:结算中   3:已还款 
	 * @return
	 */
	public int getProductCount(String[] type, String [] status){
		String myType = QwyUtil.packString(type);
		String mystatus = QwyUtil.packString(status);
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT COUNT(*) FROM Product pro ");
		hql.append("WHERE pro.productStatus IN ("+mystatus+") ");
		if(!QwyUtil.isNullAndEmpty(myType)){
			hql.append("AND pro.productType IN ("+myType+")");
		}
		Object obj = dao.findJoinActive(hql.toString(), null);
		return obj==null?0:Integer.parseInt(obj.toString())>0?Integer.parseInt(obj.toString()):0;
	}
	
	/**
	 * 修改状态
	 * id 产品ID
	 * status 状态
	 */
	public boolean updateProductStatus(String id,String status){
		Product product=bean.getProductById(id);
		if(!QwyUtil.isNullAndEmpty(product)){
			product.setProductStatus(status);
			if("1".equals(product.getProductType())){
				//如果是新手产品主动进入售罄的话,则需要更新进入售罄时间;
				product.setClearingTime(new Date());
			}
			product.setRecommend("0");
			product.setUpdateTime(new Date());
			dao.update(product);
			return true;
		}
		return false;	
	}
	
	
	
	/**
	 * 分页获取产品记录
	 * @param pageUtil 分页工具类
 	 * @param product
	 * @param username 用户名
	 * @param insertTime 插入时间
	 * @param finishTime  结束时间
	 * @param productStatus  产品状态 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款 
	 * @return
	 * @throws Exception 
	 */
	public PageUtil<Product> findProductPageUtil(PageUtil<Product> pageUtil,Product product, String finishTime, String insertTime, String username, String productStatus) throws Exception{
		return findProductPageUtil(pageUtil, product, finishTime, insertTime, username, productStatus,"finishTime");
		
	}
	
	
	/**
	 * 分页获取产品记录
	 * @param pageUtil 分页工具类
 	 * @param product
	 * @param username 用户名
	 * @param insertTime 插入时间
	 * @param finishTime  结束时间
	 * @param productStatus  产品状态 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款
	 * @param DESCName  排序名称
	 * @return 
	 * @throws Exception 
	 */
	public PageUtil<Product> findProductPageUtil(PageUtil<Product> pageUtil,Product product, String finishTime, String insertTime, String username, String productStatus, String DESCName) throws Exception{
		StringBuffer hql=new StringBuffer();
		List<Object> list = new ArrayList<Object>();
		
		
		hql.append(" FROM Product pro WHERE 1=1 "); 
		if(!QwyUtil.isNullAndEmpty(product)){
			//项目总额
			if(!QwyUtil.isNullAndEmpty(product.getFinancingAmount())){
				hql.append(" AND pro.financingAmount = ? ");
				list.add(product.getFinancingAmount()*100);
			}
			//产品名称
			if(!QwyUtil.isNullAndEmpty(product.getTitle())){
				hql.append(" AND pro.title like '%"+product.getTitle()+"%' ");
//				list.add("%"+product.getTitle()+"%");
			}
			//年化收益
			if(!QwyUtil.isNullAndEmpty(product.getAnnualEarnings())){
				hql.append(" AND pro.annualEarnings = ? ");
				list.add(product.getAnnualEarnings());
			}
			//项目类型
			if(!QwyUtil.isNullAndEmpty(product.getInvestType())){
				hql.append(" AND pro.investType = ? ");
				list.add(product.getInvestType());
			}
		}	
		//发布时间
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				hql.append(" AND pro.insertTime >= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND pro.insertTime < ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" AND pro.insertTime >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND pro.insertTime <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		//到期时间
		if(!QwyUtil.isNullAndEmpty(finishTime)){
			String [] time=QwyUtil.splitTime(finishTime);
			if(time.length>1){
				hql.append(" AND pro.finishTime >= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND pro.finishTime <= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" AND pro.finishTime >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND pro.finishTime <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		//产品状态
		if(!QwyUtil.isNullAndEmpty(productStatus)){
			hql.append(" AND pro.productStatus = ? ");
			list.add(productStatus);
		}
		if(!QwyUtil.isNullAndEmpty(DESCName)){
			hql.append(" ORDER By  pro."+DESCName+" DESC ");
		}else{
			hql.append(" ORDER By  pro.insertTime DESC ");
		}
		return dao.getByHqlAndHqlCount(pageUtil, hql.toString(), hql.toString(), list.toArray());
		
	}
	
	/**
	 *  产品历史发布记录  含虚拟投资金额
	 * @param pageUtil 分页工具
	 * @param product
	 * @param finishTime
	 * @param insertTime 
	 * @param username 用户名
	 * @param productStatus 产品状态
	 * @param DESCName 排序列
	 * @return
	 * @throws Exception
	 */
	public PageUtil<Product> findProductsPageUtil(PageUtil<Product> pageUtil,Product product, String finishTime, String insertTime, String username, String productStatus, String DESCName,String realName) throws Exception{
		StringBuffer sql=new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append(" SELECT p.product_status,p.title,p.invest_type,p.annual_earnings,jiang_li_earnings,financing_amount,finish_time,insert_time,");
		sql.append(" (SELECT in_money*0.01 FROM virtual_ins WHERE product_id = p.id GROUP BY product_id )sumVir,");
		sql.append(" (SELECT SUM(copies) FROM investors WHERE product_id = p.id AND investor_status IN ('1','2','3'))sumInv,product_type,id,iStatus,p.real_name ");
		sql.append(" FROM product p ");
		sql.append(" WHERE 1=1");
		if (!QwyUtil.isNullAndEmpty(product)) {
			//项目总额
			if (!QwyUtil.isNullAndEmpty(product.getFinancingAmount())) {
				sql.append(" AND financing_amount = "+product.getFinancingAmount()*100);
			}
			
			//产品名称
			if (!QwyUtil.isNullAndEmpty(product.getTitle())) {
				sql.append(" AND title LIKE '%"+product.getTitle()+"%'");
			}
			//借款企业名称
			if (!QwyUtil.isNullAndEmpty(realName)) {
				sql.append(" AND p.real_name LIKE '%"+realName.trim()+"%'");
			}
			//年化收益
			if (!QwyUtil.isNullAndEmpty(product.getAnnualEarnings())) {
				sql.append(" AND annual_earnings ="+product.getAnnualEarnings());
			}
			//项目类型
			if (!QwyUtil.isNullAndEmpty(product.getInvestType())) {
				sql.append(" AND invest_type = '"+product.getInvestType()+"'");
			}
			//发布时间
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String [] times=QwyUtil.splitTime(insertTime);
				if (times.length > 1) {
					sql.append(" AND insert_time >= ? ");
					params.add( QwyUtil.fmMMddyyyy.parse(times[0]));
					sql.append(" AND insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
				} else {
					sql.append(" AND insert_time >= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
					sql.append(" AND insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
				}
			}
			//到期时间
			if (!QwyUtil.isNullAndEmpty(finishTime)) {
				String [] times=QwyUtil.splitTime(finishTime);
				if (times.length > 1) {
					sql.append(" AND finish_time >= ? ");
					params.add( QwyUtil.fmMMddyyyy.parse(times[0]));
					sql.append(" AND finish_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
				} else {
					sql.append(" AND finish_time >= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
					sql.append(" AND finish_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
				}
			}
			//产品状态
			if (!QwyUtil.isNullAndEmpty(productStatus)) {
				sql.append(" AND product_status = '"+productStatus+"'");
			}
		}
		
		if(!QwyUtil.isNullAndEmpty(DESCName)){
			sql.append(" ORDER By  p."+DESCName+" DESC ");
		}else{
			sql.append(" ORDER By  p.insert_time DESC ");
		}
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM ( ");
		sqlCount.append(sql);
		sqlCount.append(" )t");
		
		return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params.toArray());
		
	}
	
	/**
	 * 根据产品ID获取投资记录
	 */
	
	public Map<String, ProductAccount> findInvAccountByProductId(){
		
		Map<String, ProductAccount> map=new HashMap<String, ProductAccount>();
		StringBuffer buffer=new StringBuffer();
		buffer.append(" SELECT  pd.id  , SUM(ifnull(ivs.all_shouyi,0)) as all_shouyi, sum(ifnull(ivs.coupon,0)) as coupon , SUM(ifnull(ivs.in_money,0)) as in_money FROM product pd LEFT JOIN  investors ivs on pd.id = ivs.product_id GROUP BY pd.id ");
		List<Object []> list=dao.LoadAllSql(buffer.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			for (Object [] objects : list) {
				ProductAccount  p = new ProductAccount();
				p.setAll_shouyi(Double.parseDouble(objects[1]+""));
				p.setCoupon(Double.parseDouble(objects[2]+""));
				p.setIn_money(Double.parseDouble(objects[3]+""));
				map.put(objects[0]+"",p );
			}
		}
		return map;
	}
	
	
	/**
	 * 根据产品ID获取投资记录
	 */
	
	public Map<String, String> findInvestorsByProductId(){
		Map<String, String> map=new HashMap<String, String>();
		StringBuffer buffer=new StringBuffer();
		buffer.append(" SELECT  pd.id  , SUM(ivs.all_shouyi) FROM product pd LEFT JOIN  investors ivs on pd.id = ivs.product_id GROUP BY pd.id ");
		List<Object []> list=dao.LoadAllSql(buffer.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			for (Object [] objects : list) {
				map.put(objects[0]+"",!QwyUtil.isNullAndEmpty(objects[1])?objects[1]+"":"0" );
			}
		}
		return map;
	}
	
//	public List<Product> findProductPageUtil(Product product, String finishTime, String insertTime, String username, String productStatus) throws Exception{
//		return findProductPageUtil(Product product, String finishTime, String insertTime, String username, String productStatus,null)
//	}
	/**
	 * 分页获取产品记录
 	 * @param product
	 * @param username 用户名
	 * @param insertTime 插入时间
	 * @param finishTime  结束时间
	 * @param productStatus  产品状态 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<Product> findProductPageUtil(Product product, String finishTime, String insertTime, String username, String productStatus) throws Exception{
		return findProductPageUtil(product,finishTime,insertTime,username,productStatus,"finishTime");
	}
	/**
	 * 分页获取产品记录
 	 * @param product
	 * @param username 用户名
	 * @param insertTime 插入时间
	 * @param finishTime  结束时间
	 * @param productStatus  产品状态 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款 
	 * @param DESCName 排序列名
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<Product> findProductPageUtil(Product product, String finishTime, String insertTime, String username, String productStatus, String DESCName) throws Exception{
		return findProductPageUtil(product, finishTime, insertTime, username, productStatus, DESCName, null);
		
	}
	
	
	/**
	 * 分页获取产品记录
 	 * @param product
	 * @param username 用户名
	 * @param insertTime 插入时间
	 * @param finishTime  结束时间
	 * @param productStatus  产品状态 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款 
	 * @param DESCName 排序列名
	 * @param productType 项目类型
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<Product> findProductPageUtil(Product product, String finishTime, String insertTime, String username, String productStatus, String DESCName,String productType) throws Exception{
		List<Object> list=new ArrayList<Object>();
		StringBuffer hql=new StringBuffer();
		hql.append(" FROM Product pro WHERE 1=1 "); 
		if(!QwyUtil.isNullAndEmpty(product)){
			//项目总额
			if(!QwyUtil.isNullAndEmpty(product.getFinancingAmount())){
				hql.append(" AND pro.financingAmount = ? ");
				list.add(product.getFinancingAmount()*100);
			}
			//产品名称
			if(!QwyUtil.isNullAndEmpty(product.getTitle())){
				hql.append(" AND pro.title like '%"+product.getTitle()+"%' ");
//				list.add("%"+product.getTitle()+"%");
			}
			//年化收益
			if(!QwyUtil.isNullAndEmpty(product.getAnnualEarnings())){
				hql.append(" AND pro.annualEarnings = ? ");
				list.add(product.getAnnualEarnings());
			}
			//项目类型
			if(!QwyUtil.isNullAndEmpty(product.getInvestType())){
				hql.append(" AND pro.investType = ? ");
				list.add(product.getInvestType());
			}
			
		}	
		//发布时间
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				hql.append(" AND pro.insertTime >= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND pro.insertTime <= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" AND pro.insertTime >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND pro.insertTime <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		//到期时间
		if(!QwyUtil.isNullAndEmpty(finishTime)){
			String [] time=QwyUtil.splitTime(finishTime);
			if(time.length>1){
				hql.append(" AND pro.finishTime >= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND pro.finishTime <= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" AND pro.finishTime >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND pro.finishTime <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		//产品状态
		if(!QwyUtil.isNullAndEmpty(productStatus)){
			hql.append(" AND pro.productStatus = ? ");
			list.add(productStatus);
		}
		if(!QwyUtil.isNullAndEmpty(productType)){
			hql.append(" AND pro.productType = ? ");
			list.add(productType);
		}
		if(!QwyUtil.isNullAndEmpty(DESCName)){
			hql.append(" ORDER By  pro."+DESCName+" DESC ");
		}else{
			hql.append(" ORDER By  pro.insertTime DESC ");
		}
		return dao.LoadAll(hql.toString(), list.toArray());
	}
	
	
	
	
	/**
	 * 分页资金速动明细表
	 * @param pageUtil 分页工具类
	 * @param insertTime 插入时间
	 * @return 
	 * @throws Exception 
	 */
	public PageUtil<Zjsumx> findZjsdmxPageUtil(PageUtil pageUtil,String insertTime) throws Exception{
		List<Object> list=new ArrayList<Object>();
		StringBuffer hql=new StringBuffer();
		hql.append(" select DATE_FORMAT(dd.insert_time, '%Y-%m-%d') AS date, product.financing_amount ,interest_details.sumMoney from dateday  dd"); 
		hql.append(" LEFT JOIN (	SELECT(SUM(ids.pay_interest) + SUM(ids.pay_money)) as sumMoney,"); 
		hql.append(" DATE_FORMAT(ids.return_time, '%Y-%m-%d') as return_time FROM");
		hql.append(" interest_details ids	GROUP BY	DATE_FORMAT(ids.return_time, '%Y-%m-%d')) interest_details ");
		hql.append(" ON interest_details.return_time =  DATE_FORMAT(dd.insert_time, '%Y-%m-%d') LEFT JOIN (");
		hql.append(" SELECT	SUM(pd.financing_amount) as financing_amount,DATE_FORMAT(pd.insert_time, '%Y-%m-%d') as insert_time");
		hql.append(" FROM product pd WHERE pd.product_status IN ('0', '1', '2', '3')");
		hql.append(" GROUP BY	DATE_FORMAT(pd.insert_time, '%Y-%m-%d'))product on product.insert_time =  DATE_FORMAT(dd.insert_time, '%Y-%m-%d')");
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				hql.append(" WHERE dd.insert_time >= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND dd.insert_time <= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" WHERE dd.insert_time >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND dd.insert_time <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		hql.append(" ORDER BY DATE_FORMAT(dd.insert_time,'%Y-%m-%d') DESC ");
		StringBuffer bufferCount=new StringBuffer();
		bufferCount.append(" SELECT COUNT(t.date)  ");
		bufferCount.append(" FROM (");
		bufferCount.append(hql);
		bufferCount.append(") t");
		pageUtil=dao.getBySqlAndSqlCount(pageUtil, hql.toString(), bufferCount.toString(), list.toArray());
		pageUtil.setList(toZjsumxs(pageUtil.getList()));
		return pageUtil;
	}
	
	/**
	 * 分页资金速动明细表
	 * @param insertTime 插入时间
	 * @return 
	 * @throws Exception 
	 */
	public List<Zjsumx> findZjsdmx(String insertTime) throws Exception{
		List<Object> list=new ArrayList<Object>();
		StringBuffer hql=new StringBuffer();
		hql.append(" SELECT DATE_FORMAT(dd.insert_time,'%Y-%m-%d') as date,  "); 
		hql.append(" ( SELECT SUM(pd.financing_amount) FROM product pd WHERE DATE_FORMAT(pd.insert_time,'%Y-%m-%d')=DATE_FORMAT(dd.insert_time,'%Y-%m-%d') AND pd.product_status in('0','1','2','3')  GROUP BY DATE_FORMAT(pd.insert_time,'%Y-%m-%d') ), "); 
		hql.append(" ( SELECT (SUM(ids.pay_interest)+SUM(ids.pay_money)) FROM interest_details ids WHERE DATE_FORMAT(ids.return_time,'%Y-%m-%d')=DATE_FORMAT(dd.insert_time,'%Y-%m-%d') "); 
		hql.append(" GROUP BY DATE_FORMAT(ids.return_time,'%Y-%m-%d') ) "); 
		hql.append(" FROM dateday dd  "); 
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				hql.append(" WHERE dd.insert_time >= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND dd.insert_time <= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" WHERE dd.insert_time >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND dd.insert_time <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		hql.append(" GROUP BY DATE_FORMAT(dd.insert_time,'%Y-%m-%d') ");
		hql.append(" ORDER BY DATE_FORMAT(dd.insert_time,'%Y-%m-%d') DESC ");
		List<Object []> objects=dao.LoadAllSql(hql.toString(), list.toArray());
		return toZjsumxs(objects);
	}
	/**
	 * 转换为付息表格bean
	 */
	public List<Zjsumx> toZjsumxs(List<Object []> list){
		List<Zjsumx>  zjsumxs=new ArrayList<Zjsumx>();
		try {
			if (!QwyUtil.isNullAndEmpty(list)) {
				/*Object [] obj=*/
				for (Object [] objects : list) {
					Zjsumx zjsumx=new Zjsumx();
					zjsumx.setDate(objects[0]+"");	
					zjsumx.setProductAllMoney(!QwyUtil.isNullAndEmpty(objects[1])?QwyUtil.calcNumber(objects[1], 100, "/", 2)+"":"0");
					zjsumx.setPayIns(!QwyUtil.isNullAndEmpty(objects[2])?QwyUtil.calcNumber(objects[2], 100, "/", 2)+"":"0");
					zjsumxs.add(zjsumx);
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return  zjsumxs;	
	}
	/**
	 * 得到JASPER
	 * @param insertTime 项目插入时间 
	 * @param sourceFileName 路径
	 * @return
	 * @throws Exception
	 */
	public List<JasperPrint> getZjsumxJasperPrintList(String insertTime,String sourceFileName) throws Exception{
		List<JasperPrint> list=new ArrayList<JasperPrint>();
		List<Zjsumx> zjsumxs=findZjsdmx(insertTime);
		JRBeanCollectionDataSource ds=new JRBeanCollectionDataSource(zjsumxs);	
		JasperPrint js=JasperFillManager.fillReport(sourceFileName, null, ds);
		list.add(js);
		return list;
	}
/**
 * 根据id查询产品	
 * @param id
 * @return
 */
	public Product findProductById(String id){
		return (Product)dao.findById(new Product(), id);
	}
/**
 * 修改产品
 */
	public Boolean updateProduct(Product newProduct){
		Product product=findProductById(newProduct.getId());
		product.setTitle(newProduct.getTitle());
		product.setHdby(newProduct.getHdby());
		product.setHdlj(newProduct.getHdlj());
		product.setHkly(newProduct.getHkly());
		product.setDescription(newProduct.getDescription());
		product.setZjbz(newProduct.getZjbz());
		product.setCplxjs(newProduct.getCplxjs());
		product.setUpdateTime(new Date());
		dao.saveOrUpdate(product);
		return true;
	}
	
	/**
	 * 修改合同表的用户标题	
	 * @param productId
	 * @return
	 */
		@SuppressWarnings("unchecked")
		public String modifyContractByProductId(String productId,String productTitle){
		  List<Contract> contractList=null;
			StringBuffer buff = new StringBuffer();
			buff.append("FROM Contract c ");
			buff.append("WHERE c.productId= ? ");
			contractList=dao.LoadAll(buff.toString(), new Object[]{productId});
			if(contractList!=null&&contractList.size()>0){
				for(Object object:contractList){
					Contract contract=(Contract)object;
					if(!productTitle.equals(contract.getProductTitle())){
						contract.setProductTitle(productTitle);
						contract.setUpdateTime(new Date());
						dao.saveOrUpdate(contract);
					}

				}
			}
		   return null;
		}

	/**
	 * 基金产品获取
	 * @param product (项目总额financingAmount,产品名称title,annualEarnings,investType)
	 * @param productType 类别 0为普通项目,1为:新手专享;2:基金产品
	 * @return
	 * @throws Exception
	 */
	public List<Product> findFundProduct(Product product, String productType) throws Exception{
		return findProductPageUtil(product, null, null, null, null, null, productType);
	}

    /**
     * 通过产品名称模糊查找预约产品,返回对象集合
     * @author yks
     * @date 2016-09-22
     * @param title
	 * @param productType 产品类型 0 常规 1新手
     * @return
     */
    public List<Product> getYYProductsLike(String title,String productType){
        try{
            log.info("模糊查找预约产品,title="+title+";productType =" +productType);
            StringBuffer hqlBuff = new StringBuffer();
            hqlBuff.append(" FROM Product p WHERE p.productStatus = -3 ");
            if(!QwyUtil.isNullAndEmpty(title)){
                hqlBuff.append(" AND p.title Like '%"+title+"%'");
            }
			if(!QwyUtil.isNullAndEmpty(productType)){
				hqlBuff.append(" AND p.productType = " + productType);
			}
            hqlBuff.append("ORDER BY p.insertTime DESC");
            log.info(hqlBuff.toString());
            List<Product> resultList = dao.LoadAll(hqlBuff.toString(),null);
            return resultList;
        }catch (Exception e){
            log.error("操作异常: ",e);
            log.error("系统错误",e.getCause());
        }
        return null;
    }

    /**
     * 发布常规产品时，查看是否存在排队中预约产品,有则删除该预约产品，返回删除的预约产品，无，返回null
     * @author yks
     * @date 2016-09-24
     * @param title
     * @return
     */
    public Product alterYYProducts(String title){
        try{
            StringBuffer hqlBuff = new StringBuffer();
            hqlBuff.append(" FROM Product p WHERE p.productStatus = -3 ");
            if(!QwyUtil.isNullAndEmpty(title)){
                hqlBuff.append(" AND p.title = ? ");
            }
            log.info(hqlBuff.toString());
            Product yyproduct= (Product) dao.findUniqueResult(hqlBuff.toString(),new Object[]{title});
            if(null != yyproduct){
                dao.delete(yyproduct);
                return yyproduct;
            }
        }catch (Exception e){
            log.error(e.getMessage());
            log.error("操作异常: ",e);
        }
        return null;
    }
    /**
     * 验证产品名称是否唯一
     * @param title
	 * @param productType 产品类型 0 常规 1新手
     * @return
     */
    public boolean checkTitleIsOnly(String title,String productType){
        log.info("验证产品名称是否唯一,title ="+title+";productType ="+productType);
        boolean result = false;
        try{
            StringBuffer hqlBuff = new StringBuffer();
            hqlBuff.append("  FROM Product p WHERE 1=1 ");
            if(!QwyUtil.isNullAndEmpty(title)){
                hqlBuff.append(" AND p.title = ?");
            }
            if (!QwyUtil.isNullAndEmpty(productType)){
				hqlBuff.append(" AND p.productType = ?");
			}
            log.info(hqlBuff.toString());
            List<Product> products = dao.LoadAll(hqlBuff.toString(),new Object[]{title,productType});
            if(QwyUtil.isNullAndEmpty(products)){
                result = true;
            }
        }catch (Exception e){
            log.error("操作异常:",e);
            log.error("操作异常: ",e);
        }
        return result;
    }

    /**
     * 模糊查找营销中的产品
     * @param title
	 * @param productType 产品类型 0 常规 1新手
     * @return
     */
    public Product getZSProductLIKE(String title,String productType){
        log.info("模糊查找营销中的产品,title ="+title +";productType ="+productType);
        try{
            StringBuffer hqlBuff = new StringBuffer();
            hqlBuff.append("  FROM Product p WHERE 1=1");
            if(!QwyUtil.isNullAndEmpty(title)){
				hqlBuff.append(" AND p.title LIKE CONCAT('%" + title + "%')");
            }
            if(!QwyUtil.isNullAndEmpty(productType)){
				hqlBuff.append(" AND p.productType = "+productType);
			}
            hqlBuff.append(" AND p.productStatus = 0 ");
            log.info(hqlBuff.toString());
            return (Product) dao.findUniqueResult(hqlBuff.toString(),null);
        }catch (Exception e){
            log.error("操作异常:",e);
            return null;
        }
    }

	/**
	 * 删除预约产品
	 * @param productId
	 * @throws Exception
	 */
    public void deleteYYProduct(String productId) throws Exception{
		Product product = new Product();
		product = (Product) dao.findById(product,productId);
		dao.delete(product);
	}


	/**
	 * 修改产品
	 */
	public Boolean updateYYProduct(Product newYYProduct) throws Exception{
		Product product=findProductById(newYYProduct.getId());
        Long newFinancialAmount = QwyUtil.calcNumber(
                newYYProduct.getFinancingAmount(), 100, "*").longValue();
        Long atleastMoney = QwyUtil.calcNumber(
                newYYProduct.getAtleastMoney(), 100, "*").longValue();
        product.setAllCopies(QwyUtil.calcNumber(newYYProduct.getFinancingAmount(), 100, "/").longValue());
        product.setHasCopies(0L);
        product.setLeftCopies(QwyUtil.calcNumber(newYYProduct.getFinancingAmount(), 100, "/").longValue());
		product.setTitle(newYYProduct.getTitle());
		product.setRealName(newYYProduct.getRealName());
		product.setIdcard(DESEncrypt.jiaMiIdCard(newYYProduct.getIdcard()));
		product.setPhone(DESEncrypt.jiaMiUsername(newYYProduct.getPhone()));
		product.setAddress(newYYProduct.getAddress());
		product.setInvestType(newYYProduct.getInvestType());
		product.setIsRecommend(newYYProduct.getIsRecommend());
		product.setCgcpType(newYYProduct.getCgcpType());
		product.setIsJiangLi(newYYProduct.getIsJiangLi());
        product.setJiangLiEarnings(newYYProduct.getJiangLiEarnings());
		product.setModule(newYYProduct.getModule());
		product.setFinancingAmount(newFinancialAmount);
		product.setAtleastMoney(atleastMoney);
		product.setBaseEarnings(newYYProduct.getBaseEarnings());
        product.setAnnualEarnings(newYYProduct.getAnnualEarnings());
		product.setPayInterestWay(newYYProduct.getPayInterestWay());
		product.setCalcInterestWay(newYYProduct.getCalcInterestWay());
		product.setLcqx(newYYProduct.getLcqx());
		product.setHdby(newYYProduct.getHdby());
		product.setHdlj(newYYProduct.getHdlj());
		product.setHkly(newYYProduct.getHkly());
		product.setDescription(newYYProduct.getDescription());
		product.setZjbz(newYYProduct.getZjbz());
		product.setCplxjs(newYYProduct.getCplxjs());
		product.setJkrxx(newYYProduct.getJkrxx());
		product.setRiskHints(newYYProduct.getRiskHints());
		product.setUpdateTime(new Date());
		product.setDisplayBaseEarnings(newYYProduct.getDisplayBaseEarnings());
		product.setDisplayExtraEarnings(newYYProduct.getDisplayExtraEarnings());
		dao.saveOrUpdate(product);
		return true;
	}

	/**获取最近一个产品的发起人的信息;
	 * @return
	 */
	public Product getProductLeader(){
		StringBuffer sb = new StringBuffer();
		sb.append("FROM Product pro ");
		sb.append("WHERE pro.productStatus ='0' ");
		sb.append("ORDER BY pro.insertTime DESC ");
		return (Product)dao.findJoinActive(sb.toString(), null);
	}

	/**根据单个状态获取理财产品
	 * @param productStatus //产品状态 -3:排队中 -2：审核不通过 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Product> getProductByStatus(String productStatus){
		StringBuffer sb = new StringBuffer();
		sb.append("FROM Product pro ");
		sb.append("WHERE pro.productStatus = ? ");
		sb.append("ORDER BY pro.productType DESC,pro.title ASC,pro.insertTime DESC ");
		return (List<Product>)dao.LoadAll(sb.toString(), new Object[]{productStatus});
	}
	
	/**
	 * 查询bookingkeyword表中的数据集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BookingKeyword> getBookingKwList(){
		StringBuffer sb = new StringBuffer();
		sb.append("FROM BookingKeyword bk ORDER BY bk.type asc,bk.status asc, bk.insertTime DESC ");
		
		return (List<BookingKeyword>)dao.LoadAll(sb.toString(), null);
	}
	
	/**
	 * 添加实体  BookingKeyword
	 * @param bookingKeyword
	 * @return
	 */
	public String addProduct(BookingKeyword bookingKeyword){
		return dao.saveAndReturnId(bookingKeyword);
	}
	
	/**
	 * 根据ID 修改状态  状态非0即1 0 已启动  1未启动
	 * @param id  
	 * @return
	 */
	public boolean updateStatusById(String id){
		BookingKeyword bookingKeyword = (BookingKeyword) dao.findById(new BookingKeyword(), id);
		if (!QwyUtil.isNullAndEmpty(bookingKeyword)) {
			if (!QwyUtil.isNullAndEmpty(bookingKeyword.getStatus()) && bookingKeyword.getStatus().equals("1")) {
				bookingKeyword.setStatus("0");
			} else {
				bookingKeyword.setStatus("1");
			}
			bookingKeyword.setUpdateTime(new Date());
		}
		dao.saveOrUpdate(bookingKeyword);
		return true;

	}


	/**
	 * 分页获取产品记录
	 * @param pageUtil
	 * @param product
	 * @return
	 * @throws Exception
	 */
	public PageUtil<Product> findProductPageList(PageUtil<Product> pageUtil,Product product) throws Exception{
		StringBuffer hql=new StringBuffer();
		List<Object> list = new ArrayList<Object>();
		hql.append(" FROM Product pro WHERE 1=1 ");
		hql.append(" AND pro.productStatus = '-3' or pro.productStatus = '0' ");
		if(!QwyUtil.isNullAndEmpty(product.getTitle())){
			hql.append(" ORDER By  pro."+product.getTitle()+" DESC ");
		}else{
			hql.append(" ORDER By  pro.insertTime DESC ");
		}
		return dao.getByHqlAndHqlCount(pageUtil, hql.toString(), hql.toString(), list.toArray());

	}

}




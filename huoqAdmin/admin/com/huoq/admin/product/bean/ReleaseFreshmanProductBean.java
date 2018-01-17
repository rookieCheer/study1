package com.huoq.admin.product.bean;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.ReleaseProductDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Product;

/**发布产品Bean层
 * @author qwy
 *
 * @createTime 2015-04-16 13:49:00
 */
@Service
public class ReleaseFreshmanProductBean {
	private static Logger log = Logger.getLogger(ReleaseFreshmanProductBean.class);
	@Resource
	ReleaseProductDAO dao;
	
	/**添加新产品  新手产品
	 * @param product Product 实体;
	 * @return 返回新生成的id;
	 */
	public String saveProduct(Product product){
		try {
			product.setAllCopies(QwyUtil.calcNumber(product.getFinancingAmount(), 100, "/").longValue());
			product.setHasCopies(0L);
			product.setLeftCopies(product.getAllCopies());
			product.setProductType("1");
			/*product.setProductStatus("0");*/
			product.setUserCount(0L);
			product.setInsertTime(new Date());
			product.setProgress(0d);
			product.setActivity("新手特权");
			product.setActivityColor("#f4583f");
			product.setBaseEarnings(product.getAnnualEarnings());
			product.setIsJiangLi(QwyUtil.isNullAndEmpty(product.getIsJiangLi())?"0":product.getIsJiangLi());
			product.setiStatus(0);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return dao.save(product);
	}
}

package com.huoq.product.bean;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.DESEncrypt;
import com.huoq.orm.ProductApply;
import com.huoq.product.dao.ProductApplyDao;

/**
 * 
 * @author bym
 *
 */
@Service
public class ProductApplyBean {
	private static Logger log = Logger.getLogger(ProductApplyBean.class);

	@Resource
	private ProductApplyDao dao;

	public void doProductApply(ProductApply productApply) {
		productApply.setContractName(DESEncrypt.jiaMiUsername(productApply.getContractName().toLowerCase()));
		productApply.setPhone(DESEncrypt.jiaMiUsername(productApply.getPhone().toLowerCase()));
		dao.saveOrUpdate(productApply);
	}
}

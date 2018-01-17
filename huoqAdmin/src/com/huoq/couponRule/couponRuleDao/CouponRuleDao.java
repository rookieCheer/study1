package com.huoq.couponRule.couponRuleDao;

import com.huoq.common.dao.ObjectDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CouponRuleDao extends ObjectDAO {

	private static final Log log = LogFactory.getLog(ObjectDAO.class);

	//property constants
    @Resource(name="sessionFactory")    //sessionFactory的注解
    public void setSuperSessionFactory(SessionFactory sessionFactory){
        super.setSessionFactory(sessionFactory);
    }
	
	/**
	 * 根据hql语句来查询总数
	 * 
	 * @param hql
	 *            hql语句
	 * @param ob
	 * @return
	 */
	public Double findSum(String hql, final Object[] ob) {

		log.debug("findCountByHql");
		final String findMemberaAdvCou = "SELECT count(*) " + hql;
		try {
			List list = null;
			HibernateCallback selectCallback = new HibernateCallback() {
				public Object doInHibernate(Session session) {
					Query query = session.createQuery(findMemberaAdvCou);
					if (ob != null) {
						for (int i = 0; i < ob.length; i++)
							query.setParameter(i, ob[i]);
					}
					return query.list();
				}
			};
			list = getHibernateTemplate().executeFind(selectCallback);
			Double count = 0.0;
			if (list != null && list.size() > 0 && list.get(0) != null)
				count = (double) ((Number) list.get(0)).intValue();
			return count;
		} catch (DataAccessException e) {
			log.error("查询总数失败", e);
			return -1.0;
		}

	}
}

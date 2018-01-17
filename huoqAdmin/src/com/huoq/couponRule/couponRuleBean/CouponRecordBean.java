package com.huoq.couponRule.couponRuleBean;

import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
import com.huoq.couponRule.couponRuleDao.CouponRuleDao;
import com.huoq.orm.CouponReleaseRecord;
import com.huoq.orm.Users;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 券包规则项的记录
 * 
 * @author 权乐乐
 *
 */
@Service
public class CouponRecordBean {

	@Resource
	private CouponRuleDao couponRuleDao;
	
	/**
	 * 添加规则项记录
	 */
	public void saveCouponRecord(CouponReleaseRecord couponRecord){
		couponRuleDao.save(couponRecord);
	}
	
	/**
	 * 查询规则项记录总金额
	 * 
	 * @return
	 */
	public Double findCouponRecordSum(Long id){
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer buff = new StringBuffer();
		buff.append("select sum(crr.coupon.initMoney) from CouponReleaseRecordSales crr");
		buff.append(" where crr.toUser.id = ?");
		buff.append(" and crr.fromUser.username IS NOT NULL and crr.coupon.status='2'");
		buff.append(" and crr.couponReleaseRuleItem.rule.id = 4");
		return (Double) couponRuleDao.findUniqueResult(buff.toString(),new Object[]{id});
	}
	
	/**
	 * 查询不同好友的规则项记录金额
	 * 
	 * @return
	 */
	public List friendItemsSumCost(Long id,Users user){
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer buff = new StringBuffer();
		buff.append("select sum(crr.coupon.initMoney), crr.fromUser.username, crr.fromUser.id, DATE_FORMAT(crr.fromUser.insertTime,'%Y-%m-%d') from CouponReleaseRecordSales crr");
		buff.append(" where crr.toUser.id = ?");
		buff.append(" and crr.fromUser.id = ?");
		buff.append(" and crr.coupon.status = '2'");
		buff.append(" and crr.couponReleaseRuleItem.rule.id = 4");
		buff.append(" group by crr.fromUser.username");
		List<Object> list = couponRuleDao.LoadAll(buff.toString(),new Object[]{id,user.getId()});
		List<Map<String, Object>> lists = new ArrayList<>();
		if (!QwyUtil.isNullAndEmpty(list) && list.size() != 0){
			Object[] objArr = list.toArray();
			for (int i = 0; i < objArr.length;i++){
				Object o = objArr[i];
				Map<String, Object> map = new HashMap<String, Object>();
				JSONArray jsonArr = JSONArray.fromObject(o);
				Double initMoney = (Double)jsonArr.get(0);
				String userName = DESEncrypt.jieMiUsername(jsonArr.get(1).toString());
				String fromUserid = jsonArr.get(2).toString();
				String date = null;
				if(!QwyUtil.isNullAndEmpty(jsonArr.get(3))){
					date = jsonArr.get(3).toString();
				}
				map.put("initMoney", initMoney);
				map.put("userName", userName);
				map.put("fromUserid", fromUserid);
				map.put("date", date);
				lists.add(map);
			}
		}
		return lists;
	}
	
}

package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.UsersVIPBeatDao;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Users;
import com.huoq.orm.UsersVIPBeat;

/**
 * VIP内测用户
 * @author admin
 *
 */
@Service
public class UsersVIPBeatBean {
	
	private static Logger log = Logger.getLogger(UsersVIPBeatBean.class);

	@Resource
	UsersVIPBeatDao dao;
	
	/**
	 * 分页获取 VIP内测用户对象
	 * @param pageUtil  分页对象
	 * @param username 用户名 未加密的 内部加密
	 * @param status 状态默认传值 0
	 * @param rate 加息
	 * @return
	 */
	public PageUtil<UsersVIPBeat> getUsersVIPList(PageUtil<UsersVIPBeat> pageUtil,String username,String status,Double rate,String insertTime){
		StringBuffer hql = new StringBuffer();
		List<Object> ob = new ArrayList<Object>();
		try {
			hql.append(" FROM UsersVIPBeat uv WHERE 1=1");
			if (!QwyUtil.isNullAndEmpty(username)) {
				hql.append(" AND uv.username = ?");
				ob.add(DESEncrypt.jiaMiUsername(username));
			}
			
			if (!QwyUtil.isNullAndEmpty(status)) {
				hql.append(" AND uv.status = ?");
				ob.add(status);
			}
			
			if (!QwyUtil.isNullAndEmpty(rate)) {
				hql.append(" AND uv.addInterestRates = ?");
				ob.add(rate);
			}
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					hql.append(" AND uv.insertTime >= ? ");
					ob.add(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
					hql.append(" AND uv.insertTime <= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
				} else {
					hql.append(" AND uv.insertTime >= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND uv.insertTime <= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}
			}
			
			return dao.getPage(pageUtil, hql.toString(), ob.toArray());
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	/**
	 * 添加VIP内测用户
	 * @param username 页面传的值 未加密 用户名
	 * @return 返回添加成功后的ID
	 */
	public String addUsersVip(String username) {
		if (QwyUtil.isNullAndEmpty(username)) {
			return "";
		}
		Users users = (Users) dao.findJoinActive(" FROM Users WHERE username = ?", new Object[]{DESEncrypt.jiaMiUsername(username)});
		if (QwyUtil.isNullAndEmpty(users)) {
			return "";
		}
		UsersVIPBeat usersVIPBeat = new UsersVIPBeat();
		usersVIPBeat.setUsersId(users.getId());
		usersVIPBeat.setUsername(DESEncrypt.jiaMiUsername(username));
		usersVIPBeat.setStatus("0"); //默认为0
		usersVIPBeat.setVip(0); //vip等级;0开始;0代表不是vip用户',
		usersVIPBeat.setNote("内测用户");
		usersVIPBeat.setAddInterestRates(0D);
		usersVIPBeat.setInsertTime(new Date());
		usersVIPBeat.setUpdateTime(usersVIPBeat.getInsertTime());
		
		return dao.save(usersVIPBeat);
	}
	
	/**
	 * 修改VIP内测用户信息
	 * @param id
	 * @param note
	 * @param vip
	 * @param rate
	 * @return
	 */
	public boolean modifyUsersVip(String id,String note,Integer vip,Double rate){
		if (QwyUtil.isNullAndEmpty(id)) {
			return false;
		}
		UsersVIPBeat usersVIPBeat = (UsersVIPBeat) dao.findById(new UsersVIPBeat(), id);
		if (QwyUtil.isNullAndEmpty(usersVIPBeat)) {
			return false;
		}
		if (!QwyUtil.isNullAndEmpty(note)) {
			usersVIPBeat.setNote(note);
		}
		if (!QwyUtil.isNullAndEmpty(vip)) {
			usersVIPBeat.setVip(vip);
		}
		if (!QwyUtil.isNullAndEmpty(rate)) {
			usersVIPBeat.setAddInterestRates(rate);
		}
		usersVIPBeat.setUpdateTime(new Date());
		dao.update(usersVIPBeat);
		return true;
	}
	
	/**
	 * 根据用户名查找是否存在该VIP用户
	 * @param username
	 * @return
	 */
	public UsersVIPBeat findByUsername(String username) {
		
		return (UsersVIPBeat) dao.findJoinActive("FROM UsersVIPBeat WHERE username = ?", new Object[]{DESEncrypt.jiaMiUsername(username)});
		
	}
	

	/**
	 * 根据ID修改状态
	 */
	public boolean updateStatusById(String id) {
		UsersVIPBeat usersVIPBeat = (UsersVIPBeat) dao.findById(new UsersVIPBeat(), id);
		if (!QwyUtil.isNullAndEmpty(usersVIPBeat)) {
			if (!QwyUtil.isNullAndEmpty(usersVIPBeat.getStatus()) && usersVIPBeat.getStatus().equals("0")) {
				usersVIPBeat.setStatus("1");
			} else {
				usersVIPBeat.setStatus("0");
			}
		}
		dao.saveOrUpdate(usersVIPBeat);
		return true;
	}

	
}

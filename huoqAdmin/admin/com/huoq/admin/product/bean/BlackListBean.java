package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.LockHolder;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.BlackList;
import com.huoq.orm.Users;

/**
 * 黑名单记录
 * Created by yks on 2016/10/13.
 */
@Service
public class BlackListBean {

    @Resource(name = "objectDAO")
    private ObjectDAO dao;
    private Logger log = Logger.getLogger(BlackListBean.class);


    /**
     * 显示黑名单记录列表
     *
     * @param pageUtil   分页对象
     * @param username   黑名单姓名
     * @param status     状态 0 使用中 1 已解除
     * @param insertTime
     * @return 黑名单记录分页对象
     */
    public PageUtil<BlackList> loadBlackListRecords(PageUtil pageUtil, String username,
                                                    String status, String insertTime) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            StringBuilder hql = new StringBuilder();
            hql.append("FROM BlackList bl WHERE 1=1 ");
            //状态
            if (!"all".equals(status)) {
                hql.append(" AND bl.status = ? ");
                params.add(status);
            }
            //黑名单
            if (!QwyUtil.isNullAndEmpty(username)) {
                hql.append(" AND bl.username = ? ");
                //params.add(DESEncrypt.jiaMiUsername(username));
                params.add(username);
            }
            //按创建时间查询
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] times = QwyUtil.splitTime(insertTime);
                if (times.length == 1) {
                    hql.append("AND bl.updateTime >= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                    hql.append("AND bl.updateTime <= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
                } else {
                    hql.append("AND bl.updateTime >= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                    hql.append("AND bl.updateTime <= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1] + " 23:59:59"));
                }
            }
            hql.append("ORDER BY bl.updateTime DESC, bl.id ASC");
            pageUtil = dao.getPage(pageUtil, hql.toString(), params.toArray());
        } catch (Exception e) {
            log.error("操作失败", e.getCause());
            return null;
        }
        return pageUtil;
    }


    /**
     * update 黑名单状态
     *
     * @param blackListId
     * @param status
     * @throws Exception
     */
    public void updateBlackListStatus(String blackListId, String status) throws Exception {
        BlackList blackList = (BlackList) dao.findById(new BlackList(), Long.parseLong(blackListId));
        if (!QwyUtil.isNullAndEmpty(blackList)) {
            blackList.setStatus(status);
            dao.saveOrUpdate(blackList);
        }
    }
    /**
     * 添加描述
     */
    public void updateDescription(String blackListId, String description)throws Exception{
    	StringBuilder hql = new StringBuilder();
        hql.append(" UPDATE black_list SET description ='"+ description+"'");
        hql.append(" ,update_time=NOW() ");
        hql.append(" WHERE id= " + blackListId);
        dao.updateBySql(hql.toString(), null);
    }

    /**
     * 将手机号添至黑名单
     *
     * @param blackList
     * @return
     */
    public void addPhones2BlackList(BlackList blackList) {
        try {
            List<BlackList> lists = getBlackListByUsername(blackList.getUsername());
            BlackList black = new BlackList();
            if (lists != null && lists.size() > 0) {
                black = lists.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(black.getNote());
                sb.append(";");
                sb.append(blackList.getNote());
                black.setNote(sb.toString());
                black.setStatus("0");
                dao.update(black); //更新黑名单
            } else {
                black.setUsername(blackList.getUsername());
                black.setNote(blackList.getNote());
                black.setImei(blackList.getImei());
                black.setIp(blackList.getIp());
                black.setInsertTime(new Date());
                black.setStatus("0");//状态：0  拉黑   1 解绑
                dao.saveAndReturnId(black); //添加手机号至黑名单
            }
        } catch (Exception e) {
            log.error("添加黑名单出错:", e);
            log.error("操作异常: ",e);
        }
    }
    
    /**添加黑名单
	 * @param username 用户名
	 * @param ip ip地址
	 * @param note 备注
	 * @param imei 手机IMEI值
	 */
	public void saveBlackList(String username,String ip,String note,String imei){
		synchronized (LockHolder.getLock(username)) {
			BlackList bl = getBlackListByUsernameByOne(username);
			if (QwyUtil.isNullAndEmpty(bl)) {
				//黑名单不存在;新建
				bl = new BlackList();
				bl.setInsertTime(new Date());
				bl.setUsername(username);
				bl.setUpdateTime(bl.getInsertTime());
				bl.setNote(note);
				bl.setStatus("0");
				bl.setIp(ip);
				bl.setImei(imei);
				dao.save(bl);
			} else {
				BlackList blOld = getBlackListByUsernameByOne(username);
				blOld.setNote(blOld.getNote() +  note);
				blOld.setUpdateTime(new Date());
				blOld.setStatus("0");
				blOld.setIp(ip);
				blOld.setImei(imei);
				dao.saveOrUpdate(blOld);
			}
		}
		
	}

    /**
     * 通过手机号查询黑名单
     *
     * @param username
     * @return
     * @throws Exception
     */
    private List<BlackList> getBlackListByUsername(String username) throws Exception {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM BlackList bl WHERE 1=1 ");
        hql.append(" AND bl.username = " + username);
        hql.append(" ORDER BY bl.insertTime DESC ");
        return dao.LoadAll(hql.toString(), null);
    }
    
    /**
     * 通过手机号查询状态为0的黑名单
     *
     * @param username
     * @return
     * @throws Exception
     */
    public List<BlackList> getBlackListByUsernameAdnStatus(String username) throws Exception {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM BlackList bl WHERE 1=1 AND bl.status=0 ");
        hql.append(" AND bl.username = " + username);
        hql.append(" ORDER BY bl.insertTime DESC ");
        return dao.LoadAll(hql.toString(), null);
    }
    

    /**
     * 获取当前条件下黑名单记录(用于报表查询)
     *
     * @param pageUtil
     * @param username
     * @param status
     * @param insertTime
     * @return
     * @throws Exception
     */
    public List<BlackList> exportBlackListExport(PageUtil pageUtil, String username,
                                                 String status, String insertTime) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT bl.id, bl.username, bl.ip, bl.note,bl.insert_time, bl.imei, bl.STATUS ");
        sql.append(" FROM black_list AS bl WHERE 1=1 ");
        //状态
        if (!"all".equals(status)) {
            sql.append(" AND bl.STATUS = ? ");
            params.add(status);
        }
        //黑名单手机号码
        if (!QwyUtil.isNullAndEmpty(username)) {
            sql.append(" AND bl.username = ? ");
            params.add(username);
            //params.add(DESEncrypt.jiaMiUsername(username));
        }
        //按创建时间查询
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] times = QwyUtil.splitTime(insertTime);
            if (times.length == 1) {
                sql.append("AND bl.insert_time >= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                sql.append("AND bl.insert_time <= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
            } else {
                sql.append("AND bl.insert_time >= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                sql.append("AND bl.insert_time <= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1] + " 23:59:59"));
            }
        }
        sql.append(" ORDER BY bl.insert_time DESC ,bl.id ASC ");
        StringBuilder sqlCount = new StringBuilder();
        sqlCount.append(" SELECT COUNT(*)  ");
        sqlCount.append(" FROM (");
        sqlCount.append(sql);
        sqlCount.append(") t");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params.toArray());
        List<BlackList> blackListList = toBlackList(pageUtil.getList());
        return blackListList;

    }

    /**
     * @param objects
     * @return
     * @throws Exception
     */
    private List<BlackList> toBlackList(List<Object[]> objects) throws Exception {
        List<BlackList> list = new ArrayList<>();
        if (!QwyUtil.isNullAndEmpty(objects)) {
            for (int i = 0; i < objects.size(); i++) {
                Object[] object = objects.get(i);
                BlackList bl = new BlackList();
                bl.setId(Long.parseLong(object[0].toString()));
                bl.setUsername(object[1].toString());
                bl.setIp(object[2].toString());
                bl.setNote(object[3].toString());
                bl.setInsertTime(!QwyUtil.isNullAndEmpty(object[4]) ? QwyUtil.fmyyyyMMddHHmmss.parse(object[4].toString()) : null);
                bl.setImei(!QwyUtil.isNullAndEmpty(object[5]) ? object[5].toString() : null);
                if ("0".equals(object[6].toString())) {
                    bl.setStatus("已拉黑");
                } else {
                    bl.setStatus("已解除");
                }
                list.add(bl);
            }
        }
        return list;
    }


    /**
     * 根据userId查找用户
     * @param userId
     * @return
     */
    public Users findUsersById(Long userId){
        Users user = (Users) dao.findById(new Users(), userId);
        return user;
    }
    
    /**
   	 * 根据ID修改黑名单状态
   	 */
   	public boolean updateStatusById(Long id) {
   		BlackList blackList = (BlackList) dao.findById(new BlackList(), id);
   		if (!QwyUtil.isNullAndEmpty(blackList)) {
   			if (!QwyUtil.isNullAndEmpty(blackList.getStatus()) && blackList.getStatus().equals("0")) {
   				blackList.setStatus("1");
   			} else {
   				blackList.setStatus("1");
   			}
   			blackList.setUpdateTime(new Date());
   		}
   		dao.saveOrUpdate(blackList);
   		return true;
   	}

   	/**查找黑名单--
	 * @param username 用户名
	 * @return 黑名单
	 */
	public BlackList getBlackListByUsernameByOne(String username){
		StringBuffer sb = new StringBuffer();
		sb.append("FROM BlackList bl WHERE bl.username = ? ");
		return (BlackList)dao.findJoinActive(sb.toString(), new Object[]{username});
	}
}

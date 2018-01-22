package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.UserInfoBean;
import com.huoq.admin.product.dao.RankingDao;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Rank;
import com.huoq.orm.Region;

/**
 * 金额排行
 *
 * @author 覃文勇
 * @createTime 2015-7-30下午4:56:58
 */
@Service
public class RankingBean {
    private static Logger log = Logger.getLogger(RankingBean.class);
    @Resource
    RankingDao dao;

    /*
     * 投入总金额排行榜
     */
    @SuppressWarnings("unchecked")
    public PageUtil<Rank> loadInvestorRank(PageUtil pageUtil, String insertTime) {
        StringBuffer hql = new StringBuffer();
        List<Object> rankList = new ArrayList<Object>();
        try {
            hql.append("SELECT i.users_id,SUM(in_money) AS in_money,username,real_name " +
                    "FROM investors i,users u,users_info ui WHERE u.id=i.users_id AND i.users_id=ui.users_id " +
                    "AND i.investor_status IN (1,2,3) ");

            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    hql.append(" AND i.insert_time >= ? ");
                    rankList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    hql.append(" AND i.insert_time <= ? ");
                    rankList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    hql.append(" AND i.insert_time >= ? ");
                    rankList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    hql.append(" AND i.insert_time <= ? ");
                    rankList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            hql.append("GROUP BY i.users_id ORDER BY in_money DESC  ");
            StringBuffer buffer = new StringBuffer();
            buffer.append("  SELECT COUNT(t.in_money) FROM (");
            buffer.append(hql);
            buffer.append(") t ");
            PageUtil bySqlAndSqlCount = dao.getBySqlAndSqlCount(pageUtil, hql.toString(), buffer.toString(), rankList.toArray());
            List<Object[]> objlist = bySqlAndSqlCount.getList();
            List<Rank> ranks = toInvestorRank(objlist);
            bySqlAndSqlCount.setList(ranks);
            return bySqlAndSqlCount;

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    private List<Rank> toInvestorRank(List<Object[]> objlist) {
        List<Rank> rankList = new ArrayList<>();
        try {
            for (Object[] objects : objlist) {
                Rank rank = new Rank();
                rank.setUsersId(objects[0] + "");
                rank.setInmoney(objects[1] + "");//投资总金额
                if (!QwyUtil.isNullAndEmpty(objects[2])) {
                    rank.setUsersname((String) objects[2]);
                }
                if (!QwyUtil.isNullAndEmpty(objects[3])) {
                    rank.setRealname((String) objects[3]);
                }
                rankList.add(rank);
            }
            return rankList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}

        /*
         * 充值总金额排行榜
         */
        @SuppressWarnings("unchecked")
        public PageUtil<Rank> loadCZRecordRank (PageUtil pageUtil){
            try {
                List<Rank> rankList = new ArrayList<Rank>();
                String hql = "SELECT c.users_id,SUM(money) 'money',username,real_name FROM cz_record c,users u,users_info ui WHERE u.id=c.users_id AND c.users_id=ui.users_id AND STATUS IN (1) GROUP BY users_id ORDER BY money DESC";
                StringBuffer buffer = new StringBuffer();
                buffer.append("  SELECT COUNT(t.money) FROM (");
                buffer.append(hql);
                buffer.append(") t ");
                PageUtil<Object[]> page = dao.getBySqlAndSqlCount(pageUtil, hql, buffer.toString(), null);
                for (int i = 0; i < page.getList().size(); i++) {
                    Object[] objects = page.getList().get(i);
                    Rank rank = new Rank();
                    rank.setUsersId(objects[0] + "");
                    rank.setMoney(objects[1] + "");//充值总金额
                    if (!QwyUtil.isNullAndEmpty(objects[2])) {
                        rank.setUsersname((String) objects[2]);
                    }
                    if (!QwyUtil.isNullAndEmpty(objects[3])) {
                        rank.setRealname((String) objects[3]);
                    }
                    rankList.add(rank);
                }
                pageUtil.setList(rankList);
                return pageUtil;
            } catch (Exception e) {
                log.error("操作异常: ", e);
            }
            return null;
        }

    }

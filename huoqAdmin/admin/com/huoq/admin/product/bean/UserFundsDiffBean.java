package com.huoq.admin.product.bean;

import com.huoq.account.bean.MyAccountBean;
import com.huoq.admin.product.dao.UserFundsDiffDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.FreezeMoneyUsersInfo;
import com.huoq.orm.Investors;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户账户偏差
 * Created by yks on 2016/12/29.
 */
@Service
public class UserFundsDiffBean {
    private static Logger log = Logger.getLogger(UserFundsDiffBean.class);

    @Resource
    private UserFundsDiffDAO userFundsDiffDAO;

    @Resource
    private MyAccountBean myAccountBean;


    /**
     * 取得偏差有异常的用户集合
     *
     * @return
     */
    public List<Investors> getErroDiffInvestors(int currentPage, int pageSize) throws Exception {
        List<Investors> list1 = new ArrayList<>();
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM Investors inv WHERE inv.investorStatus IN ('1','2','3') ");
        hql.append(" GROUP BY inv.usersId ");
        list1 = userFundsDiffDAO.LoadAll(hql.toString(),null);
        //list1 = dao.findAdvList(hql.toString(), null, currentPage, pageSize);
        List<Investors> list2 = new ArrayList<>();
        if (!QwyUtil.isNullAndEmpty(list1)) {
            for (Investors inv : list1) {
                Double result = Double.parseDouble(QwyUtil.isNullAndEmpty(myAccountBean.getDiff(inv.getUsersId())
                        .toString()) ? "0" : myAccountBean.getDiff(inv.getUsersId()).toString());
                if (result < 100.0 && result >= 0) {
                    continue;
                }
                inv.setTzdiff(result);
                list2.add(inv);
            }
        }
        return getPageList1(list2,pageSize,currentPage);
    }


    /**
     * 实现数据集合的自分页
     * @param list 分页集合
     * @param pagesize 每页记录数量
     * @return
     */
    public List<Investors> getPageList1(List<Investors> list,int pagesize,int currentPage) {
        List<Investors> pageList = null;
        int totalcount = list.size();
        int pagecount = 0;
        int m = totalcount % pagesize;
        if (m > 0) {
            pagecount = totalcount / pagesize + 1;
        } else {
            pagecount = totalcount / pagesize;
        }
        if (currentPage > pagecount){
            return pageList;
        }
        for (int i = 1; i <= pagecount; i++) {
            if (m == 0) {
                pageList = list.subList((currentPage - 1) * pagesize, pagesize * (currentPage));
            } else {
                if (currentPage == pagecount) {
                    pageList = list.subList((currentPage - 1) * pagesize, totalcount);
                } else {
                    pageList = list.subList((currentPage - 1) * pagesize, pagesize * (currentPage));
                }
            }
        }
        return pageList;
    }



    /**
     * 取得偏差有异常的用户集合
     *
     * @return
     */
    public List<FreezeMoneyUsersInfo> getFreezeMoneyUsersInfo(int currentPage, int pageSize) throws Exception {
        List<FreezeMoneyUsersInfo> fmUsersList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT users_id,phone,freeze_money,product_money,product_money - freeze_money,freeze_money=product_money,real_name FROM ( " +
                " SELECT uni.users_id,uni.real_name,phone,uni.freeze_money,(SELECT if(isnull(SUM(in_money)),0,SUM(in_money)) FROM investors inv WHERE " +
                " inv.users_id  = uni.users_id AND inv.investor_status IN ('1','2'))'product_money' FROM users_info uni ) tp " +
                " WHERE (freeze_money=product_money) = 0 ");
        List<Object[]> list = userFundsDiffDAO.LoadAllSql(sql.toString(),null);
        for (Object[] obj : list){
            FreezeMoneyUsersInfo fmui = new FreezeMoneyUsersInfo();
            fmui.setUsersId(!QwyUtil.isNullAndEmpty(obj[0])?obj[0].toString():"");
            fmui.setPhone(!QwyUtil.isNullAndEmpty(obj[1])? DESEncrypt.jieMiUsername(obj[1].toString()):"");
            fmui.setFreezeMoney(!QwyUtil.isNullAndEmpty(obj[2])?obj[2].toString():"0");
            fmui.setProductMoney(!QwyUtil.isNullAndEmpty(obj[3])?obj[3].toString():"0");
            fmui.setDiff(!QwyUtil.isNullAndEmpty(obj[4])?obj[4].toString():"0");
            fmui.setIsDiff(!QwyUtil.isNullAndEmpty(obj[5])?obj[5].toString():"");
            fmui.setRealName(!QwyUtil.isNullAndEmpty(obj[6])?obj[6].toString():"");
            fmUsersList.add(fmui);
        }
        if (!QwyUtil.isNullAndEmpty(fmUsersList)){
            return getPageList(fmUsersList,pageSize,currentPage);
        }
        return fmUsersList;
    }


    /**
     * 实现数据集合的自分页
     * @param list 分页集合
     * @param pagesize 每页记录数量
     * @return
     */
    public List<FreezeMoneyUsersInfo> getPageList(List<FreezeMoneyUsersInfo> list,int pagesize,int currentPage) {
        List<FreezeMoneyUsersInfo> pageList = null;
        int totalcount = list.size();
        int pagecount = 0;
        int m = totalcount % pagesize;
        if (m > 0) {
            pagecount = totalcount / pagesize + 1;
        } else {
            pagecount = totalcount / pagesize;
        }
        if (currentPage > pagecount){
            return pageList;
        }
        for (int i = 1; i <= pagecount; i++) {
            if (m == 0) {
                pageList = list.subList((currentPage - 1) * pagesize, pagesize * (currentPage));
            } else {
                if (currentPage == pagecount) {
                    pageList = list.subList((currentPage - 1) * pagesize, totalcount);
                } else {
                    pageList = list.subList((currentPage - 1) * pagesize, pagesize * (currentPage));
                }
            }
        }
        return pageList;
    }


}

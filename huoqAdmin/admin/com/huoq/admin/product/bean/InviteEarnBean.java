package com.huoq.admin.product.bean;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InviteEarn;
import com.huoq.orm.InviteEarnExport;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 邀请投资奖励记录
 * Created by Administrator on 2016/10/10.
 */
@Service
public class InviteEarnBean {

    @Resource(name = "objectDAO")
    private ObjectDAO dao;
    private Logger log = Logger.getLogger(InviteEarnBean.class);


    /**
     * @param pageUtil   分页对象
     * @param inviter    邀请人姓名
     * @param inviteId   邀请人id
     * @param status     (状态  0：未发放 1：已发放 )
     * @param insertTime 创建时间
     * @return 邀请投资奖励记录分页对象
     */
    public PageUtil<InviteEarn> loadInviteEarnRecords(PageUtil pageUtil, String inviter, String inviteId,
                                                      String status, String insertTime) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            StringBuilder hql = new StringBuilder();
            hql.append("FROM InviteEarn ie WHERE 1=1 ");
            //发放状态
            if (!"all".equals(status)) {
                hql.append(" AND ie.status = ? ");
                params.add(status);
            }
            //邀请人姓名id
            if (!QwyUtil.isNullAndEmpty(inviteId)) {
                hql.append(" AND ie.inviteId = ? ");
                params.add(Long.parseLong(inviteId));
            }
            //邀请人姓名
            if (!QwyUtil.isNullAndEmpty(inviter)) {
                hql.append(" AND ie.users.username = ? ");
                params.add(DESEncrypt.jiaMiUsername(inviter));
            }
            //按插入时间查询
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] times = QwyUtil.splitTime(insertTime);
                if (times.length == 1) {
                    hql.append("AND ie.insertTime >= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                    hql.append("AND ie.insertTime <= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
                }else {
                    hql.append("AND ie.insertTime >= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                    hql.append("AND ie.insertTime <= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1] + " 23:59:59"));
                }
            }
            hql.append("ORDER BY ie.insertTime DESC, ie.id ASC");
            //pageUtil = dao.getByHqlAndHqlCount(pageUtil,hql.toString(),hql.toString(),params.toArray());
            pageUtil = dao.getPage(pageUtil, hql.toString(), params.toArray());
        } catch (Exception e) {
            log.error("操作失败", e.getCause());
            return null;
        }
        return pageUtil;
    }


    /**
     * 导出邀请投资奖励记录报表
     * @param inviter 邀请人姓名
     * @param insertTime
     * @param inviteId 邀请人id
     * @param status 状态 0 未发放 1 已发放
     * @param pageUtil
     * @param sourceFileName  文件地址
     * @return
     * @throws Exception
     */
    public List<JasperPrint> getInviteEarnJasperPrintList(String inviter, String insertTime, String inviteId,
                                                      String status, PageUtil pageUtil, String sourceFileName) throws Exception {
        List<JasperPrint> list = new ArrayList<JasperPrint>();
        List<InviteEarnExport> exportList = exportInviteEarnExport(pageUtil,inviter,inviteId,status,insertTime);
        try{
            if (!QwyUtil.isNullAndEmpty(exportList)) {
                Map<String, String> map = QwyUtil.getValueMap(exportList);
                JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(exportList);
                JasperPrint js = JasperFillManager.fillReport(sourceFileName, map, ds);
                list.add(js);
            }
        }catch (Exception e){
            log.error("操作异常: ",e);
        }
        return list;
    }

    /**
     * 获取当前条件下邀请投资奖励记录(用于报表查询)
     * @param pageUtil
     * @param inviter
     * @param inviteId
     * @param status
     * @param insertTime
     * @return
     * @throws Exception
     */
    public List<InviteEarnExport> exportInviteEarnExport(PageUtil pageUtil, String inviter, String inviteId,
                                                          String status, String insertTime) throws Exception {

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ie.id, ie.be_invited_id, ie.be_invite_phone, ie.copies, ie.earn_money, ie.type, ");
        sql.append(" ie.insert_time, ie.investors_id, ie.invite_id, ie.note, ie.STATUS, ie.return_time, u.username ");
        sql.append(" FROM invite_earn AS ie,users u WHERE 1=1 AND ie.invite_id = u.id ");
        //发放状态
        if (!"all".equals(status)) {
            sql.append(" AND ie.STATUS = ? ");
            params.add(status);
        }
        //邀请人姓名id
        if (!QwyUtil.isNullAndEmpty(inviteId)) {
            sql.append(" AND ie.invite_id = ? ");
            params.add(Long.parseLong(inviteId));
        }
        //邀请人姓名
        if (!QwyUtil.isNullAndEmpty(inviter)) {
            sql.append(" AND u.username = ? ");
            params.add(DESEncrypt.jiaMiUsername(inviter));
        }
        //按插入时间查询
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] times = QwyUtil.splitTime(insertTime);
            if (times.length == 1) {
                sql.append("AND ie.insert_time >= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                sql.append("AND ie.insert_time <= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
            }else {
                sql.append("AND ie.insert_time >= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                sql.append("AND ie.insert_time <= ?");
                params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1] + " 23:59:59"));
            }
        }
        sql.append(" ORDER BY ie.insert_time DESC ,ie.id ASC ");
        StringBuilder sqlCount = new StringBuilder();
        sqlCount.append(" SELECT COUNT(*)  ");
        sqlCount.append(" FROM (");
        sqlCount.append(sql);
        sqlCount.append(") t");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params.toArray());
        List<InviteEarnExport> inviteEarnList = toInviteEarnList(pageUtil.getList());
        return inviteEarnList;

    }

    /**
     * @param objects
     * @return
     * @throws Exception
     */
    private List<InviteEarnExport> toInviteEarnList(List<Object[]> objects) throws Exception {
        List<InviteEarnExport> list = new ArrayList<>();
        if (!QwyUtil.isNullAndEmpty(objects)) {
            for (int i = 0; i < objects.size(); i++) {
                Object[] object = objects.get(i);
                InviteEarnExport iee = new InviteEarnExport();
                iee.setId(object[0].toString());
                iee.setBeInvitedId(object[1].toString());
                iee.setBeInvitePhone(!QwyUtil.isNullAndEmpty(object[2]) ? DESEncrypt.jieMiUsername(object[2].toString()) : "");
                iee.setCopies(object[3].toString());
                iee.setEarnMoney(!QwyUtil.isNullAndEmpty(QwyUtil.calcNumber(object[4], "100", "/", 2))
                        ? QwyUtil.calcNumber(object[4], "100", "/", 2) + "" : "0");
                if("1".equals(object[5].toString())){
                    iee.setType("邀请好友");
                }else {
                    iee.setType("");
                }
                iee.setInsertTime(!QwyUtil.isNullAndEmpty(object[6]) ? QwyUtil.fmyyyyMMddHHmmss.
                        format(QwyUtil.fmyyyyMMddHHmmss.parse(object[6].toString())) : "");
                iee.setInvestorsId(object[7].toString());
                iee.setInviteId(object[8].toString());
                iee.setNote(object[9].toString());
                if ("0".equals(object[10].toString())) {
                    iee.setStatus("未发放");
                } else if ("1".equals(object[10].toString())) {
                    iee.setStatus("已发放");
                }
                iee.setReturnTime(!QwyUtil.isNullAndEmpty(object[11]) ? QwyUtil.fmyyyyMMddHHmmss.
                        format(QwyUtil.fmyyyyMMddHHmmss.parse(object[11].toString())) : "");
                iee.setInviter(!QwyUtil.isNullAndEmpty(object[12]) ? DESEncrypt.jieMiUsername(object[12].toString()) : "");
                list.add(iee);
            }
        }
        return list;
    }


}

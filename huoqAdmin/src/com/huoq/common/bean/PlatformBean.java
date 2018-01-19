package com.huoq.common.bean;

import com.huoq.admin.product.bean.InvestorsBean;
import com.huoq.common.dao.MyWalletDAO;
import com.huoq.common.util.ArrayUtils;
import com.huoq.common.util.DateUtils;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Platform;
import com.huoq.product.bean.ProductBean;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.huoq.admin.product.bean.VirtualInsRecordBean;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对平台融资情况的一个修改; Bean层;
 * 
 * @author qwy
 * @createTime 2015-5-18下午11:36:35
 */
@Service
public class PlatformBean {

    private static Logger        log = Logger.getLogger(PlatformBean.class);
    @Resource
    private MyWalletDAO          dao;
    /**
     * 注入产品表service
     */
    @Resource
    private ProductBean          productService;
    /**
     * 注入投资表service
     */
    @Resource
    private InvestorsBean        investorsService;

    /**
     * 注入虚拟投资记录表Service
     */
    @Resource
    private VirtualInsRecordBean virtualService;

    /**
     * 获取平台融资情况;
     * 
     * @return
     */
    public Platform getPlatform(String insertTime) {
        Platform plat = (Platform) dao.findById(new Platform(), 1L);
        // 今日充值金额
        Double updateTodayrechargeMoney = updateTodayrechargeMoney(null);
        plat.setTodayrechargeMoney(updateTodayrechargeMoney);
        // 今日存量增量
        Double todayCapitalStock = updateTodayCapitalStock(null);
        plat.setTodayCapitalStock(todayCapitalStock);
        // 累计提现金额
        Double uodateAllOutCashMoney = uodateAllOutCashMoney(null);
        plat.setAllOutCashMoney(uodateAllOutCashMoney);
        // 今日提现金额
        Double uodateTodayOutCashMoney = uodateTodayOutCashMoney(null);
        plat.setTodayOutCashMoney(uodateTodayOutCashMoney);
        // 今日购买金额
        Double updateTodayBuyMoney = updateTodayBuyMoney(null);
        plat.setTodayBuyMoney(updateTodayBuyMoney);
        // 总购买金额(平台交易总额)
        Double updateAllBuyMoney = updateAllBuyMoney();
        plat.setAllBuyMoney(updateAllBuyMoney);

        // 累计充值金额
        Double rechargeMoney = updateRechargeMoney(null);
        plat.setRechargeMoney(rechargeMoney);
        // 更新累计注册用户
        Integer updateRegisterCount = updateRegisterCount(null);
        plat.setRegisterCount(updateRegisterCount);
        // 累计绑定用户
        Integer updateCertificationCount = updateCertificationCount(null);
        plat.setCertificationCount(updateCertificationCount);
        // 今日注册用户
        Integer updateTodayregisterCount = updateTodayregisterCount(null);
        plat.setTodayregisterCount(updateTodayregisterCount);
        // 今日绑定用户
        Integer updateTodaycertificationCount = updateTodaycertificationCount(null);
        plat.setTodaycertificationCount(updateTodaycertificationCount);
        // 今日购买人数
        Integer updateTodayBuyNumber = updateTodayBuyNumber(null);
        plat.setTodayBuyNumber(updateTodayBuyNumber);
        // 今日提现人数
        Integer updateTodayfirstBuyNumber = updateTodayfirstBuyNumber(null);
        plat.setTodayNewBuyNumber(updateTodayfirstBuyNumber);
        // 获取未审核提现金额
        Double updateUncheckedOutCashMoney = updateUAuditingOutCashMoney();
        plat.setUncheckedOutCashMoney(updateUncheckedOutCashMoney);

        // 今日满标企业(家)
        Integer todayFullScaleCompanyNumber = updateTodayFullScaleCompanyNumber();
        plat.setTodayFullScaleCompanyNumber(todayFullScaleCompanyNumber);
        dao.saveOrUpdate(plat);
        plat = null;
        plat = (Platform) dao.findById(new Platform(), 1L);
        if (plat == null) {
            plat = new Platform();
            plat.setId(1L);
            plat.setInsertTime(new Date());
            plat.setTotalMoney(0D);
            plat.setTotalProfit(0D);
            plat.setTotalCoupon(0D);
            plat.setUseCoupon(0D);
            plat.setCollectMoney(0D);
            plat.setRegisterCount(0);
            plat.setFreshmanCoupon(0D);
            dao.saveOrUpdate(plat);
        } else {
            // 实时显示平台资金存量
            // 更新平台资金存量
            String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
            // 获取昨日资金存量
            Double allCapitalStock = updateAllCapitalStock(yesterday);
            if (!QwyUtil.isNullAndEmpty(allCapitalStock)) {
                // 首页资金存量等于昨日资金存量加今日存量增量
                allCapitalStock = todayCapitalStock + allCapitalStock;
                // 设置到资金存量
                plat.setAllCapitalStock(allCapitalStock);
            } else {
                allCapitalStock = 0.0;
                allCapitalStock = todayCapitalStock + allCapitalStock;
                plat.setAllCapitalStock(allCapitalStock);
            }
            plat.setRechargeMoney(rechargeMoney);
        }
        return plat;
    }

    /**
     * 今日满标企业数量
     * 
     * @author：zhuhaojie
     * @time：2018年1月16日 上午9:40:41
     * @version
     * @return
     */
    public Integer updateTodayFullScaleCompanyNumber() {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT pro.id,pro.real_name,sum(pro.all_copies) raiseMoney,sum(inv.in_money)/100 invMoney ").append(" FROM product pro ").append(" JOIN investors inv ON inv.product_id = pro.id ").append(" AND inv.investor_status ='1' ").append(" AND inv.insert_time >=? AND  inv.insert_time <=? ").append(" group by id,real_name");
        Object[] param = new Object[2];
        Date begin = new Date(DateUtils.getStartTime());
        Date end = new Date(DateUtils.getEndTime());
        param[0] = begin;
        param[1] = end;

        List list = investorsService.getInvestorsBySqlSecond(sql.toString(), param);
        if (list != null && list.size() > 0) {
            // 存储公司id
            Set<String> productId = new HashSet<String>();
            for (Object obj : list) {
                if (obj instanceof Object[]) {
                    Object[] array = (Object[]) obj;
                    String id = (String) array[0];// id
                    productId.add(id);
                }
            }
            sql.delete(0, sql.length());

            sql.append(" select product_id,sum(pay_in_mony)/100 virtualMoney from virtual_ins_record ").append(" where  insert_time >=?  AND   insert_time <=? ").append(" and product_id in(:ids) group by product_id ");
            int size = productId.size();
            List<String> productIds = ArrayUtils.converArrayToList(productId.toArray(new String[size]));
            List virualList = virtualService.getBySql(sql.toString(), param, "ids", productIds);
            if (virualList != null && virualList.size() > 0) {
                int sizeOne = list.size();
                for (int i = 0; i < sizeOne; i++) {
                    Object obj = list.get(i);
                    if (obj instanceof Object[]) {
                        Object[] array = (Object[]) obj;
                        // 产品id
                        String id = (String) array[0];
                        // 募集总金额
                        BigDecimal total = new BigDecimal(array[2].toString());
                        double dtotal = total.doubleValue();
                        // 循环虚拟机投资集合
                        for (Object vobj : virualList) {
                            if (vobj instanceof Object[]) {
                                Object[] varray = (Object[]) vobj;
                                String productIdV = (String) varray[0];
                                if (productIdV.equals(id)) {
                                    double virtualMoney = (Double) varray[1];
                                    dtotal = dtotal - virtualMoney;
                                }
                            }
                        }
                        // 循环完成
                        array[2] = dtotal;
                        obj = array;
                        list.set(i, obj);
                    }
                }
            }
            productId.clear();
            for (Object obj : list) {
                if (obj instanceof Object[]) {
                    Object[] array = (Object[]) obj;
                    // 产品id
                    String id = (String) array[0];
                    // 募集总金额
                    double dtotal = (Double) array[2];
                    // 实际总投资金额
                    double inv = (Double) array[3];
                    if (inv > dtotal * 0.9) {
                        productId.add(id);
                    }
                }
            }
            return productId.size();
        }
        return 0;
    }

    /**
     * 今日未审核提现总额(元)
     * 
     * @param insertTime
     */
    public Double updateTodayUAuditingOutCashMoney(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT SUM(tx.money)/100 FROM `tx_record` tx WHERE tx.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')  ");
            sql.append(" AND tx.is_check = 0 ");
            sql.append(" AND status IN (0,2,3) AND tx_status = 0 ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayUAuditingOutCashMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayUAuditingOutCashMoney = Double.valueOf((loadAllSql.get(0) + ""));
            }
            return todayUAuditingOutCashMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 未审核提现总额 提现记录表中 
     * 操作状态为:未操作 
     * 
     * 且 提现状态为: 待审核,提现失败,正在审核 
     * 且 审核状态为: 未审核的 所有记录的money字段的和 转换成元的值
     * 
     * @author：zhuhaojie
     * @time：2018年1月16日 下午2:52:12
     * @version
     * @param insertTime
     * @return
     */
    public Double updateUAuditingOutCashMoney() {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT SUM(tx.money)/100 FROM `tx_record` tx WHERE ");
            sql.append("  tx.is_check = 0 ");
            sql.append(" AND status IN (0,2,3) AND tx_status = 0 ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayUAuditingOutCashMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayUAuditingOutCashMoney = Double.valueOf((loadAllSql.get(0) + ""));
            }
            return todayUAuditingOutCashMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

 

    /**
     * 更新平台今日首投用户数
     * 
     * @param insertTime
     */
    public Integer updateTodayfirstBuyNumber(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT COUNT(1) FROM investors i LEFT JOIN product p ON p.id = i.product_id WHERE i.investor_status = '1' AND i.insert_time  ");
            sql.append("BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59' ) AND p.product_type = 1");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer todayNewBuyNumber = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayNewBuyNumber = Integer.valueOf((loadAllSql.get(0) + ""));
            }
            return todayNewBuyNumber;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新平台资金总存量
     *
     * @param insertTime
     */
    public Double updateAllCapitalStock(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT all_capital_stock FROM data_overview WHERE 1=1 ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                list.add(insertTime);
                list.add(insertTime);
                sql.append("AND insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            }
            if (QwyUtil.isNullAndEmpty(insertTime)) {
                Date date = new Date();
                list.add(date);
                list.add(date);
                sql.append("AND insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            }
            // String yesterday =
            // QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(),
            // -1).getTime());
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double allCapitalStock = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                allCapitalStock = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return allCapitalStock;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 今日平台增量
     */
    public Double updateTodayCapitalStock(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(money),2) FROM(  ");
            sql.append("SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("UNION ALL ");
            sql.append("SELECT SUM(-tr.money/100) money FROM tx_record tr WHERE tr.is_check = '1'  AND tr.status = '1' AND tr.check_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayCapitalStock = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayCapitalStock = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayCapitalStock;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 今日充值金额
     */
    public Double updateTodayrechargeMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' ");
            sql.append("AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayrechargeMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayrechargeMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayrechargeMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 今日购买人数
     */
    public Integer updateTodayBuyNumber(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();

            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(1) FROM ( " + "SELECT SUM(t.number) t FROM ( "
                       + "SELECT COUNT(i.id) number,i.users_id userid FROM investors i WHERE i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND i.investor_status IN('1','2','3') GROUP BY users_id "
                       + "UNION ALL "
                       + "SELECT COUNT(cpfr.id) number,cpfr.users_id userid   FROM coin_purse_funds_record  cpfr WHERE cpfr.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND cpfr.type='to' GROUP BY users_id)t GROUP BY t.userid)a ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer todayBuyMoney = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayBuyMoney = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayBuyMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 今日购买金额
     */
    public Double updateTodayBuyMoney(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(t.money) FROM ( "
                       + "SELECT SUM(i.in_money)/100 money FROM investors i WHERE i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND i.investor_status IN('1','2','3') "
                       + "UNION ALL "
                       + "SELECT SUM(cpfr.money)/100 money  FROM coin_purse_funds_record  cpfr WHERE cpfr.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND cpfr.type='to')t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayBuyMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayBuyMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayBuyMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 平台总交易额 投资表中,已付款，结算中,已结算 的所有数据求和，转换成元 和 零钱包中是转入状态的所有记录的和，转换成元 两者相加
     */
    public Double updateAllBuyMoney() {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();

            StringBuffer sql = new StringBuffer();
            sql.append("SELECT  COALESCE(SUM(t.money), 0) FROM ( " + "SELECT COALESCE(SUM(i.in_money), 0)/100 money FROM investors i WHERE  i.investor_status IN('1','2','3') "
                       + "UNION ALL " + "SELECT COALESCE(SUM(cpfr.money), 0)/100 money  FROM coin_purse_funds_record  cpfr WHERE  cpfr.type='to')t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double allBuyMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                allBuyMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return allBuyMoney;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新当日认证人数
     */
    public Integer updateTodaycertificationCount(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM account ac WHERE ac.insert_time  BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer registerCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0)) && loadAllSql != null) {
                registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return registerCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新平台总认证人数
     */
    public Integer updateCertificationCount(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM account ac WHERE ac.STATUS = 1 ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    sql.append("AND ac.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    sql.append("AND ac.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                sql.append("AND ac.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            }
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer registerCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return registerCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新今日注册人数
     */
    public Integer updateTodayregisterCount(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM users u WHERE u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer registerCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return registerCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新累计提现金额
     */
    public Double uodateAllOutCashMoney(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            StringBuffer sql = new StringBuffer();
            List<Object> list = new ArrayList<Object>();
            sql.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr WHERE tr.is_check = '1' AND tr.status = '1' AND tr.check_time ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    sql.append(" BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    sql.append(" < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                sql.append(" < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            }
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double allMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                allMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return allMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新今日提现金额
     */
    public Double uodateTodayOutCashMoney(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr " + "WHERE tr.is_check = '1' AND tr.status = '1' AND tr.check_time "
                       + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double allMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                allMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return allMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新平台充值总额; 已募集产品的总额(分)
     */
    public Double updateRechargeMoney(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            // 获取昨日充值金额
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    sql.append("AND cz.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    sql.append("AND cz.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                sql.append("AND cz.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            }
            Object object = dao.LoadAllSql(sql.toString(), list.toArray()).get(0);
            Double allmoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(object)) {
                allmoney = Double.valueOf(dao.LoadAllSql(sql.toString(), list.toArray()).get(0).toString().replaceAll(",", ""));
            }
            return allmoney + 1583776;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新平台的注册人数;<br>
     * 用户注册成功之后调用此方法;
     */
    public Integer updateRegisterCount(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer("SELECT SUM(1) FROM users u ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    sql.append("WHERE u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    sql.append("WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                sql.append("WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            }
            Object object = dao.LoadAllSql(sql.toString(), list.toArray()).get(0);
            Integer parseInt = 0;
            if (!QwyUtil.isNullAndEmpty(object)) {
                parseInt = Integer.valueOf(dao.LoadAllSql(sql.toString(), list.toArray()).get(0).toString());
            }
            return parseInt;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 获取昨日今日平台增量
     */
    public Double findTodayCapitalStock(String insertTime) {
        try {
            Platform plat = (Platform) dao.findById(new Platform(), 1L);
            List<Object> list = new ArrayList<Object>();
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String time = sd.format(date);
                list.add(time);
                list.add(time);
                list.add(time);
                list.add(time);
            }
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(money),2) FROM(  ");
            sql.append("SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("UNION ");
            sql.append("SELECT SUM(-tr.money/100) money FROM tx_record tr WHERE tr.is_check = '1' AND tr.check_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayCapitalStock = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayCapitalStock = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayCapitalStock;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

}

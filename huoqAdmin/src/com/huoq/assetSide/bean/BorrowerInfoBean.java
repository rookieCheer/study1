package com.huoq.assetSide.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import org.springframework.stereotype.Service;

import com.huoq.assetSide.dao.BorrowerInfoDao;
import com.huoq.util.MyLogger;

/**
 * 借款用户基本信息
 */
@Service
public class BorrowerInfoBean {

    private static MyLogger log = MyLogger.getLogger(BorrowerInfoBean.class);
    @Resource
    private BorrowerInfoDao borrowerInfoDao;


    /**
     * 根据主键获取借款人信息
     *
     * @param borrowerInfoId 主键
     * @return
     */
    @SuppressWarnings("unchecked")
    public BorrowerInfo getBorrowerInfoById(Integer borrowerInfoId) {
        String hql = "from BorrowerInfo b where b.id=?";
        List<BorrowerInfo> borrowerInfoList = borrowerInfoDao.LoadAll(hql, new Object[]{borrowerInfoId});
        if (null != borrowerInfoList && !borrowerInfoList.isEmpty()) {
            return borrowerInfoList.get(0);
        }
        return null;
    }

    /**
     * 更新数据
     *
     * @param borrowerInfo
     */
    public void updateBorrowerInfo(BorrowerInfo borrowerInfo) {

        this.borrowerInfoDao.update(borrowerInfo);
    }


    /**
     * 获取已发布的产品总数;(包含新手产品,包含预约中的产品)
     *
     * @param type 0为普通项目,1为:新手专享;
     * @return
     */
    public int getProductCount(String[] type) {
        String myType = QwyUtil.packString(type);
        StringBuffer hql = new StringBuffer();
        hql.append("SELECT COUNT(*) FROM  Product pro ");
        hql.append("WHERE pro.productStatus IN ('-3','0','1','2','3') ");
        hql.append("AND pro.investType=7 ");
        if (!QwyUtil.isNullAndEmpty(myType)) {
            hql.append("AND pro.productType IN (" + myType + ")");
        }
        Object obj = borrowerInfoDao.findJoinActive(hql.toString(), null);
        return obj == null ? 0 : Integer.parseInt(obj.toString()) > 0 ? Integer.parseInt(obj.toString()) : 0;


    }

    /**
     * 获取借款人总数
     *
     * @return
     */
    public int getBorrowerInfoAmount() {
        StringBuffer hql = new StringBuffer();
        hql.append("SELECT COUNT(boUserName) FROM BorrowerInfo where status=1");
        Object obj = borrowerInfoDao.findJoinActive(hql.toString(), null);
        return obj == null ? 0 : Integer.parseInt(obj.toString()) > 0 ? Integer.parseInt(obj.toString()) : 0;

    }

    /**
     * 获取借款人总金额
     *
     * @return
     */
    public Double getBorrowerInfoCount() {
        StringBuffer hql = new StringBuffer();
        hql.append("SELECT SUM(boAmount)/10000  FROM  BorrowerInfo where status=1");
        Object obj = borrowerInfoDao.findJoinActive(hql.toString(), null);
        return obj == null ? 0 : Double.parseDouble(obj.toString()) > 0 ? Double.parseDouble(obj.toString()) : 0L;
    }

    /**
     * 根据单个状态获取理财产品
     *
     * @param productStatus //产品状态 -3:排队中 -2：审核不通过 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Product> getProductByStatus(String productStatus) {
        StringBuffer hql = new StringBuffer();
        hql.append("FROM Product pro ");
        hql.append("WHERE pro.productStatus = ? ");
        hql.append("ORDER BY pro.productType DESC,pro.title ASC,pro.insertTime DESC ");
        return (List<Product>) borrowerInfoDao.LoadAll(hql.toString(), new Object[]{productStatus});
    }

    /**
     * 根据借款期限查询借款人的信息
     *
     * @return
     */
    public List findProductPageUtil(String termLoan) {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        sql.append(" SELECT b.id,b.boUserName,b.thirdTransaction,b.boAmount ,b.termLoan ");
        sql.append(" FROM borrower_info b ");
        sql.append(" WHERE b.status=1 ");
        sql.append(" And b.bosource='caiwei' ");
        if (!QwyUtil.isNullAndEmpty(termLoan)) {
            sql.append("AND b.termLoan IN (?)");
            params.add(termLoan);
            return borrowerInfoDao.LoadAllSql(sql.toString(), params.toArray());
        }
        return borrowerInfoDao.LoadAllSql(sql.toString(), null);
    }

    //根据选中的id查询借款人总金额
    public BigDecimal getProductAllMoney(String[] userIds) {
        BigDecimal res = BigDecimal.ZERO;
        for (String ids : userIds) {
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                Integer userId = Integer.valueOf(id);
                BorrowerInfo borrowerInfo = this.getBorrowerInfoById(userId);
                if (null != borrowerInfo) {
                    BigDecimal money = borrowerInfo.getBoAmount();
                    if (null != borrowerInfo.getBoAmount()) {
                        res = res.add(borrowerInfo.getBoAmount());
                    }
                }
            }
        }
        return res;
    }


    /**
     * 修改借款人信息
     *
     * @param userIds
     */
    public void updateYYBorrowerInfo(String[] userIds) {
        for (String ids : userIds) {
            String[] split = ids.split(",");
            for (String id : split) {
                Integer userId = Integer.valueOf(id);
                BorrowerInfo borrowerInfo = this.getBorrowerInfoById(userId);
                if (null != borrowerInfo) {
                    borrowerInfo.setStatus(11);
                    borrowerInfo.setDtModify(new Date());
                    borrowerInfo.setRemark("募集中");
                    borrowerInfoDao.update(borrowerInfo);
                }
            }
        }

    }

    /**
     * 根据选中的id查询项目的总金额
     *
     * @param borrowerInfo
     * @return
     */
    public String saveBorrowerInfo(BorrowerInfo borrowerInfo) {
        try {
            borrowerInfo.setStatus(borrowerInfo.getTermLoan());
            borrowerInfo.setBoUserName(borrowerInfo.getBoUserName());
            borrowerInfo.setBoAmount(borrowerInfo.getBoAmount());
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return (borrowerInfoDao.save(borrowerInfo));
    }


    /**
     * 添加预约新产品
     *
     * @param yyproduct Product 实体;
     * @return 返回新生成的id;
     */

    public String saveYYProduct(Product yyproduct) {
        try {
            yyproduct.setPayInterestWay("1");
            yyproduct.setFinancingAmount(yyproduct.getFinancingAmount());
            yyproduct.setAllCopies(QwyUtil.calcNumber(yyproduct.getFinancingAmount(), 100, "/").longValue());
            yyproduct.setHasCopies(0L);
            yyproduct.setLeftCopies(yyproduct.getAllCopies());
            yyproduct.setProductStatus("-3");
            yyproduct.setInsertTime(new Date());
            yyproduct.setCalcInterestWay("0");
            yyproduct.setBookingTime(yyproduct.getBookingTime());
            yyproduct.setProgress(0d);
            yyproduct.setAnnualEarnings(yyproduct.getBaseEarnings());
            yyproduct.setRisk(yyproduct.getRisk());
            yyproduct.setiStatus(0);

        } catch (Exception e) {
            log.error("操作异常: ", e);

        }
        return borrowerInfoDao.save(yyproduct);
    }

    //根据选中的id查询借款人信息
    public void getBorrowerInfoUser(String[] userIds) {
        for (String ids : userIds) {
            String[] split = ids.split(",");
            for (String id : split) {
                Integer userId = Integer.valueOf(id);
                BorrowerInfo borrowerInfo = this.getBorrowerInfoById(userId);

            }
        }
    }

    /**
     * 删除信用贷预约产品
     *
     * @param productId
     * @throws Exception
     */
    public void deleteCreditProduct(String productId) throws Exception {
        Product product = new Product();
        product = (Product) borrowerInfoDao.findById(product, productId);
        borrowerInfoDao.delete(product);

    }

    public List<Object> findBorrowerInfo(String productId) {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        sql.append(" SELECT i.id");
        sql.append(" FROM borrower_info i  ");
        sql.append(" LEFT JOIN project_borrower_info p  ON p.borrowerInfoId=i.id ");
        if (!QwyUtil.isNullAndEmpty(productId)) {
            sql.append("WHERE p.productId IN (?)");
            params.add(productId);
        }
        return borrowerInfoDao.LoadAllSql(sql.toString(), params.toArray());

    }

    public void upBorrowerInfo(BorrowerInfo borrowerInfo) {
        borrowerInfo.setStatus(1);
        borrowerInfo.setRemark("待放款");
        borrowerInfoDao.update(borrowerInfo);
    }


    public List<Object> findProjectBorrowerInfoById(String productId) {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        sql.append(" SELECT p.id ");
        sql.append(" FROM project_borrower_info p  ");
        if (!QwyUtil.isNullAndEmpty(productId)) {
            sql.append("WHERE p.productId IN (?)");
            params.add(productId);
        }
        return borrowerInfoDao.LoadAllSql(sql.toString(), params.toArray());
    }




    public ProjectBorrowerInfo getProjectBorrowerInfoById(Integer id) {
        String hql = "from ProjectBorrowerInfo b where b.id=?";
        List<ProjectBorrowerInfo> borrowerInfoList = borrowerInfoDao.LoadAll(hql, new Object[]{id});
        if (null != borrowerInfoList && !borrowerInfoList.isEmpty()) {
            return borrowerInfoList.get(0);
        }
        return null;
    }

    public void deleteProduct(ProjectBorrowerInfo projectBorrowerInfo) {
        borrowerInfoDao.delete(projectBorrowerInfo);
    }

    public BorrowerPlayMoney getBorrowerPlayMoneyById(Integer id) {
        String hql = "from BorrowerPlayMoney b where b.id=?";
        List<BorrowerPlayMoney> borrowerInfoList = borrowerInfoDao.LoadAll(hql,  new Object[]{id});
        if (null != borrowerInfoList && !borrowerInfoList.isEmpty()) {
            return borrowerInfoList.get(0);
        }
        return null;
    }

    public List getBorrowerPlayMoneyByPid(String s) {
        StringBuffer sql = new StringBuffer();
        sql.append("select b.id,b.productId,b.status from borrower_play_money b where b.productId=?") ;
        List<BorrowerPlayMoney> borrowerInfoList = borrowerInfoDao.LoadAllSql(sql.toString(),  new Object[]{s});
        if (null != borrowerInfoList && !borrowerInfoList.isEmpty()) {
            return borrowerInfoList;
        }
        return null;
    }


    public List<Object[]> findProjectBorrowerInfo(String productId) {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        sql.append(" SELECT p.id ,p.productId,p.boUserName,b.thirdTransaction,p.boAmount,p.termLoan ");
        sql.append(" FROM project_borrower_info p  ");
        sql.append(" LEFT JOIN  borrower_info b on b.id=p.borrowerInfoId ");
        if (!QwyUtil.isNullAndEmpty(productId)) {
            sql.append("WHERE p.productId IN (?)");
            params.add(productId);
        }
        return borrowerInfoDao.LoadAllSql(sql.toString(), params.toArray());
    }

}
package com.huoq.assetSide.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huoq.assetSide.action.BaseResponse;
import com.huoq.assetSide.dao.BorrowerPlayMoneyDao;
import com.huoq.assetSide.vo.BorrowerPlayMoneyVo;
import com.huoq.baofoo.bean.BaofooBean;
import com.huoq.baofoo.utils.BaofooPayParams;
import com.huoq.baofoo.utils.PayResult;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.Des;
import com.huoq.common.util.HttpUtils;
import com.huoq.common.util.MyRedis;
import com.huoq.common.util.OrderNumerUtil;
import com.huoq.datejson.handle.DateJsonObjectProcessor;
import com.huoq.newbranch.enums.DEF_ENUM;
import com.huoq.orm.BorrowerInfo;
import com.huoq.orm.BorrowerPlayMoney;
import com.huoq.orm.ProjectBorrowerInfo;
import com.huoq.product.bean.ProductBean;
import com.huoq.util.MyLogger;
import com.huoq.util.RestErrorCode;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 借款用户打款
 * 
 * @author yangpeiliang
 */
@Service
public class BorrowerPlayMoneyBean {

    private static MyLogger         log = MyLogger.getLogger(BorrowerPlayMoneyBean.class);
    @Resource
    private ProjectBorrowerInfoBean projectBorrowerInfoBean;
    @Resource
    private BorrowerInfoBean        borrowerInfoBean;
    @Resource
    private ProductBean             productBean;
    @Resource
    private BaofooBean              baofooBean;
    @Resource
    private BorrowerPlayMoneyDao    borrowerPlayMoneyDao;

    /**
     * 给多个借款人批量打款
     * 
     * @param productId 标的ID
     */
    @Transactional
    public Integer playMoney(String productId) throws Exception {
        Integer res = 0;
        String notifyUrl = "";
        MyRedis redis = new MyRedis();
        String indentifier = redis.lockWithTimeout("playMoney_" + productId, 5000, 1000);
        List<BorrowerPlayMoneyVo> borrowerPlayMoneyVoList = new ArrayList<BorrowerPlayMoneyVo>();
        // 根据标的ID获取所有借款人信息
        List<ProjectBorrowerInfo> projectBorrowerInfoList = this.projectBorrowerInfoBean.getProjectBorrowerInfoByProductId(productId);
        for (ProjectBorrowerInfo projectBorrowerInfo : projectBorrowerInfoList) {
            BorrowerPlayMoneyVo borrowerPlayMoneyVo = new BorrowerPlayMoneyVo();
            synchronized (this) {
                // 获取借款人信息
                BorrowerInfo borrowerInfo = this.borrowerInfoBean.getBorrowerInfoById(projectBorrowerInfo.getBorrowerInfoId());
                try {
                    if (null != borrowerInfo && borrowerInfo.getStatus() == 11) {
                        notifyUrl = borrowerInfo.getNotifyUrl();
                        String payMentTransaction = "DK" + OrderNumerUtil.getSerialNumber(DEF_ENUM.DEF_ONE.getnCode(), borrowerInfo.getId());// 还款流水号
                        // 防止重复打款
                        List<BorrowerPlayMoney> borrowerPlayMoneyList = this.getPlayMoneyByThirdTransaction(borrowerInfo.getThirdTransaction());
                        if (null == borrowerPlayMoneyList || borrowerPlayMoneyList.isEmpty()) {
                            // 保存打款记录信息
                            BorrowerPlayMoney borrowerPlayMoney = new BorrowerPlayMoney();
                            borrowerPlayMoney.setThirdTransaction(borrowerInfo.getThirdTransaction());
                            borrowerPlayMoney.setPayMentTransaction(payMentTransaction);
                            borrowerPlayMoney.setPayAmount(borrowerInfo.getBoAmount());
                            borrowerPlayMoney.setProductId(productId);
                            borrowerPlayMoney.setBorrowerInfoId(borrowerInfo.getId());

                            List<BaofooPayParams> payParamsList = this.procBaofooParam(borrowerInfo, payMentTransaction);
                            PayResult payResult = this.baofooBean.remittance(payParamsList);
                            if ("0000".equals(payResult.getCode())) {// 支付成功
                                borrowerPlayMoney.setStatus(DEF_ENUM.DEF_ZERO.getnCode());
                                borrowerInfo.setStatus(DEF_ENUM.DEF_THREE.getnCode());
                                borrowerInfo.setRemark(RestErrorCode.LOAN_SUCCESS.getMessage());
                                res++;
                            } else {// 支付失败
                                borrowerPlayMoney.setStatus(DEF_ENUM.DEF_ONE.getnCode());
                                borrowerInfo.setStatus(DEF_ENUM.DEF_FOUR.getnCode());
                                borrowerInfo.setRemark(RestErrorCode.LOAN_FAILURE.getMessage());
                            }
                            borrowerPlayMoney.setReturnCode(payResult.getCode());
                            borrowerPlayMoney.setReturnMsg(payResult.getMessage());
                            if (null != payResult.getDataModel()) {
                                borrowerPlayMoney.setReturnRes(payResult.getDataModel().toString());
                            }
                            borrowerPlayMoney.setDtCreate(new Date());
                            borrowerPlayMoney.setDtModify(new Date());
                            this.borrowerPlayMoneyDao.save(borrowerPlayMoney);

                            // 更新借款人状态
                            borrowerInfo.setDtModify(new Date());
                            this.borrowerInfoBean.updateBorrowerInfo(borrowerInfo);

                            // 更新标的状态
                            this.productBean.updateProduct(productId);

                            // 推送打款结果
                            borrowerPlayMoneyVo.setThirdTransaction(borrowerInfo.getThirdTransaction());
                            borrowerPlayMoneyVo.setPayMentTransaction(payMentTransaction);
                            borrowerPlayMoneyVo.setPayAmount(borrowerInfo.getBoAmount());
                            borrowerPlayMoneyVo.setStatus(borrowerPlayMoney.getStatus());
                            if (borrowerPlayMoney.getStatus() == DEF_ENUM.DEF_ZERO.getnCode()) {
                                borrowerPlayMoneyVo.setRemark(RestErrorCode.LOAN_SUCCESS.getMessage());
                            } else {
                                borrowerPlayMoneyVo.setRemark(RestErrorCode.LOAN_FAILURE.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    borrowerPlayMoneyVo.setThirdTransaction(borrowerInfo.getThirdTransaction());
                    borrowerPlayMoneyVo.setPayMentTransaction("");
                    borrowerPlayMoneyVo.setPayAmount(borrowerInfo.getBoAmount());
                    borrowerPlayMoneyVo.setStatus(DEF_ENUM.DEF_THREE.getnCode());
                    borrowerPlayMoneyVo.setRemark(RestErrorCode.LOAN_FAILURE.getMessage());
                    log.error(e.getMessage(), e);
                }
            }
            redis.releaseLock("playMoney_" + productId, indentifier);
            borrowerPlayMoneyVoList.add(borrowerPlayMoneyVo);
            // 向第三方推送打款通知
            this.procBorrowerPlayMoney(borrowerPlayMoneyVoList, notifyUrl);
        }
        if (res == 0) {
            return 0;
        }
        if (null != projectBorrowerInfoList && !projectBorrowerInfoList.isEmpty()) {
            if (projectBorrowerInfoList.size() == res) {
                return 1;
            }
            if (res > 0 && res < projectBorrowerInfoList.size()) {
                return 2;
            }
        }
        return null;
    }

    /**
     * 给单个借款人单独打款
     * 
     * @param playMoneyId 打款主键
     */
    @Transactional
    public Boolean playMoneyAlone(Integer playMoneyId) throws Exception {
        Boolean res = Boolean.FALSE;
        MyRedis redis = new MyRedis();
        String indentifier = redis.lockWithTimeout("playMoneyAlone_" + playMoneyId, 5000, 1000);
        // 根据标的ID获取所有借款人信息
        List<BorrowerPlayMoney> borrowerPlayMoneyList = this.getPlayMoneyById(playMoneyId);
        if (null != borrowerPlayMoneyList && !borrowerPlayMoneyList.isEmpty()) {
            BorrowerPlayMoney borrowerPlayMoney = borrowerPlayMoneyList.get(0);
            try {
                // 获取借款人信息
                BorrowerInfo borrowerInfo = this.borrowerInfoBean.getBorrowerInfoById(borrowerPlayMoney.getBorrowerInfoId());
                if (null != borrowerInfo && borrowerInfo.getStatus() == 4) {
                    String payMentTransaction = "DK" + OrderNumerUtil.getSerialNumber(DEF_ENUM.DEF_ONE.getnCode(), borrowerInfo.getId());// 还款流水号
                    // 调用宝付接口
                    List<BaofooPayParams> payParamsList = this.procBaofooParam(borrowerInfo, payMentTransaction);
                    PayResult payResult = this.baofooBean.remittance(payParamsList);
                    if ("0000".equals(payResult.getCode())) {// 支付成功
                        borrowerPlayMoney.setStatus(DEF_ENUM.DEF_ZERO.getnCode());
                        borrowerInfo.setStatus(DEF_ENUM.DEF_THREE.getnCode());
                        borrowerInfo.setRemark(RestErrorCode.LOAN_SUCCESS.getMessage());
                        res = Boolean.TRUE;
                    } else {// 支付失败
                        borrowerPlayMoney.setStatus(DEF_ENUM.DEF_ONE.getnCode());
                        borrowerInfo.setStatus(DEF_ENUM.DEF_FOUR.getnCode());
                        borrowerInfo.setRemark(RestErrorCode.LOAN_FAILURE.getMessage());
                    }
                    borrowerPlayMoney.setReturnCode(payResult.getCode());
                    borrowerPlayMoney.setReturnMsg(payResult.getMessage());
                    if (null != payResult.getDataModel()) {
                        borrowerPlayMoney.setReturnRes(payResult.getDataModel().toString());
                    }
                    borrowerPlayMoney.setDtModify(new Date());
                    this.borrowerPlayMoneyDao.update(borrowerPlayMoney);

                    // 更新借款人状态
                    borrowerInfo.setDtModify(new Date());
                    this.borrowerInfoBean.updateBorrowerInfo(borrowerInfo);

                    // 更新标的状态
                    this.productBean.updateProduct(borrowerPlayMoney.getProductId());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                redis.releaseLock("playMoneyAlone_" + playMoneyId, indentifier);
            }
        }
        return res;
    }

    /**
     * 向第三方推送打款通知
     * 
     * @paramborrowerPlayMoneyVoList
     * @param notifyUrl
     */
    private void procBorrowerPlayMoney(List<BorrowerPlayMoneyVo> borrowerPlayMoneyVoLis, String notifyUrl) {
        log.info("打款回调通知开始..." + notifyUrl);
        final String vcNotifyUrl = notifyUrl;
        final List<BorrowerPlayMoneyVo> borrowerPlayMoneyVoList = borrowerPlayMoneyVoLis;
        new Thread() {

            public void run() {
                try {
                    sleep(5000);
                    this.nodifyPlayMoney(borrowerPlayMoneyVoList, vcNotifyUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage(), e);
                }
            }

            private void nodifyPlayMoney(List<BorrowerPlayMoneyVo> borrowerPlayMoneyVoList, String notifyUrl) throws Exception {
                BaseResponse baseResponse = new BaseResponse();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("notifyType", DEF_ENUM.DEF_ONE.getnCode());
                map.put("result", borrowerPlayMoneyVoList);
                // 数据结构封装
                net.sf.json.JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(java.util.Date.class, new DateJsonObjectProcessor());
                net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(map, config);
                baseResponse.setData(json.toString());
                // 加密处理
                JSONObject jsonObject = JSONObject.fromObject(baseResponse);
                String params = Des.encode(Des.secretKey, jsonObject.toString());

                // http请求推送
                log.info("=======打款回调========" + notifyUrl + "<>" + params.toString());
                HttpUtils.post(notifyUrl, params);
                // HttpUtils.post("http://127.0.0.1:8081/huoqService/assetSide/queryBorrowerUserinfo", params);
            };
        }.start();
        log.info("打款回调通知结束...");
    }

    /**
     * 打款异步通知
     * 
     * @param transContent
     */
    @Transactional
    public void notifyplayMoney(String transContent,String dataType) throws Exception {
        log.info("打款结果宝付异步通知方法开始..." + transContent);
        MyRedis redis = new MyRedis();
        String indentifier = redis.lockWithTimeout("notifyplayMoney_", 5000, 1000);
        try {
            synchronized (this) {
                PayResult payResult = this.baofooBean.getContent(transContent,dataType);
                JSONObject jSONObject = payResult.getDataModel();
                String payMentTransaction = jSONObject.getString("trans_no");
                // 根据打款流水号获取打款信息
                List<BorrowerPlayMoney> borrowerPlayMoneyList = this.getPlayMoneyByPayMentTransaction(payMentTransaction);
                if (null != borrowerPlayMoneyList && !borrowerPlayMoneyList.isEmpty()) {
                    BorrowerPlayMoney borrowerPlayMoney = borrowerPlayMoneyList.get(0);
                    if ("0000".equals(payResult.getCode())) {// 打款成功
                        borrowerPlayMoney.setStatus(DEF_ENUM.DEF_ZERO.getnCode());
                    } else {
                        borrowerPlayMoney.setStatus(DEF_ENUM.DEF_ONE.getnCode());
                    }
                    borrowerPlayMoney.setReturnCode(payResult.getCode());
                    borrowerPlayMoney.setReturnMsg(payResult.getMessage());
                    if (null != payResult.getDataModel()) {
                        borrowerPlayMoney.setReturnRes(payResult.getDataModel().toString());
                    }
                    borrowerPlayMoney.setDtModify(new Date());
                    this.borrowerPlayMoneyDao.update(borrowerPlayMoney);

                    // 更新借款人基本信息打款结果
                    BorrowerInfo borrowerInfo = this.borrowerInfoBean.getBorrowerInfoById(borrowerPlayMoney.getBorrowerInfoId());
                    if (null != borrowerInfo) {
                        if (borrowerPlayMoney.getStatus() == DEF_ENUM.DEF_ZERO.getnCode()) {
                            borrowerInfo.setStatus(DEF_ENUM.DEF_THREE.getnCode());
                            borrowerInfo.setRemark(RestErrorCode.LOAN_SUCCESS.getMessage());
                        } else {
                            borrowerInfo.setStatus(DEF_ENUM.DEF_FOUR.getnCode());
                            borrowerInfo.setRemark(RestErrorCode.LOAN_FAILURE.getMessage());
                        }
                        borrowerInfo.setDtModify(new Date());
                        this.borrowerInfoBean.upBorrowerInfo(borrowerInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            redis.releaseLock("notifyplayMoney_", indentifier);
        }
        log.info("打款结果宝付异步通知方法结束...");
    }

    /**
     * 封装宝付参数
     * 
     * @param borrowerInfo
     * @param payMentTransaction
     * @return
     */
    private List<BaofooPayParams> procBaofooParam(BorrowerInfo borrowerInfo, String payMentTransaction) throws Exception {
        List<BaofooPayParams> payParamsList = new ArrayList<>();
        BaofooPayParams baofooPayParams = new BaofooPayParams();
        baofooPayParams.setTransNo(payMentTransaction);
        baofooPayParams.setTransMoney(borrowerInfo.getBoAmount().toString());
        baofooPayParams.setToAccName(DESEncrypt.jieMiUsername(borrowerInfo.getBoUserName()));
        baofooPayParams.setToAccNo(DESEncrypt.jieMiBankCard(borrowerInfo.getBoAccNo()));
        baofooPayParams.setToBankName(borrowerInfo.getBoBankName());
        baofooPayParams.setTransCardId(DESEncrypt.jieMiIdCard(borrowerInfo.getIdCard()));
        baofooPayParams.setTransMobile(borrowerInfo.getTransMobile());
        payParamsList.add(baofooPayParams);
        return payParamsList;
    }

    /**
     * 根据打款流水号查询打款信息
     * 
     * @param payMentTransaction 打款流水号
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<BorrowerPlayMoney> getPlayMoneyByPayMentTransaction(String payMentTransaction) {
        String hql = "from BorrowerPlayMoney b where b.payMentTransaction=?";
        return borrowerPlayMoneyDao.LoadAll(hql, new Object[] { payMentTransaction });
    }

    /**
     * 根据第三方流水号查询打款信息
     * 
     * @param thirdTransaction 第三方流水号
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<BorrowerPlayMoney> getPlayMoneyByThirdTransaction(String thirdTransaction) {
        String hql = "from BorrowerPlayMoney b where b.thirdTransaction=?";
        return borrowerPlayMoneyDao.LoadAll(hql, new Object[] { thirdTransaction });
    }

    /**
     * 根据主键查询打款信息
     * 
     * @param id 打款ID
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<BorrowerPlayMoney> getPlayMoneyById(Integer id) {
        String hql = "from BorrowerPlayMoney b where b.id=?";
        return borrowerPlayMoneyDao.LoadAll(hql, new Object[] { id });
    }
}

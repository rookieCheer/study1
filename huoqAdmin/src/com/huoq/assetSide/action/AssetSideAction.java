package com.huoq.assetSide.action;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huoq.assetSide.bean.BorrowerPlayMoneyBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.util.MyLogger;

/**
 * 给借款人打款
 * 
 * @author Administrator
 */
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 发布产品页面
@Results({ @Result(name = "err", value = "/Product/Admin/err.jsp"), @Result(name = "error", value = "/Product/Admin/error.jsp"),
           @Result(name = "creditYYProduct", value = "/Product/Admin/creditProduct/credit.jsp")

})
public class AssetSideAction extends BaseAction {

    private static MyLogger       logger = MyLogger.getLogger(AssetSideAction.class);

    @Resource
    private BorrowerPlayMoneyBean borrowerPlayMoneyBean;
    // 标的ID
    private String                productId;
    // 打款主键
    private Integer               playMoneyId;

    /**
     * 给多个借款人批量打款
     * 
     * @returnE
     */
    public String playMoney() throws Exception {
        String json = "";
        try {
            Integer res = this.borrowerPlayMoneyBean.playMoney(productId);
            if (null != res) {
                if (res == 0) {
                    json = QwyUtil.getJSONString("fail", "打款全部失败");
                } else if (res == 1) {
                    json = QwyUtil.getJSONString("success", "打款成功");
                } else if (res == 2) {
                    json = QwyUtil.getJSONString("partSuccess", "部分打款成功");
                }
            }
        } catch (Exception e) {
            QwyUtil.getJSONString("error", "打款出现系统异常");
            logger.error("接口AssetSideAction的playMoney方法出现异常, 原因：", e);
        }
        QwyUtil.printJSON(getResponse(), json);
        return null;
    }

    /**
     * 给单个借款人单独打款
     * 
     * @returnE
     */
    public String playMoneyAlone() throws Exception {
        String json = "";
        try {
            Boolean res = this.borrowerPlayMoneyBean.playMoneyAlone(playMoneyId);
            if (res) {
                json = QwyUtil.getJSONString("success", "打款成功");
            } else {
                json = QwyUtil.getJSONString("fail", "打款失败");
            }
        } catch (Exception e) {
            QwyUtil.getJSONString("error", "打款出现系统异常");
            logger.error("接口AssetSideAction的playMoneyAlone方法出现异常, 原因：", e);
        }
        QwyUtil.printJSON(getResponse(), json);
        return null;
    }

    /**
     * 打款结果宝付异步通知
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/notifyplayMoney", method = RequestMethod.POST)
    public void notifyplayMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            logger.info("###############打款结果宝付异步通知###########");
            String transContent = request.getParameter("data_content");
            String dataType = request.getParameter("data_type");
            logger.info(transContent);
            logger.info(dataType);
            if (!StringUtils.isEmpty(transContent)) {
                this.borrowerPlayMoneyBean.notifyplayMoney(transContent,dataType);
                response.getWriter().println("OK");
            }
        } catch (Exception e) {
            logger.error("接口AssetSideAction的notifyplayMoney方法出现异常, 原因：", e);
            response.getWriter().println("fail");
        }
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getPlayMoneyId() {
        return playMoneyId;
    }

    public void setPlayMoneyId(Integer playMoneyId) {
        this.playMoneyId = playMoneyId;
    }

}

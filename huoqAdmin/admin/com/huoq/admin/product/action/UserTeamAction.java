package com.huoq.admin.product.action;

import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.InviteBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
import com.huoq.couponRule.couponRuleBean.CouponRecordBean;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.Invite;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 后台管理--注册人数;
 *
 * @author qwy
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
        @Result(name = "teamList", value = "/Product/Admin/userTeam/teamList.jsp"),
        @Result(name = "friendRecords", value = "/Product/Admin/userTeam/friendRecords.jsp")
})
public class UserTeamAction extends BaseAction {
    @Resource
    private InviteBean inviteBean;
    @Resource
    private RegisterUserBean registerUserBean;
    @Resource
    private CouponRecordBean couponRecordBean;
    @Resource
    private UserRechargeBean userRechargeBean;

    public String teamList() {
        UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
        if (QwyUtil.isNullAndEmpty(users)) {
            String json = QwyUtil.getJSONString("err", "管理员未登录");
            try {
                QwyUtil.printJSON(getResponse(), json);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            //管理员没有登录;
            return null;
        }

        // 获取请求参数
        String username = getRequest().getParameter("username");
        Users currentUser = registerUserBean.getUsersByUsername(username);
        long uid = currentUser.getId();
        Double sumCost = 0.0;
        int inviteCount = 0;
        //获取邀请该用户的人
        Invite beInvite = inviteBean.findByBeInvitedId(uid);
        if (null != beInvite){
            Users user = (Users) registerUserBean.getUsersById(beInvite.getInviteId());
            if (null != user) {
                currentUser.setInviteName(user.getUsername());
            }
        }

        // 该用户邀请的好友
        List<Invite> invite = inviteBean.getInvite(uid);

        List<Map<String, Object>> list = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Invite inv : invite) {
            Users user = (Users) registerUserBean.getUsersById(inv.getBeInvitedId());
            if (!QwyUtil.isNullAndEmpty(user)) {
                // 好友列表
                List friendlist = couponRecordBean.friendItemsSumCost(uid, user);
                if (!QwyUtil.isNullAndEmpty(friendlist)) {
                    list.addAll(friendlist);
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    user.setUsername(DESEncrypt.jieMiUsername(user.getUsername()));
                    map.put("initMoney", 0.0);
                    map.put("userName", user.getUsername());
                    map.put("fromUserid", user.getId());
                    map.put("date", sdf.format(user.getInsertTime()));
                    list.add(map);
                }
            }
        }
        // 查询该用户分销所得累积使用红包
        sumCost = couponRecordBean.findCouponRecordSum(uid);
        if (QwyUtil.isNullAndEmpty(sumCost)) {
            sumCost = 0.0;
        }
        // 当前用户的好友
        inviteCount = registerUserBean.getInviteCount(uid);
//			inviteCount = inviteBean.getInviteCount(uid);
        if (QwyUtil.isNullAndEmpty(inviteCount)) {
            inviteCount = 0;
        }

        HashMap okmap = new HashMap();
        okmap.put("user", currentUser);
        okmap.put("count", inviteCount);
        okmap.put("sum", sumCost);
        okmap.put("list", list);

        getRequest().setAttribute("data", okmap);

        return "teamList";
    }

    public String friendRecords() {
        UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
        if (QwyUtil.isNullAndEmpty(users)) {
            String json = QwyUtil.getJSONString("err", "管理员未登录");
            try {
                QwyUtil.printJSON(getResponse(), json);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            //管理员没有登录;
            return null;
        }

        // 获取请求参数
        long fromUserId = Long.parseLong(getRequest().getParameter("fromUserId"));
        long toUserId = Long.parseLong(getRequest().getParameter("toUserId"));
        Users fromUser = registerUserBean.getUsersById(fromUserId);

        // 获取好友名字（手机号）
        String userName = fromUser.getUsername();
        if (!QwyUtil.isNullAndEmpty(userName)) {
            userName = DESEncrypt.jieMiUsername(userName);
        }
        // 用户注册时间
        Date insertTime = fromUser.getInsertTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String inserttime = sdf.format(insertTime); 
        // 获取总金额
        Double sum = userRechargeBean.findCouponReleaseRecordSumMoney(fromUserId, toUserId);
        Double sumMoney = 0d;
        if (!QwyUtil.isNullAndEmpty(sum)) {
            sumMoney = sum;
        }    // 获取所有产生记录
        List<Map> list = userRechargeBean.findCouponReleaseRecord(fromUserId, toUserId);

        HashMap map = new HashMap();
        Users toUser = registerUserBean.getUsersById(toUserId);
        String toUserName = toUser.getUsername();
        if (!QwyUtil.isNullAndEmpty(toUserName)) {
        	toUserName = DESEncrypt.jieMiUsername(toUserName);
        }
        map.put("userName", userName);
        map.put("toUserName", toUserName);
        map.put("insertTime", inserttime);
        map.put("sumMoney", sumMoney/100);
        map.put("list", list);
        getRequest().setAttribute("data", map);
        return "friendRecords";
    }

}

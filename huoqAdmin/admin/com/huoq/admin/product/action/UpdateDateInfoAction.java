package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.UpdateDateInfoBean;
import com.huoq.common.bean.ToutiaoStatisticsTableBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Activity;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import com.huoq.thread.bean.UpdateQdtjThreadBean;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({@Result(name = "noLogin", value = "/Product/loginBackground.jsp"),
        @Result(name = "loadUserInfo", value = "/Product/Admin/operationManager/userInfo.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")})
public class UpdateDateInfoAction extends BankAction {
    @Autowired
    private UpdateDateInfoBean dateInfoBean;
    @Autowired
    private ToutiaoStatisticsTableBean toutiaoBean;
    @Autowired
    private UpdateQdtjThreadBean updateQdtjThreadBean;

    /**
     * 更新头条渠道数据(将头条ios用户的数据补全为ios用户)
     *
     * @return
     */
    public String updateUserDateInfo() {
        // 查询时间类
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            // 判断管理员是否登录
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                // 管理员没有登录;
                return "noLogin";
            }
            // 加密所有为加密头条数据
            //toutiaoBean.encryptUsername();
            // 查询所有user和头条数据去做对比如果为头条数据就修改数据类型为头条
            List<Users> user = dateInfoBean.findIOSUsers();// 查询出的用户都为头条IOS用户
            // 遍历查询出的user用户
            for (Users u : user) {
                // 获取出用户的渠道
                String registChannel = u.getRegistChannel();
                // 如果渠道的长度为10则为ios用户,如果渠道信息不包含tt(头条)
                if (registChannel.equals("1267250937")) {
                    // 获取id查询出改用户的所有信息
                    Long id = u.getId();
                    Users usersBuId = dateInfoBean.findUsersBuId(id);
                    // 获取改user的渠道信息改为头条
                    String registChannel1 = usersBuId.getRegistChannel();
                    usersBuId.setRegistChannel("ttIOS");
                    // 修改渠道状态为噶广告渠道
                    usersBuId.setChannelType("2");
                    dateInfoBean.updateUsersBuyId(usersBuId);
                }
            }

            //将所有的idfa码为空的用户设置为1267250937
            List<Long> users1 = dateInfoBean.findUsers();
            for(Long id:users1){
                Users usersBuId = dateInfoBean.findUsersBuId(id);
                String registChannel1 = usersBuId.getRegistChannel();
                if(registChannel1.equals("ttIOS")){
                    usersBuId.setRegistChannel("1267250937");
                    usersBuId.setChannelType("1");
                    dateInfoBean.updateUsersBuyId(usersBuId);
                }
                if(!QwyUtil.isNullAndEmpty(usersBuId.getInviteId())){
                    usersBuId.setRegistChannel("");
                    usersBuId.setChannelType("1");
                    dateInfoBean.updateUsersBuyId(usersBuId);
                }

            }
            //查询出所有渠道为ios01或者ios02的用户
            List<Users> haveChannelIOSUsers = dateInfoBean.findHaveChannelIOSUsers();
            // 遍历查询出的user用户
            for (Users u : haveChannelIOSUsers) {
                // 获取出用户的渠道
                String registChannel = u.getRegistChannel();
                if(registChannel.equals("ttIOS01")|| registChannel.equals("ttIOS02")){
                    // 获取id查询出改用户的所有信息
                    Long id = u.getId();
                    Users usersBuId = dateInfoBean.findUsersBuId(id);
                    // 获取改user的渠道信息改为头条
                    usersBuId.setRegistChannel(u.getRegistChannel());
                    // 修改渠道状态为噶广告渠道
                    usersBuId.setChannelType("2");
                    dateInfoBean.updateUsersBuyId(usersBuId);
                }
            }

            //查询出所有为头条的android用户
            List<Users> androidusers = dateInfoBean.findandroidUsers();
            // 遍历查询出的user用户
            for (Users u : androidusers) {
                // 获取出用户的渠道
                String registChannel = u.getRegistChannel();
                // 如果渠道的长度为10则为ios用户,如果渠道信息不包含tt(头条)
                if (!registChannel.contains("tt")) {
                    // 获取id查询出改用户的所有信息
                    Long id = u.getId();
                    Users usersBuId = dateInfoBean.findUsersBuId(id);
                    // 获取改user的渠道信息改为头条
                    String registChannel1 = usersBuId.getRegistChannel();
                    usersBuId.setRegistChannel("ttandriod");
                    // 修改渠道状态为噶广告渠道
                    usersBuId.setChannelType("2");
                    dateInfoBean.updateUsersBuyId(usersBuId);
                }
            }


            // 查询所有user和头条数据去做对比如果为头条数据就修改数据类型为头条(本来是修改custom的问题但是custom可能不是头条客户)
            List<Users> aduser = dateInfoBean.findAndroidUsers();// 查询出的用户都为头条IOS用户
            // 遍历查询出的user用户
            for (Users u : aduser) {
                // 获取出用户的渠道
                String registChannel = u.getRegistChannel();
                // 如果渠道的长度为10则为ios用户,如果渠道信息不包含tt(头条)
                if (registChannel.equals("custom")) {
                    //获取id查询出改用户的所有信息
                    Long id = u.getId();
                    Users usersBuId = dateInfoBean.findUsersBuId(id);
                    //获取改user的渠道信息改为头条
                    String registChannel1 = usersBuId.getRegistChannel();
                    usersBuId.setRegistChannel("ttandriod");
                    //修改渠道状态为噶广告渠道
                    usersBuId.setChannelType("2");
                    dateInfoBean.updateUsersBuyId(usersBuId);
                }
            }
            // 查询出所有激活用户
            List<Activity> activity = dateInfoBean.findActivity();
            // 遍历所有激活用户
            for (Activity ac : activity) {
                String id = ac.getId();
                // 根据id查询出avtivity
                Activity acc = dateInfoBean.findActivityBuId(id);
                // String channel = acc.getChannel();
                if (!QwyUtil.isNullAndEmpty(acc)) {
                    if (!QwyUtil.isNullAndEmpty(acc.getChannel()) && acc.getChannel().equals("1267250937")) {
                        acc.setChannel("ttIOS");
                        acc.setChannelType("2");
                        dateInfoBean.updateActivityBuyId(acc);
                    }
                }
            }
            List<Activity> androidActivity = dateInfoBean.findAndroidActivity();
            // 遍历所有激活用户
            for (Activity ac : androidActivity) {
                String id = ac.getId();
                // 根据id查询出avtivity
                Activity acc = dateInfoBean.findActivityBuId(id);
                // String channel = acc.getChannel();
                if (!QwyUtil.isNullAndEmpty(acc)) {
                    if (!QwyUtil.isNullAndEmpty(acc.getChannel()) && !acc.getChannel().contains("tt")) {
                        acc.setChannel("ttandriod");
                        acc.setChannelType("2");
                        dateInfoBean.updateActivityBuyId(acc);
                    }
                }
            }
   /*         List<Activity> android = dateInfoBean.findAndroid();
            for (Activity act :android) {
                String id = act.getId();
                Activity acc = dateInfoBean.findActivityBuId(id);
                // String channel = acc.getChannel();
                if (!QwyUtil.isNullAndEmpty(acc)) {
                    if (!QwyUtil.isNullAndEmpty(acc.getChannel()) && acc.getChannel().contains("tt")) {
                        acc.setChannel("oss");
                        acc.setChannelType("1");
                        dateInfoBean.updateActivityBuyId(acc);
                    }
                }
            }
*/
            List<Activity> havaChannelActivity = dateInfoBean.findHavaChannelActivity();
            // 遍历所有激活用户
            for (Activity ac : havaChannelActivity) {
                String id = ac.getId();
                // 根据id查询出avtivity
                Activity acc = dateInfoBean.findActivityBuId(id);
                String channel = ac.getChannel();
                if (!QwyUtil.isNullAndEmpty(acc)) {
                    if (!QwyUtil.isNullAndEmpty(acc.getChannel())) {
                        acc.setChannel(channel);
                        acc.setChannelType("2");
                        dateInfoBean.updateActivityBuyId(acc);
                    }
                }
            }

            List<Date> insertTime = dateInfoBean.findInsertTime();// 此方法暂时没用
            for (Date time : insertTime) {
                updateQdtjThreadBean.updateQdtjByDate(time);
            }
            return "loadUserInfo";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

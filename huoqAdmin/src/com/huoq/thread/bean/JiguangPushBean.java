package com.huoq.thread.bean;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;
import com.huoq.util.MyLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

//极光推送
@Service
public class JiguangPushBean {
    private static ResourceBundle resb = ResourceBundle.getBundle("app");
    private static final MyLogger log =  MyLogger.getLogger(JiguangPushBean.class);

    /**
     * 极光推送
     */
    public boolean jiguangPush(Long userIdAlias,String message) {
        boolean flag = false;

//        try {
////            message = new String(message.getBytes("ISO-8859-1"), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        log.info("对别名" + userIdAlias + "的用户推送信息");
        PushResult result = push(String.valueOf(userIdAlias), message);
        if (result != null && result.isResultOK()) {
            flag = true;
            log.info("针对别名" + userIdAlias + "的信息推送成功！");
        } else {
            flag = false;
            log.info("针对别名" + userIdAlias + "的信息推送失败！");
        }
        return flag;
    }

    /**
     * 极光推送方法(采用java SDK)
     *
     * @param alias
     * @param alert
     * @return PushResult
     */
    public PushResult push(String alias, String alert) {
        String appKey = resb.getString("jpush.appKey");
        String masterSecret = resb.getString("jpush.masterSecret");
        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, clientConfig);
        PushPayload payload = buildPushObject_ios_audienceMore_messageWithExtras(alert, alias);
        try {
            return jpushClient.sendPush(payload);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
            return null;
        }
    }

    /**
     * 构建推送对象：平台是 Andorid 与 iOS，推送的设备有（推送目标为tags和推送目标别名为aliases），推送内容是 - 内容为 msg_content的消息，并且附加字段 from = JPush。
     *
     * @param msg_content
     * @param aliases
     * @return
     */
    public PushPayload buildPushObject_ios_audienceMore_messageWithExtras(String msg_content, String aliases) {
        boolean isProduction = Boolean.valueOf(resb.getString("jpush.isProduction"));

        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.alias(aliases))
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(isProduction)//true-推送生产环境 false-推送开发环境（测试使用参数）
                        .build())
                .setNotification(Notification.alert(msg_content))
                .build();
    }

}

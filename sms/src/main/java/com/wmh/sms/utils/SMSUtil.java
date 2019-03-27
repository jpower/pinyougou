package com.wmh.sms.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;


@Component
@PropertySource("classpath:application.properties")
public class SMSUtil {
    // 短信应用SDK AppID     // 1400开头
    @Value("${appid}")
    private int appid;
    // 短信应用SDK AppKey
    @Value("${appkey}")
    private String appkey;

    /**
     * 发送短信
     * @param phoneNumber //发送的手机号
     * @param templateId // 短信模板ID，需要在短信应用中申请
     * @param smsSign   // 签名，使用的是`签名内容`，而不是`签名ID`
     * @param param   //参数
     */
    public void sendSMS(String phoneNumber,int templateId,String smsSign,String param) {
        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            String[] params =param.split(",");
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumber,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }catch (Exception e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }

}
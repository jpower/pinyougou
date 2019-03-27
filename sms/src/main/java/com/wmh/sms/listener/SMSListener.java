package com.wmh.sms.listener;

import com.wmh.sms.utils.SMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by 周大侠
 * 2018-12-16 15:56
 */
@Component
public class SMSListener {
    @Autowired
    private SMSUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSMS(Map<String,String> map){
        smsUtil.sendSMS( map.get("phoneNumber"),Integer.parseInt(map.get("templateId")), map.get("smsSign"),map.get("params"));

    }





}

package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Created by 周大侠
 * 2018-12-15 11:19
 */
@Component
public class ItemPageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            boolean b = itemPageService.deleItemHtml((Long) objectMessage.getObject());
            System.out.println(b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.MacSpi;
import javax.jms.*;

/**
 * Created by 周大侠
 * 2018-12-15 9:42
 */
@Component
public class ItemPageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {

            boolean b = itemPageService.genItemHtml(Long.parseLong(textMessage.getText()));
            System.out.println(b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

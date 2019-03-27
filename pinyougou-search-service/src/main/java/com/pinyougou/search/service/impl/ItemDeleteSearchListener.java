package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.impl.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * Created by 周大侠
 * 2018-12-15 0:02
 */
@Component
public class ItemDeleteSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
             itemSearchService.deleteList(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

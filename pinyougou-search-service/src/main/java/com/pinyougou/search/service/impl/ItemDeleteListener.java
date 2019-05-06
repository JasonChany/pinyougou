package com.pinyougou.search.service.impl;

import java.io.Serializable;
import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.search.service.ItemSearchService;

/**
 * 索引库同步更新监听器
 * @author Jason
 *
 */
@Component
public class ItemDeleteListener implements MessageListener {
	@Autowired
	private ItemSearchService itemSearchService;

	@Override
	public void onMessage(Message message) {
		try {
			ObjectMessage objectMessage=(ObjectMessage) message;
			Long[] goodsIds = (Long[]) objectMessage.getObject();
			System.out.println("监听到消息："+goodsIds);
			itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
			System.out.println("成功删除索引库记录");
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}

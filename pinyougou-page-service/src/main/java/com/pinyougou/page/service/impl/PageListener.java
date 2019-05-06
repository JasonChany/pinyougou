package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

/**
 * 生成静态页面的监听器（接收消息并完成页面生成操作）
 * @author Jason
 *
 */
@Component
public class PageListener implements MessageListener {
	@Autowired
	private ItemPageService itemPageService;

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage=(TextMessage) message;
			Long goodsId=Long.parseLong(textMessage.getText());
			boolean b = itemPageService.getItemHtml(goodsId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

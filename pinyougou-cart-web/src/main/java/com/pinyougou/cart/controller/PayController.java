package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.util.Hash;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;
import utils.IdWorker;

/**
 * 支付控制层
 * @author Jason
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference
	private WeixinPayService weixinPayService;
	
	@Reference
	private OrderService orderService;

	/**
	 * 本地支付
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative() {
		//从缓存中获取支付日志
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		TbPayLog payLog = orderService.searchPayLogFromRedis(username);
		System.out.println(payLog);
		if (payLog!=null) {
			return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");			
		}else {
			return new HashMap<>();
		}
		
	}
	
	/**
	 * 循环获取支付状态
	 * @param out_trade_no
	 * @return
	 */
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		Result result=null;
		int x=0;
		while(true) {
			Map map = weixinPayService.queryPayStatus(out_trade_no);
			if (map==null) {
				result=new Result(false, "支付出错");
				break;
			}
			if ("SUCCESS".equals(map.get("trade_state"))) {
				result=new Result(true, "支付成功");
				//修改订单状态
				orderService.updateOrderStatus(out_trade_no, (String)map.get("transaction_id"));
				break;
			}
			
			try {
				Thread.sleep(3000L);//每隔3秒向微信发送请求获取支付状态
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
			x++;
			if (x>=100) {//5分钟用户不支付或关闭支付页面，终止循环获取支付状态
				result=new Result(false, "二维码超时");
				break;
			}
		}
		
		return result;
		
	}
}

package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

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
	private SeckillOrderService seckillOrderService;

	/**
	 * 本地支付
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative() {
		//从缓存中获取秒杀订单
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		TbSeckillOrder seckillOrder=seckillOrderService.searchOrderFromRedisByUserId(username);
		if (seckillOrder!=null) {
			long fen=(long) (seckillOrder.getMoney().doubleValue()*100);
			return weixinPayService.createNative(seckillOrder.getId()+"", fen+"");			
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
		//获取当前登陆用户
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Result result=null;
		int x=0;
		while(true) {
			Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
			if (map==null) {
				result=new Result(false, "支付出错");
				break;
			}
			if ("SUCCESS".equals(map.get("trade_state"))) {
				result=new Result(true, "支付成功");
				//保存订单
				seckillOrderService.saveOrderFromRedisToDb(username,Long.valueOf(out_trade_no), map.get("transaction_id"));
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
			
			if(x>20){
				result=new Result(false, "二维码超时");
				//1.调用微信的关闭订单接口
				Map<String,String> payresult =weixinPayService.closePay(out_trade_no);
				
				if( !"SUCCESS".equals(payresult.get("result_code")) ){//如果返回结果是正常关闭
					if("ORDERPAID".equals(payresult.get("err_code"))){
						result=new Result(true, "支付成功");
						seckillOrderService.saveOrderFromRedisToDb(username,Long.valueOf(out_trade_no), map.get("transaction_id"));
					}
				}
				
				if(result.isSuccess()==false){
					System.out.println("超时，取消订单");
					//2.调用删除
					seckillOrderService.deleteOrderFromRedis(username,Long.valueOf(out_trade_no));
				}
				break;
			}
		}
		
		return result;
		
	}
}

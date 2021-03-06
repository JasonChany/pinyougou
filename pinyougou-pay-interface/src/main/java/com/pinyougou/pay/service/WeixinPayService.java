package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 微信支付接口服务
 * @author Jason
 *
 */
public interface WeixinPayService {
	
	/**
	 * 生成微信支付二维码
	 * @param out_trade_no
	 * @param total_fee
	 * @return
	 */
	public Map createNative(String out_trade_no,String total_fee);
	
	/**
	 * 根据订单号查询订单支付状态
	 * @param out_trade_no
	 * @return
	 */
	public Map queryPayStatus(String out_trade_no);
	
	/**
	 * 关闭支付
	 * @param out_trade_no
	 * @return
	 */
	public Map closePay(String out_trade_no);

}

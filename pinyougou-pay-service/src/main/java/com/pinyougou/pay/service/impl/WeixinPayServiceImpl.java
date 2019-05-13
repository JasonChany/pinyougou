package com.pinyougou.pay.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;

import utils.HttpClient;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
	
	@Value("${appid}")
	private String appid;//公众账号ID
	
	@Value("${partner}")
	private String partner;//商户号
	
	@Value("${notifyurl}")
	private String notifyurl;//回调地址
	
	@Value("${partnerkey}")
	private String partnerkey;

	@Override
	public Map createNative(String out_trade_no, String total_fee) {
		
		
		//封装参数
		Map param=new HashMap<>();
		param.put("appid", appid);//设置公众账号ID
		param.put("mch_id", partner);//设置商户号
		param.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
		param.put("body", "品优购");//商品描述
		param.put("out_trade_no", out_trade_no);//商品订单号
		param.put("total_fee", total_fee);//标价金额（订单总金额）
		param.put("spbill_create_ip", "127.0.0.1");//支付终端ip
		param.put("notify_url", notifyurl);//回调地址（使用此模式此参数无用，但是必需传递参数，随意传即可）
		param.put("trade_type", "NATIVE");//交易类型（使用此模式的固定模式为NATIVE）
				
		try {
			//转换参数
			String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);//根据密钥生成带签名的xml参数
			System.out.println("请求参数为："+xmlParam);
			
			//发送支付请求
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
			client.setHttps(true);
			client.setXmlParam(xmlParam);
			client.post();
			
			//获取返回结果
			String xmlResult = client.getContent();
			System.out.println("微信返回消息："+xmlResult);
			Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
			
			//返回给前台的数据（只返回需要的数据，安全起见）
			Map<String,String> map = new HashMap<>();
			map.put("code_url", mapResult.get("code_url"));//二维码链接
			map.put("out_trade_no", out_trade_no);//订单号
			map.put("total_fee", total_fee);//订单总金额
			
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		} 		
		
	}

	@Override
	public Map queryPayStatus(String out_trade_no) {
		//封装参数
		Map param=new HashMap<>();
		param.put("appid", appid);//设置公众账号ID
		param.put("mch_id", partner);//设置商户号
		param.put("out_trade_no", out_trade_no);//商品订单号
		param.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
		
		try {
			//转换请求参数(通过密钥生成带签名的xml参数)
			String xmlParam=WXPayUtil.generateSignedXml(param, partnerkey);
			System.out.println("请求参数："+xmlParam);
			
			//发送支付请求
			HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
			client.setHttps(true);
			client.setXmlParam(xmlParam);
			client.post();
			
			//获取返回结果
			String xmlResult = client.getContent();
			System.out.println("微信返回消息："+xmlResult);
			
			//转换结果参数
			Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
			return mapResult;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

}

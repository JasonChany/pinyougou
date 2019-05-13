package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import utils.CookieUtil;


@RestController
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	@Reference(timeout=6000)
	private CartService cartService;

	/**
	 * 从Cookie中获取购物车列表
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList(){
		//获取当前登录人
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		//从cookie获取购物车列表
		String cartListStr = CookieUtil.getCookieValue(request, "cartList","UTF-8");
		if (cartListStr==null||"".equals(cartListStr)) {
			cartListStr="[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListStr,Cart.class);
		if ("anonymousUser".equals(username)) {//匿名用户（未登录）从cookie获取购物车
			
			
			return cartList_cookie;
		}else {//非匿名用户，从redis获取数据
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			if (cartList_cookie.size()>0) {
				//合并购物车
				cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
				//将合并后的购物车存入redis
				cartService.saveCartListToRedis(username, cartList_redis);
				//清空cookie
				CookieUtil.deleteCookie(request, response, "cartList");
				System.out.println("执行了合并购物车的逻辑");
			}		
			
			return cartList_redis;
		}
		
		
	}
	@RequestMapping("/addGoodsToCartList")
	@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")//允许跨域请求，并支持获取Cookie（默认）
	public Result addGoodsToCartList(Long itemId,Integer num) {
		//获取当前登陆人
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			//获取购物车列表
			List<Cart> cartList = findCartList();
			//调用服务层操作购物车
			cartList=cartService.addGoodsToCartList(cartList, itemId, num);
			if ("anonymousUser".equals(username)) {//未登录
				//存入cookie
				CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");				
			}else {//已登录
				//存入redis
				cartService.saveCartListToRedis(username, cartList);
			}
			return new Result(true, "存入购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "存入购物车失败");
		}
		
		
	}
	
	

	
}

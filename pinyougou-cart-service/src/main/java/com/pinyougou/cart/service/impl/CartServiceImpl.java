package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		//1、根据商品ID获取商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item==null) {
			throw new RuntimeException("商品不存在");
		}
		if (!"1".equals(item.getStatus())) {
			throw new RuntimeException("商品已下架");
		}
		//2、从商品中获取商家ID
		String sellerId = item.getSellerId();
		//3、判断购物车对象中是否有该商家的购物车对象
		Cart cart = searchCartBySellerId(cartList, sellerId);
		if (cart==null) {//4、如果没有，创建购物车对象(购物车明细对象--->购物车明细列表--->购物车对象)并添加到购物车对象列表
			cart=new Cart();
			cart.setSellerId(sellerId);//设置商家ID
			cart.setSellerName(item.getSeller());
			List<TbOrderItem> orderItemList=new ArrayList<>();//创建购物车明细列表
			TbOrderItem orderItem=createOrderItem(item,num);//创建购物车明细对象
			orderItemList.add(orderItem);//将购物车明细对象添加到购物车明细列表
			cart.setOrderItemList(orderItemList);//将购物车明细列表添加到购物车对象
			cartList.add(cart);//将购物车对象添加到购物车列表
			
		}else {//5、如果有，判断当前购物车中购物明细列表中是否有该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
			
			if (orderItem==null) {//5.1  没有，创建明细对象--->购物明细列表--->购物车对象--->购物车对象列表
				orderItem=createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
				
			}else {//5.2 有，更新数量，更新金额
				orderItem.setNum(orderItem.getNum()+num);//更新数量
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));//更新金额
				//如果购物车明细对象的商品数量为0，从购物车明细列表中移除该购物车明细对象
				if (orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				//如果购物车明细列表数量为0，从购物车列表中移除该购物车对象
				if (cart.getOrderItemList().size()==0) {
					cartList.remove(cart);
				}
			}
		}
		
		
		
		
		return cartList;
	}
	
	
	/**
	 * 从购物车明细列表中获取指定商品对象(用于判断购物车明细列表中是否有指定商品)
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
		for (TbOrderItem tbOrderItem : orderItemList) {
			if (tbOrderItem.getItemId().longValue()==itemId.longValue()) {
				return tbOrderItem;
			}
		}
		return null;
	}


	/**
	 * 构建购物车明细对象
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item, Integer num) {
		if (num<0) {
			throw new RuntimeException("数量非法");
		}
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}


	/**
	 * 从购物车列表中获取指定商家的购物车对象（判断购物车列表中是否有该商家的的购物车对象）
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}


	@Override
	public List<Cart> findCartListFromRedis(String username) {
		List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		
		if (cartList==null) {
			cartList=new ArrayList<>();
		}
		System.out.println("从redis获取购物车");
		return cartList;
	}


	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis存入购物车");
		redisTemplate.boundHashOps("cartList").put(username, cartList);
		
	}


	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		for (Cart cart : cartList2) {
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				cartList1=addGoodsToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
			}
		}
		return cartList1;
	}

}

package com.itheima.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

/**
 * 品牌Service层接口
 * @author Jason
 *
 */

public interface BrandService {
	/**
	 * 查询所有品牌
	 * @return
	 */
	List<TbBrand> findAll();

}

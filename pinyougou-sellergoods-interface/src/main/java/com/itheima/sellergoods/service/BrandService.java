package com.itheima.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;

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
	
	/**
	 * 分页查询
	 * @param pageNum	当前页
	 * @param pageSize	每页记录数
	 * @return
	 */
	PageResult findPage(Integer pageNum,Integer pageSize);
	
	/**
	 * 新增品牌
	 * @param brand
	 */
	void add(TbBrand brand);
	
	/**
	 * 查询品牌详情
	 * @param id
	 * @return
	 */
	TbBrand findOne(Long id);
	
	/**
	 * 修改品牌
	 * @param brand
	 */
	void update(TbBrand tbBrand);
	
	/**
	 * 删除品牌
	 * @param ids
	 */
	void delete(Long[] ids);
	
	/**
	 * 分页查询（可带条件）
	 * @param brand	查询条件
	 * @param pageNum	当前页
	 * @param pageSize	每页显示的数据
	 * @return
	 */
	PageResult findPageByCondition(TbBrand brand,Integer pageNum,Integer pageSize);

}

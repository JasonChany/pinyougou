package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.sellergoods.service.BrandService;
import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;


@RestController
@RequestMapping("/brand")
public class BrandController {
	@Reference
	private BrandService brandService;
	
	/**
	 * 查询所有品牌
	 * @return
	 */
	@RequestMapping("/findAll.do")
	public List<TbBrand> findAll() {
		return brandService.findAll();
	}
	
	/**
	 * 分页查询所有品牌
	 */
	@RequestMapping("/findPage.do")
	public PageResult findPage(Integer pageNum,Integer pageSize) {
		return brandService.findPage(pageNum, pageSize);
	}
	
	/**
	 * 新增品牌
	 * @param brand
	 * @return
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody TbBrand brand) {
		try {
			brandService.add(brand);
			return new Result(true, "新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "新增失败");
		}
	}
	
	/**
	 * 查询品牌详情
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne.do")
	public TbBrand findOne(Long id) {
		return brandService.findOne(id);
	}
	
	/**
	 * 修改品牌
	 * @param brand
	 * @return
	 */
	@RequestMapping("/update.do")
	public Result update(@RequestBody TbBrand brand) {
		try {
			brandService.update(brand);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
	
	/**
	 * 删除品牌
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete.do")
	public Result delete(Long[] ids) {
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 分页条件查询
	 * @param brand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult findPageByCondition(@RequestBody TbBrand brand,Integer pageNum,Integer pageSize) {
		return brandService.findPageByCondition(brand, pageNum, pageSize);
	}
}

package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.sellergoods.service.BrandService;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;

import entity.PageResult;
import entity.Result;
//添加注册dubbox服务的注解(包不要导成spring的了)
@Service
@Transactional
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page=(Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(),page.getResult());
	}

	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
	}

	@Override
	public TbBrand findOne(Long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand brand) {
			brandMapper.updateByPrimaryKey(brand);
	}

	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPageByCondition(TbBrand brand, Integer pageNum, Integer pageSize) {
		//开始分页
		PageHelper.startPage(pageNum,pageSize);
		//构建查询条件对象
		TbBrandExample brandExample=new TbBrandExample();
		Criteria criteria=brandExample.createCriteria();
		if(brand!=null) {
			if (brand.getName()!=null&&brand.getName().length()>0) {
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if (brand.getFirstChar()!=null&&brand.getFirstChar().length()>0) {
				criteria.andFirstCharEqualTo(brand.getFirstChar());
			}
		}		
		//开始查询
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(brandExample);
		return new PageResult(page.getTotal(), page.getResult());
	}

}

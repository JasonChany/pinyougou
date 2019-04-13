package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.sellergoods.service.BrandService;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
//添加注册dubbox服务的注解(包不要导成spring的了)
@Service
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

}

package com.pinyougou.page.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsDescExample;
import com.pinyougou.pojo.TbGoodsDescExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

@Service
public class ItemPageServiceImpl implements ItemPageService {
	@Value("${pagedir}")
	private String pagedir;
	
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;
 
	@Override
	public boolean getItemHtml(Long goodsId) {
		try {
			//获取配置对象
			Configuration configuration = freeMarkerConfig.getConfiguration();
			//根据一个模板 获取一个模板对象
			Template template = configuration.getTemplate("item.ftl");
			//数据模型容器
			Map dataModel=new HashMap<>();
			
			//加载商品表数据
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);
			
			//获取商品扩展表数据
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc",goodsDesc);
			
			//读取商品分类
			TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());//读取一级分类
			TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());//读取二级分类
			TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());//读取三级分类
			dataModel.put("itemCat1", itemCat1.getName());
			dataModel.put("itemCat2", itemCat2.getName());
			dataModel.put("itemCat3", itemCat3.getName());
			
			//读取SKU信息
			TbItemExample example=new TbItemExample();
			com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId).andStatusEqualTo("1");//过滤未上架商品
			example.setOrderByClause("is_default desc");//根据默认标志排序，保证第一个是默认的
			List<TbItem> itemList = itemMapper.selectByExample(example);
			dataModel.put("itemList", itemList);
			
			//获取流对象并输出模板文件
			Writer out = new FileWriter(pagedir+goodsId+".html");
			template.process(dataModel, out);
			out.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}

}

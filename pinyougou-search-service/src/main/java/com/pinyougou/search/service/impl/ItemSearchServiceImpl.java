package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate; 

	@Override
	public Map<String, Object> search(Map searchMap) {
		//关键字空格处理(满足多关键字查询)
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replaceAll(" ", ""));
		//结果集对象
		Map<String,Object> map=new HashMap();
		
		
		/*
		Query query=new SimpleQuery();
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		List<TbItem> items = page.getContent();
		map.put("rows", items);
		*/
		
		
		//高亮查询列表
		map.putAll(searchList(searchMap));
		
		//分类列表
		List categoryList = selectCategoryList(searchMap);
		map.put("categoryList", categoryList);
		
		//品牌列表规格列表
		String categoryName=(String) searchMap.get("category");
		if (!"".equals(categoryName)) {
			//有分类名称
			map.putAll(searchBrandAndSpecList(categoryName));			
		}else {
			//没有分类名称
			if (categoryList.size()>0) {
				map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));				
			}
		}
		
		
			
		
		
		return map;
	}
	
	/**
	 * 高亮查询封装方法
	 * @param searchMap
	 * @return
	 */
	private Map searchList(Map searchMap) {
		Map<String,Object> map=new HashMap();
		
		//构建高亮查询对象
		HighlightQuery higntQuery=new SimpleHighlightQuery();
		
		//构建高亮选项对象
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//设置高亮域前缀
		highlightOptions.setSimplePostfix("</em>");//设置高亮域后缀
		//将高亮选项对象设置进高亮查询对象
		higntQuery.setHighlightOptions(highlightOptions);
		
		
		//1.1按关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		//将条件对象设置进高亮查询对象
		higntQuery.addCriteria(criteria);
		
		//1.2按分类筛选
		if (!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			higntQuery.addFilterQuery(filterQuery);			
		}
		//1.3按品牌筛选
		if (!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			higntQuery.addFilterQuery(filterQuery);			
		}
		//1.4按规格列表筛选
		if (searchMap.get("spec")!=null) {
			Map<String,String> specMap = (Map) searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				higntQuery.addFilterQuery(filterQuery);
			}
		}
		//1.5按价格筛选
		if (!"".equals(searchMap.get("price"))) {
			String priceStr=(String)searchMap.get("price");
			String[] price = priceStr.split("-");
			if (!"0".equals(price[0])) {
				Criteria fiterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery=new SimpleFilterQuery(fiterCriteria);
				higntQuery.addFilterQuery(filterQuery);
			}
			if (!"*".equals(price[1])) {
				Criteria fiterCriteria=new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery=new SimpleFilterQuery(fiterCriteria);
				higntQuery.addFilterQuery(filterQuery);
			}
		}
		
		
		//1.6分页查询
		System.out.println(searchMap);
		//获取每页显示条数
		Integer pageSize=new Integer(searchMap.get("pageSize")+"");
		if (pageSize==null||"".equals(pageSize)) {
			pageSize=20;
		}
			//获取当前页		
		Integer pageNo=new Integer(searchMap.get("pageNo")+"");
		if (pageNo==null||"".equals(pageNo)) {
			pageNo=1;//空处理
		}
		
		higntQuery.setOffset((pageNo-1)*pageSize);//开始索引：（当前页-1）*每页显示条数
		higntQuery.setRows(pageSize);
		
		//1.7排序查询
		String sortValue = (String) searchMap.get("sort");//DESC/ASC
		String sortField=(String) searchMap.get("sortField");//排序字段
		if (sortValue!=null&&!"".equals(sortValue)) {
			if ("ASC".equals(sortValue)) {
				Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
				higntQuery.addSort(sort);				
			}
			
			if ("DESC".equals(sortValue)) {
				Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
				higntQuery.addSort(sort);				
			}
			
		}
		
		//获取高亮结果对象
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(higntQuery, TbItem.class);
		
		//循环高亮入口集合
		List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : highlighted) {
			//获取原实体类
			TbItem item = highlightEntry.getEntity();
			if (highlightEntry.getHighlights().size()>0&&highlightEntry.getHighlights().get(0).getSnipplets().size()>0) {
				item.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		
		//返回每页显示的数据
		map.put("rows", page.getContent());
		//返回总页数
		map.put("totalPages", page.getTotalPages());
		//返回总记录数
		map.put("total", page.getTotalElements());
		
		
		return map;
	}
	
	/**
	 * 查询分类列表
	 * @param searchMap
	 * @return
	 */
	private List selectCategoryList(Map searchMap) {
		//初始化结果对象
		List list=new ArrayList<>();
		
		//构建查询对象
		Query groupQuery=new SimpleQuery();
		
		//构建分组选项对象
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");//设置分组条件（根据商品分类名称分组）
		//设置进查询对象
		groupQuery.setGroupOptions(groupOptions);
		
		//构建关键字条件对象
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		//将条件对象设置进查询对象
		groupQuery.addCriteria(criteria);
		
		//进行分组查询得到分组页对象
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
		
		//根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		//遍历分组入口集合
		for (GroupEntry<TbItem> groupEntry : content) {
			//将分组结果名称封装到结果中
			list.add(groupEntry.getGroupValue());
		}
		
		return list;
	}
	
	/**
	 * 查询品牌和规格列表
	 * @param category
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		//初始化结果对象
		Map map=new HashMap<>();
		
		//根据商品分类名称从缓存中获取模板ID
		Long typeTemplateId=(Long) redisTemplate.boundHashOps("categoryList").get(category);
		
		//根据该商品分类的模板ID从缓存中获取其所有的品牌列表和规格选项列表
		if (typeTemplateId!=null) {
			//从缓存获取品牌列表
			List brandList=(List) redisTemplate.boundHashOps("brandList").get(typeTemplateId);
			//存入结果对象
			map.put("brandList", brandList);
			
			//从缓存获取规格选项列表
			List specList=(List) redisTemplate.boundHashOps("specList").get(typeTemplateId);
			//存入结果对象
			map.put("specList", specList);
		}
		
		return map;
		
	}

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除的商品ID："+goodsIdList);
		Query query=new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsis").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

}

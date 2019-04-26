package com.pinyougou.search.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;

@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {
	
	@Reference
	private ItemSearchService searchService;
	
	/**
	 * 关键字搜索
	 */
	@RequestMapping("/search")
	public Map<String, Object> search(@RequestBody Map searchMap){
		return searchService.search(searchMap);
	}
}

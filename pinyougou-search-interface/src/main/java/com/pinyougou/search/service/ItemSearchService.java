package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口服务
 * @author Jason
 *
 */

public interface ItemSearchService {
	
	public Map<String,Object> search(Map searchMap);
	
	/**
	 * 添加数据到索引库
	 * @param list
	 * @return
	 */
	public void importList(List list);
	
	/**
	 * 从索引库删除指定的数据
	 * @param goodsIdList
	 */
	public void deleteByGoodsIds(List goodsIdList);

}

app.service("contentService",function($http){
	//根据广告分类获取该分类下的所有广告列表
	this.findByCategoryId=function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);		
	}
})
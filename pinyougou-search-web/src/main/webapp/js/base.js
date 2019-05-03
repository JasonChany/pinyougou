var app=angular.module('pinyougou',[]);

//使用$sce服务添加html信任策略（将html字符串转换为html对象），通过过滤器实现
app.filter("trustHtml",['$sce',function($sce){
	return function(data){
		return $sce.trustAsHtml(data);		
	}
}])
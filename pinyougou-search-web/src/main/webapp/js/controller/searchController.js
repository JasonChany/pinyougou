app.controller('searchController',function($scope,$location,searchService){
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':'1','pageSize':'20','sort':'','sortField':''};//搜索对象
	
	//关键字搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(function(response){
			$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
			$scope.resultMap=response;
			bulidPageLabel();
		})
	}
	
	//添加搜索项
	$scope.addSearchItem=function(key,value){
		//点击的是分类或品牌
		if (key=='brand'||key=='category'||key=='price') {
			$scope.searchMap[key]=value;
		}else {
			//点击的是规格
			$scope.searchMap.spec[key]=value;
		}
		
		$scope.search();
	}
	
	//撤销搜索选项
	$scope.removeSearchItem=function(key){
		if (key=="brand"||key=="category"||key=='price') {
			$scope.searchMap[key]="";
		}else{
			delete $scope.searchMap.spec[key];//移除此属性
		}
		
		$scope.search();
	}
	
	//构建分页标签(显示前2后2)
	bulidPageLabel=function(){
		$scope.pageLabel=[];
		var maxPageNo=$scope.resultMap.totalPages;
		var firstPage=1;
		var lastPage=maxPageNo;
		var firstDot=true;
		var lastDot=true;
		//数据记录有5页以上
		if (maxPageNo>5) {
			if ($scope.searchMap.pageNo<=3) {//当前页码小于等于3页
				lastPage=5;
				firstDot=false;//不显示点
			}else if ($scope.searchMap.pageNo>=lastPage-2) {//当前页大于等于末页的前两页
				firstPage=maxPageNo-4;
				lastDot=false;//不显示点
			}else {
				firstPage=$scope.searcheMap.pageNo-2;
				lastPage=$scope.searcheMap.pageNo+2;
			}		
		}else{//数据记录没有5页（前后都不显示点）
			firstDot=false;
			lastDot=false;
		}
		
		//循环产生分页标签
		for (var i = firstPage; i <=lastPage; i++) {
			$scope.pageLabel.push(i);
		}
		
	}
	
	//根据页码查询
	$scope.queryByPage=function(pageNo){
		if (pageNo<1||pageNo>$scope.resultMap.totalPages) {
			return;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	
	//判断当前页是否为第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}		
	}
	
	//判断当前页是否为最后一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}	
	}
	
	//设置排序规则
	$scope.sortSearch=function(sortField,sort){
		$scope.searchMap.sortField=sortField;
		$scope.searchMap.sort=sort;
		$scope.search();
	}
	
	//隐藏品牌列表（搜索关键字中包含品牌名称就隐藏）
	$scope.keywordsIsBrand=function(){
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0) {
				return true;
			}
		}
		return false;
	}
	
	//接收首页传递的搜索关键字进行搜索
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=$location.search()['keywords'];
		$scope.search();
	}
	
})
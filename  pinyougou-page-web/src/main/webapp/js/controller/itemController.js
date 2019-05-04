app.controller('itemController',function($scope){
	//用于记录用户选择的规格
	$scope.specificationItems={};
	//商品数量操作
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	//用户选择规格
	$scope.selectSpecification=function(name,value){
		$scope.specificationItems[name]=value;
		searchSku();
	}
	
	//判断规格是否被用户选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}
	}
	
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec))//将默认SKU的规格进行显示
	}
	
	//匹配两个对象的值是否完全相等
	matchObject=function(map1,map2){
		for(var key in map1){
			if(map1[key]!=map2[key]){
				return false;
			}			
		}
		//做双向匹配，完全匹配上才算相等
		for(var key in map2){
			if(map2[key]!=map1[key]){
				return false;
			}
		}
		
		return true;
	}
	
	//查询用户当前选择的SKU
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={"id":0,"title":"没有该规格的商品","price":0};
	}
	
	//添加商品到购物车
	$scope.addToCart=function(){
		alert('加入购物车成功-skuid:'+$scope.sku.id);
	}
})
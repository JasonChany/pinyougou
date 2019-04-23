/*
 * 公共控制器
 */

app.controller("baseController", function($scope) {
	//重新加载列表（把分页查询的函数进行封装便于重复调用，如下方分页插件配置中改变配置时就要调用分页查询）
	$scope.reloadList=function(){
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	};
	
	//分页插件配置
	$scope.paginationConf={
			currentPage: 1,
			totalItems: 10,
			itemsPerPage: 10,
			perPageOptions: [10, 20, 30, 40, 50],
			onChange: function(){
				$scope.reloadList();//重新加载（本质是调用分页查询的函数）
			}
	};
	
	//更新选择框
	$scope.selectIds=[];//定义数组
	$scope.updateSelection=function($event,id){
		if($event.target.checked){
			//复选框被选中（向数组添加id）
			$scope.selectIds.push(id);
		}else{
			//复选框未选中（获取该id在数组中的位置并从数组中移除）
			var index=$scope.selectIds.indexOf(id);//获取该id在数组中的位置
			$scope.selectIds.splice(index,1)//第二个参数表示移除几个元素
		}
	};
	
	//从json字符串中获取指定key的字符串
	$scope.jsonToString=function(jsonString,key){
		//将Json字符串转换为json对象
		var json=JSON.parse(jsonString);
		//
		var value="";
		for (var i = 0; i < json.length; i++) {
			if(i>0){
				value+=",";
			}
			value+=json[i][key];
		}
		
		return value;
	};
	
	//从集合中查询指定key的对象
	$scope.searchObjectByKey=function(list,key,keyValue){
		for (var i = 0; i < list.length; i++) {
			if (list[i][key]==keyValue) {
				return list[i];
			}
		}
		return null;
	}
	
	
	
})



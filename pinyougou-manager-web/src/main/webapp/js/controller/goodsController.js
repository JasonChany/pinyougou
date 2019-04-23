 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location  ,goodsService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
	$scope.itemCatList=[];//商品分类列表
	//查询商品分类
	$scope.findItemList=function(){
		itemCatService.findAll().success(function(response){
			for (var i = 0; i < response.length; i++) {
				$scope.itemCatList[response[i].id]=response[i].name;				
			}
		})
	}
	
	//商品审核
	$scope.updateStatus=function(status){
		if ($scope.selectIds.length==0) {
			return;
		}
		goodsService.updateStatus($scope.selectIds,status).success(function(response){
			if (response.success) {
				alert(response.message);
				$scope.reloadList();
				$scope.selectIds=[];//清空 ID 集合
			}else{
				alert(response.message);
			}
		});
	}
	
	//商品分类一级类目
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(function(response){
			$scope.itemCat1List=response;
		});
	}
	
	
	//商品分类二级类目（监控一级类目变量变化后执行的操作）
	$scope.$watch('entity.goods.category1Id', function(newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat2List=response;
		});
	})
	
	//商品分类三级类目（监控二级类目变量变化后执行的操作）
	$scope.$watch('entity.goods.category2Id', function(newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat3List=response;
		});
	})
	
	//模板ID显示（监控三级类目变量变化后执行的操作）
	$scope.$watch('entity.goods.category3Id', function(newValue,oldValue) {
		itemCatService.findOne(newValue).success(function(response){
			$scope.entity.goods.typeTemplateId=response.typeId
		})
	})
	
	//品牌列表展示、扩展属性展示（监控模板ID变量变化后执行的操作）
	$scope.$watch('entity.goods.typeTemplateId', function(newValue,oldValue) {
		typeTemplateService.findOne(newValue).success(function(response){
			$scope.typeTemplate=response;
			//品牌列表
			$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
			//扩展属性
			if($location.search()['id']==null){
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);				
			}
		})
		//获取模板对应的规格列表
		typeTemplateService.findSpecList(newValue).success(function(response){
			$scope.specList=response;
		});
		
	})
	
	
	//商品管理模块分类显示转换
	$scope.itemCatList=[];
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(function(response){
			for (var i = 0; i < response.length; i++) {
				$scope.itemCatList[response[i].id]=response[i].name;
			}
		});
	}
	
	//商品管理模块的修改获取商品实体
	$scope.findOne=function(){
		//获取商品ID
		var id=$location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(function(response) {
			$scope.entity=response;
			//富文本编辑器值类型转换
			editor.html($scope.entity.goodsDesc.introduction);
			//图片数据的类型转换
			$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
			//$scope.entity.goodsDesc=JSON.parse($scope.entity.goodsDesc);
			//扩展属性值类型转换
			$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
			//规格选项值类型转换
			$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
			//SKU规格列转换
			for (var i = 0; i < $scope.entity.itemList.length; i++) {
				$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
			}
		})
		
	}
	//检查规格选项是否被选中
	$scope.checkAttributeValue=function(specName,optionName){
		var items=$scope.entity.goodsDesc.specificationItems;
		var object=$scope.searchObjectByKey(items,"attributeName",specName);
		if (object==null) {
			return false;
		}else{
			if (object.attributeValue.indexOf(optionName)>=0) {
				return true;
			}else{
				return false;
			}
		}
	}
	
	
    
});	

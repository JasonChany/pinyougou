 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	//初始化商品组合实体数据结构
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};
	
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
		//提取富文本编辑器的值
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
					$scope.entity={};	
					//清空富文本编辑器
					editor.html("");
					location.href="goods.html";
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
	//增加
	$scope.add=function(){
		//获取富文本编辑器中的数据（商品介绍）
		$scope.entity.goodsDesc.introduction=editor.html();//这里的editor是页面的js脚本的一个富文本编辑器对象
		goodsService.add($scope.entity).success(
				function(response){
					if (response.success) {
						alert(response.message);
						$scope.entity={};	
						//清空富文本编辑器
						editor.html("");
					}else{
						alert(response.message);
					}
				}
		);
	}
	//上传图片
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response){
			if (response.success) {
				$scope.image_entity.url=response.message;
			}else{
				alert(response.message)
			}
		}).error(function(){
			alert("上传发生错误");
		})
	}
	
	
	//添加图片列表
    $scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//列表中移除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
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
	
	//组装选择的规格选项
	$scope.updateSpecAttribute=function($event,name,value){
		//根据属性名获取对应的规格（用于判断是否勾选了规格选项）数据结构：[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G","联通3G"]},{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","6寸"]}]
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		if (object!=null) {
			//有勾选的(向组合实体对应的specificationItems属性中添加要存储的规格选项)
			if ($event.target.checked) {
				//勾选（添加值）
				object.attributeValue.push(value);
			}else{
				//取消勾选（移除值）
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				//如果全部都取消勾选，则移除该结构
				if (object.attributeValue.length==0) {
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1)
				}
			}
		}else{
			//无勾选（第一次勾选：向组合实体对应的specificationItems属性中添加存储的数据结构）
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
	}
	
	//构建sku列表(数据结构：[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ]  ，spec是动态构建（勾选添加）的，即规格名称作为key，规格选项作为value，最终"规格"页面存储到tb_item表中)
	$scope.createItemList=function(){
		//构建初始数据结构
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];
		//循环规格选项列表(数据来源：像数据结构的spec填充数据)
		var items=$scope.entity.goodsDesc.specificationItems;
		for (var i = 0; i < items.length; i++) {
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
		}
	}
	
	//添加列值(list的作用是用于深克隆，相当于深克隆的来源)
	addColumn=function(list,columnName,columnValues){
		var newList=[];//深克隆后用于返回的新集合
		for (var i = 0; i < list.length; i++) {
			var oldRow=list[i];
			for (var j = 0; j < columnValues.length; j++) {
				var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
		
	}
	
	//商品管理模块状态转换
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
	
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
	
	//商家管理模块的商品上架与下架（更新marketable状态）
	$scope.marketable=['已下架','已上架'];//商品状态
	$scope.updateMarketable=function(marketable){
		if ($scope.selectIds.length==0) {
			alert("请选中至少一件已审核商品");
			return;
		}
		
		goodsService.updateMarketable($scope.selectIds,marketable).success(function(response){
			if (response.success) {
				alert(response.message);
				$scope.reloadList();
				$scope.selectIds=[];
			}else{
				alert(response.message);
			}
		});
	}
	
});	

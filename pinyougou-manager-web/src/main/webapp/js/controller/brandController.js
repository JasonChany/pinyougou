/*
    		控制器层
    	*/
    	app.controller("brandController",function($scope,brandService,$controller){
    		//（伪）继承公共控制器
    		$controller("baseController",{$scope:$scope});
    		
    		//查询所有品牌
    		$scope.findAll=function(){
    			brandService.findAll.success(function(response){
        			$scope.list=response;
        		})
    		};    	
    		
    		
    		
    		//分页查询
    		$scope.findPage=function(pageNum,pageSize){
    			brandService.findPage(pageNum,pageSize).success(function(response){
    				//绑定数据
    				$scope.list=response.rows;
    				//更新总记录数
    				$scope.paginationConf.totalItems=response.total
    			});
    		};
    		
    		
    		
    		//新增（修改）品牌
    		$scope.save=function(){
    			var object=brandService.add($scope.entity);
    			if($scope.entity.id!=null){
    				object=brandService.update($scope.entity);
    			}
    			object.success(function(response){
    				if(response.success){
    					//新增(修改)成功（重新加载列表）
    					alert(response.message);
    					$scope.reloadList();
    				}else{
    					//新增（修改）失败
    					alert(response.message);
    				}
    			})
    		};
    		
    		//修改品牌回显品牌信息
    		$scope.findOne=function(id){
    			brandService.findOne(id).success(function(response){
    				$scope.entity=response;
    			})
    		};
    		
    		
    		//删除品牌
    		$scope.dele=function(){
    			if(confirm("你确定要删除吗？")){
    				brandService.dele($scope.selectIds).success(function(response) {
    					if(response.success){
    						alert(response.message);
    						$scope.reloadList();
    					}else{
    						alert(response.message);
    					}
    				});
    			}    			
    		};
    		
    		//分页条件查询
    		$scope.searchEntity={};
    		$scope.search=function(pageNum,pageSize){
    			brandService.search(pageNum,pageSize,$scope.searchEntity).success(function(response) {
    				//绑定数据
    				$scope.list=response.rows;
    				//更新总记录数
    				$scope.paginationConf.totalItems=response.total
				});
    		}
    		
    		
    	});
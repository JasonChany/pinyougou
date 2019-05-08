 //控制层 
app.controller('userController' ,function($scope,$controller,$interval,$timeout,userService){
	//控制倒计时点击验证码按钮
	$scope.timer = false;
    $scope.timeout = 60000;
    $scope.timerCount = $scope.timeout / 1000;
    $scope.text = "获取验证码";
    
    $scope.onClick = function(){
      $scope.showTimer = true;
      $scope.timer = true;
      $scope.text = "秒后重新获取";
      var counter = $interval(function(){
        $scope.timerCount = $scope.timerCount - 1;
      }, 1000);
      $timeout(function(){
        $scope.text = "重新获取";
        $scope.timer = false;
        $interval.cancel(counter);
        $scope.showTimer = false;
        $scope.timerCount = $scope.timeout / 1000;
      }, $scope.timeout);
    };
	
	//注册
	$scope.reg=function(){
		if ($scope.entity.password!=$scope.password) {
			alert("两次输入的密码不一致，请重新输入！")
			$scope.entity.password="";
			$scope.password="";
			return ;
		}else{
			userService.add($scope.entity,$scope.smsCode).success(
				function(response) {
					alert(response.message);
				}
			);
		}
	}
	
	//发送验证码
	$scope.sendCode=function(){
		if ($scope.entity.phone==null||$scope.entity.phone=='') {
			alert("请输入手机号！");
			return;
		}
		//手机号不为空才对按钮限制
		$scope.onClick();
		userService.sendCode($scope.entity.phone).success(
			function(response){
				alert(response.message);
			}
		);
	}
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		userService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		userService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		userService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=userService.update( $scope.entity ); //修改  
		}else{
			serviceObject=userService.add( $scope.entity  );//增加 
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
		userService.dele( $scope.selectIds ).success(
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
		userService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	

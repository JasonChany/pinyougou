app.controller('seckillGoodsController',function($scope,$location,$interval,seckillGoodsService){
	
	//获取当前正在参与秒杀的商品
	$scope.findList=function(){
		seckillGoodsService.findList().success(
			function(response){
				$scope.list=response;
			}
		)		
	}
	
	//秒转换为时间字符串格式
	convertTimeString=function(allsecond){
		var days=Math.floor(allsecond/(60*60*24));//天
		var hours=Math.floor((allsecond-days*60*60*24)/(60*60));//小时
		var minutes=Math.floor((allsecond-days*60*60*24-hours*60*60)/60);//分钟
		var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		timeString="";
		if (days>0) {
			timeString=days+"天";
		}
		return timeString+(hours<10?'0'+hours:hours)+":"+(minutes<10?'0'+minutes:minutes)+":"+(second<10?'0'+seconds:seconds);
	}
	
	//获取秒杀商品详情
	$scope.findOne=function(){
		var id=$location.search()["id"];
		seckillGoodsService.findOne(id).success(
			function(response){
				$scope.entity=response;
				//倒计时读秒
				allsecond=Math.floor((new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000)
				time=$interval(function(){
					if (allsecond>0) {
						allsecond-=1;
						$scope.timeString=convertTimeString(allsecond);
					}else{
						$interval.cancel(time);
					}
				},1000)
				
			}
		)
	}
	
	//秒转换为时间字符串格式
	convertTimeString=function(allsecond){
		var days=Math.floor(allsecond/(60*60*24));//天
		var hours=Math.floor((allsecond-days*60*60*24)/(60*60));//小时
		var minutes=Math.floor((allsecond-days*60*60*24-hours*60*60)/60);//分钟
		var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		timeString="";
		if (days>0) {
			timeString=days+"天";
		}
		return timeString+(hours<10?'0'+hours:hours)+":"+(minutes<10?'0'+minutes:minutes)+":"+(seconds<10?'0'+seconds:seconds);
	}
	
	
	//提交秒杀订单
	$scope.submitOrder=function(){
		seckillGoodsService.submitOrder($scope.entity.id).success(
			function(response){
				if (response.success) {
					alert("秒杀成功，请在两分钟之内完成支付！");
					location.href="pay.html";
				}else{
					alert(response.message);
				}
				
			}
		);
	}
	
	
	
})
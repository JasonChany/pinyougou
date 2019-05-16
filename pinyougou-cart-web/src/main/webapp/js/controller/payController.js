app.controller('payController',function($scope,$location,payService){
	
	//本地支付
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){
				$scope.money=(response.total_fee/100).toFixed(2);//订单总金额
				$scope.out_trade_no=response.out_trade_no;//订单号
				
				//生成二维码
				var qr = new QRious({
					element:document.getElementById('qrious'),
					size:250,
					level:'H',
					value:response.code_url
				});
				
				//一生成二维码就获取支付状态
				queryPayStatus(response.out_trade_no);
			}
		)
	}
	
	//获取支付状态
	queryPayStatus=function(out_trade_no){
		payService.queryPayStatus(out_trade_no).success(
			function(response){
				if (response.success) {
					//支付成功（跳转到支付成功页面）
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					//支付失败（跳转到失败页面）
					if (response.message=='二维码超时') {
						//$scope.createNative();//重新生成二维码
						alert(response.message);
					}else{
						location.href="payfail.html";
					}
				}
			}
		)
	}
	
	//支付成功页面获取订单支付总金额
	$scope.getMoney=function(){
		return $location.search()['money'];
		
	}
})
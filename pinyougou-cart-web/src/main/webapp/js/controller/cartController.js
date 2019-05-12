app.controller("cartController",function($scope,cartService){
	
	//获取购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				$scope.totalValue=cartService.sum($scope.cartList);
		})
	}
	
	//添加商品到购物车
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				if (response.success) {
					$scope.findCartList();//刷新列表
				}else{
					alert(response.message);
				}
			}
		);
	}
	
	//获取收货人地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(function(response){
			$scope.addressList=response;
			//设置默认地址
			for (var i = 0; i < $scope.addressList.length; i++) {
				if ($scope.addressList[i].isDefault=='1') {
					$scope.address=$scope.addressList[i];
					break;
				}
			}
		})
	}
	
	//选择收货地址
	$scope.selectAddress=function(address){
		$scope.address=address;
	}
	
	//判断当前地址是否选中
	$scope.isSelectedAddress=function(address){
		if ($scope.address==address) {
			return true;
		}else{
			return false;
		}
	}
	
	//订单提交数据结构
	$scope.order={paymentType:'1'};
	
	//选择支付方式
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	
	//提交订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		cartService.submitOrder($scope.order).success(
			function(response){
				if (response.success) {
					if ($scope.order.paymentType=='1') {//支付类型为微信支付（跳到支付页面）
						location.href="pay.html";
					}else {//支付类型是货到付款（跳到成功页面）
						location.href="paysuccess.html";
					}
				}else{
					alert(response.message);
				}
			}
		)
	}
	
	$scope.newAddress={isDefault:'0'};
	
	//新增收货地址
	$scope.addAddress=function(){
		cartService.addAddress($scope.newAddress).success(
			function(response){
				if (response.success) {
					//刷新列表
					$scope.findAddressList();
					//清空收货地址
					$scope.newAddress={isDefault:'0'};
				}else{
					alert(response.message);
				}
			}
		)
	}
	
	//判断新增收货地址是否选择默认地址选项
	$scope.isSelectedDefault=function($event){
		if ($event.target.checked) {
			$scope.newAddress.isDefault='1';
		}else{
			$scope.newAddress.isDefault='0';
		}
	}
	
	
})
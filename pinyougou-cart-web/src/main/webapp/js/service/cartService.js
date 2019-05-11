app.service("cartService",function($http){
	
	//获取购物车列表
	this.findCartList=function(){
		return $http.get("/cart/findCartList.do")
	}
	
	//添加商品到购物车
	this.addGoodsToCartList=function(itemId,num){
		return $http.get("/cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
	}
	
	//求合计
	this.sum=function(cartList){
		var totalValue={totalNum:0,totalMoney:0.00};
		for (var i = 0; i < cartList.length; i++) {
			var orderItemList=cartList[i].orderItemList;
			for (var j = 0; j < orderItemList.length; j++) {
				var orderItem=orderItemList[j];
				//数量合计
				totalValue.totalNum+=orderItem.num
				//金额合计
				totalValue.totalMoney+=orderItem.totalFee;
			}
		}
		
		return totalValue;
	}
})
app.service("seckillGoodsService",function($http){
	
	//获取当前正在参与秒杀的商品
	this.findList=function(){
		return $http.get("/seckillGoods/findList.do");
	}
	
	//获取秒杀商品详情
	this.findOne=function(id){
		return $http.get("/seckillGoods/findOneFromRedis.do?id="+id);
	}
	
	//提交订单
	this.submitOrder=function(seckillId){
		return $http.get('/seckillOrder/submitOrder.do?seckillId='+seckillId);
	}
	
	
})
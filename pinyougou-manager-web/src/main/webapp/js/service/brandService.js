/*
    		服务层 
    	*/
    	app.service("brandService", function($http) {
    		//查询所有品牌
    		this.findAll=function(){
    			return $http.get("/brand/findAll.do");
    		};
    		
    		//分页查询
    		this.findPage=function(pageNum,pageSize){
    			return $http.get("/brand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    		};
    		
    		//新增品牌
    		this.add=function(entity){
    			return $http.post("/brand/add.do",entity)
    		};
    		
    		//修改品牌
    		this.update=function(entity){
    			return $http.post("/brand/update.do",entity)
    		};
    		//修改品牌回显品牌信息
    		this.findOne=function(id){
    			return $http.get("/brand/findOne.do?id="+id)
    		};
    		//删除品牌
    		this.dele=function(selectIds){
    			return $http.get("/brand/delete.do?ids="+selectIds)
    		};
    		//分页条件查询
    		this.search=function(pageNum,pageSize,searchEntity){
    			return $http.post("/brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity)
    		}
    		//获取品牌列表
    		this.selectOptionList=function(){
    			return $http.get("/brand/selectOptionList.do");
    		}
    	});
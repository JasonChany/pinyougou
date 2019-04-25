app.controller("contentController",function($scope,contentService){
	
	$scope.contentList=[];//用于存储不同种类的广告，下标即广告种类（便于区分，减少变量创建冗余）
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId]=response;
		});
	}
})
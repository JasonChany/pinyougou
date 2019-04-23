app.service("uploadService", function($http) {
	this.uploadFile=function(){
		var formData=new FormData();
		formData.append("file",file.files[0]);//第一个file必须与后台controller的参数名一致，第二个file与文件选择框的name属性值一致
		return $http({
			method:"POST",
			url:"/upload.do",
			data:formData,
			headers: {'Content-Type':undefined},
			transformRequest: angular.identity
		});
	}
})
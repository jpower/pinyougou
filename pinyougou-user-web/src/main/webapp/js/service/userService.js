//服务层
app.service('userService',function($http){
	this.register=function (smscode,user) {
		return $http.post('/user/register.do?smscode='+smscode,user);
    }

    this.sendCode=function (phone) {
		return $http.get('/user/sendCode.do?phone='+phone);
    }
});

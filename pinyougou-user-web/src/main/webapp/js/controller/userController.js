//控制层
app.controller('userController', function ($scope, $controller, userService) {

    $scope.register = function () {
        //注册
        if ($scope.entity.password != $scope.password) {
            alert("两次输入的密码不一致，请重新输入");
            return;
        }
        if($scope.smscode==null||$scope.smscode==""){
            alert("验证码不能为空");
            return;
        }
        userService.register($scope.smscode,$scope.entity).success(
            function (response) {
                alert(response.message);
            }
        )
    }
    $scope.sendCode = function () {
        if($scope.entity.phone==null||$scope.entity.phone==''){
            alert("手机号不能为空");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message)
            }


        )

    }


});	

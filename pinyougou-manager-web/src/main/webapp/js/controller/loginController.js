app.controller('loginController',function ($scope, loginService) {
    $scope.showLoginName=function () {
        loginService.loginName().success(
            function (result) {
            $scope.loginName=result.loginName;
        })
    }
});
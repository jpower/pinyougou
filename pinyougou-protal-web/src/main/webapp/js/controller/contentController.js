app.controller("contentController",function ($scope, contentService) {


    $scope.contentList=[];
    $scope.findContentByCategoryId=function (id) {
        contentService.findContentByCategoryId(id).success(
            function (response) {
                    $scope.contentList[id]=response;
            }
        )
    }
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})
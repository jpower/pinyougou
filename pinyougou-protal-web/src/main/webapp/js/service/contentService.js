app.service('contentService',function ($http) {
    this.findContentByCategoryId=function (id) {

        return $http.get('content/findByCategoryId.do?id='+id);
    }
})
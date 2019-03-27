app.service('seckillService',function ($http) {
    this.findValidAllFromRedis=function () {
        return $http.get('/seckillGoods/findValidAll.do');
    }
    this.findOneFromRedis=function (id) {
        return $http.get("/seckillGoods/findOneFromRedis.do?id="+id)
    }
    this.submitOrder=function (seckillId) {
        return $http.get('/seckillOrder/submitOrder.do?seckillId='+seckillId)
    }
})
app.controller('wxPayController',function ($scope,wxPayService,$location) {
    $scope.createNative = function () {
        wxPayService.createNative().success(
            function (response) {
                $scope.money = (response.total_fee / 100).toFixed(2);	//金额
                $scope.out_trade_no = response.out_trade_no;//订单号
                //二维码

                var qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    value: response['code_url']
                })
                queryPayStatus(response.out_trade_no);
            }
        )
    }
    //查询支付状态
    queryPayStatus = function (out_trade_no) {
        wxPayService.queryPayStatus(out_trade_no).success(
            function (response) {
                if (response.success) {
                    location.href = "paysuccess.html#?money=" + $scope.money;
                } else if (response.message === "二维码超时,取消订单") {
                    alert("二维码超时,取消订单");
                    location.href = "seckill-index.html";


                } else {
                    location.href = "payfail.html";
                }
            }
        );
    }
    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    };
})
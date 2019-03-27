package com.pinyougou.pay.service;

import java.util.Map;
/**
 * 微信支付接口
 * Created by 周大侠
 * 2018-12-20 22:52
 */
public interface WeixinPayService {
    /**
     * 生成微信支付二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额(分)
     * @return
     */
     Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
     Map findPayStatus(String out_trade_no);

    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);

}

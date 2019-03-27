package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付控制层
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference(timeout = 50000)
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;
    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.findPayLogByUserId(userId);
        if(payLog!=null) {
            return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");
        }else{
            return new HashMap();
        }
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        int x = 0;
        while (true) {
            Map<String,String> payStatus = weixinPayService.findPayStatus(out_trade_no);
            if (payStatus == null) {
                return new Result(false, "支付出错");
            }


            String tradeState =  payStatus.get("trade_state");
            if (tradeState.equals("SUCCESS")) {

                orderService.updatePayStatus(out_trade_no,  payStatus.get("transaction_id"));
                return new Result(true, "支付成功");
            }
            x++;
            if (x > 100) {
                return new Result(false, "二维码超时");
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}

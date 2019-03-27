package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    private SeckillOrderService seckillOrderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.findSeckillOrderByUserIdFromRedis(userId);
        if (seckillOrder != null) {
            // 金额（分）
            long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);
            return weixinPayService.createNative(seckillOrder.getId() + "", fen + "");
        } else {
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
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int x = 0;
        while (true) {
            Map<String, String> payStatus = weixinPayService.findPayStatus(out_trade_no);
            if (payStatus == null) {
                return new Result(false, "支付出错");
            }


            String tradeState = payStatus.get("trade_state");
            if (tradeState.equals("SUCCESS")) {

                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), payStatus.get("transactionId"));
                return new Result(true, "支付成功");
            }
            x++;
            if (x > 100) {
                Map<String, String> closePayResult = weixinPayService.closePay(out_trade_no);
                if ("FAIL".equals(closePayResult.get("result_code"))) {
                    if ("ORDERPAID".equals(closePayResult.get("err_code"))) {
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), payStatus.get("transactionId"));
                        return new Result(true, "支付成功");
                    }
                }

                seckillOrderService.cancelOrderFromRedis(userId, out_trade_no);
                return new Result(false, "二维码超时,取消订单");


            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}

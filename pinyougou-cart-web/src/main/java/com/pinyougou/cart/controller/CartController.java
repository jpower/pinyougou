package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import entity.Result;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by 周大侠
 * 2018-12-19 14:04
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    /**
     * 添加商品到购物车
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    // allowCredentials="true"  可以缺省
    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    public Result addCart(Long itemId, Integer num) {
        try {
            List<Cart> cartList = getCartList();
            // 添加新的商品条目到购物车中
            cartList = cartService.addItemToCart(cartList, itemId, num);
            // 获取用户的登录名
            String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
            // 如果该用户没有登陆
            if ("anonymousUser".equals(loginName)) {
                // 保存购物车信息到cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 24 * 3600, "UTF-8");

            } else {
                // 保存购物车信息到redis中
                cartService.addCartListToRedis(loginName, cartList);
            }

            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }

    /**
     * 购物车列表
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> getCartList() {
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        //从cookie中取出购物车信息
        String cookieCarts = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (StringUtils.isEmpty(cookieCarts)) {
            cookieCarts = "[]";
        }
        List<Cart> cookieCartList = JSON.parseArray(cookieCarts, Cart.class);
        // 如果用户没有登陆 直接返回cookie中的购物车信息
        if ("anonymousUser".equals(loginName)) {

            return cookieCartList;

        } else {

            List<Cart> redisCartList = cartService.findCartListFromRedis(loginName);
            // 如果cookie中存在购物车信息则和redis中的购物车信息合并
            if (cookieCartList.size() > 0) {
                // 将cookie购物车和redis购物车合并
                List<Cart> cartList = cartService.mergeCartList(cookieCartList, redisCartList);
                cartService.addCartListToRedis(loginName, cartList);
                // 删除cookie中的购物车信息
                CookieUtil.deleteCookie(request, response, "cartList");
                return cartList;
            }
            return redisCartList;
        }
    }


}

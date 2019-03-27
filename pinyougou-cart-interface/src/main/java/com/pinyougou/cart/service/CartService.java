package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * Created by 周大侠
 * 2018-12-19 11:02
 */
public interface CartService  {
    List<Cart> addItemToCart(List<Cart> list,Long itemId,Integer num);
    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */

    List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
     void addCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
     List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);



}

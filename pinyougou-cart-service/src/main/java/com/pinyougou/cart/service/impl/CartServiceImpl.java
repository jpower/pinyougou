package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周大侠
 * 2018-12-19 11:04
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加商品到老购物车 返回新的购物车
     * @param list
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addItemToCart(List<Cart> list, Long itemId, Integer num) {
        //根据商品ID查询出该商品 和该商品的商家ID
        /*  遍历原有购物车 查询是否有该商家ID
         *    1.如果有 查询 该商家商品中是否有该商品 如果有增加数量 改价格
         *          如果没有 添加该商品信息
         *    2。如果没有 添加商家 添加商品
         */
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("该商品不存在");
        }
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("商品状态不合法");
        }
        String sellerId = item.getSellerId();
        // 获取这个商家的购物车商品
        Cart cart = searchCartBySellerId(list, sellerId);
        if (cart != null) {
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            // 如果该商家购物车中有该商品 改变数量 改变价格
            if (orderItem != null) {
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                // 如果数量小于0 就从属于该商家的购物车中删除该商品信息
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                // 如果该商家购物车没有商品信息 删除该商家的购物车
                if(cart.getOrderItemList().size()==0){
                    list.remove(cart);
                }
            } else {
                cart.getOrderItemList().add(createOrderItem(num, item));
            }
        }else{
            //如果没有该商家的购物车 就创建一个
            Cart newCart = new Cart();
            newCart.setSellerId(sellerId);
            newCart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItems = new ArrayList<>();
            orderItems.add(createOrderItem(num,item));
            newCart.setOrderItemList(orderItems);
            list.add(newCart);
        }
        return list;
    }

    /**
     * 根据用户名从redis中取出购物车
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {

        System.out.println("根据用户名从redis中取出购物车");
        List<Cart> cartList  = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            return new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 添加购物车到redis中
     * @param username
     * @param cartList
     */
    @Override
    public void addCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("添加购物车到redis中");
        redisTemplate.boundHashOps("cartList").put(username,cartList);

    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
               cartList2 = addItemToCart(cartList2,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList2;
    }

    /**
     * 根据Item转换成一个OrderItem
     * @param num
     * @param item
     * @return
     */
    private TbOrderItem createOrderItem(Integer num, TbItem item) {
        TbOrderItem orderItem = new TbOrderItem();
        //数量
        orderItem.setNum(num);
        //skuID
        orderItem.setItemId(item.getId());
        //spuID
        orderItem.setGoodsId(item.getGoodsId());
        //单价
        orderItem.setPrice(item.getPrice());
        //总价
        BigDecimal totalFee = new BigDecimal(num * item.getPrice().doubleValue());
        orderItem.setTotalFee(totalFee);
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        return orderItem;
    }

    /**
     * 遍历购物车列表根据商家ID查询是否有对应的购物车
     * @param list
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> list, String sellerId) {
        for (Cart cart : list) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 遍历购物车明细列表根据商品ID查询商品明细里是否有对应的商品
     * @param list
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> list, Long itemId) {
        for (TbOrderItem orderItem : list) {
            if (orderItem.getItemId().longValue()==itemId.longValue()) {
                return orderItem;
            }
        }

        return null;
    }
}

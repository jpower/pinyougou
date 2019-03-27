package com.pinyougou.seckill.service;
import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	 List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	 void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	 void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	 TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	 void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	 PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);
	/**
	 * 提交订单
	 * @param seckillId
	 * @param userId
	 */
	 void submitOrder(Long seckillId,String userId);

	TbSeckillOrder findSeckillOrderByUserIdFromRedis(String userId);
	/**
	 * 支付成功保存订单
	 * @param userId
	 * @param orderId
	 */
	 void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);

	/**
	 * 支付超时取消订单
	 */
	void cancelOrderFromRedis(String userId,String out_trade_no);

}

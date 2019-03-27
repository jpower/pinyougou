package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 * Created by 周大侠
 * 2018-11-30 21:09
 */
public interface BrandService {
    /**
     * 查询所有品牌
     *
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 品牌分页
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);


    /**
     * 添加品牌
     *
     * @param brand
     */
    void add(TbBrand brand);

    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 修改
     *
     * @param brand
     */
    void update(TbBrand brand);

    /**
     * 删除一个或多个
     *
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 带条件的查询
     * @param brand
     * @param page
     * @param size
     * @return
     */
    PageResult search(TbBrand brand, int page, int size);

    /**
     * 品牌下拉框数据
     */
    List<Map> selectOptionList();

}

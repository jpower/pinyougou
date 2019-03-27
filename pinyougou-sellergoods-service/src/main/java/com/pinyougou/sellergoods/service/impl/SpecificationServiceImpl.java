package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.insert(tbSpecification);

        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        // 修改规格
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        // 删除原来的规格项
        List<TbSpecificationOption> specificationOptions = getTbSpecificationOptionsBySpecId(tbSpecification.getId());
        for (TbSpecificationOption specificationOption : specificationOptions) {
            specificationOptionMapper.deleteByPrimaryKey(specificationOption.getId());
        }

        // 保存现在的规格项
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        // 查询规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        List<TbSpecificationOption> specificationOptions = getTbSpecificationOptionsBySpecId(id);

        // 将规格和多个规格项组合成一个bean 返回
        Specification specification = new Specification();
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptionList(specificationOptions);

        return specification;

    }

    /**
     * 根据规格id  查询规格项
     * @param id
     * @return
     */
    private List<TbSpecificationOption> getTbSpecificationOptionsBySpecId(Long id) {

        TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria();
        criteria.andSpecIdEqualTo(id);

        return specificationOptionMapper.selectByExample(optionExample);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            System.out.println("士大夫");
            specificationMapper.deleteByPrimaryKey(id);

            List<TbSpecificationOption> specificationOptions = getTbSpecificationOptionsBySpecId(id);

            for (TbSpecificationOption specificationOption : specificationOptions) {
                specificationOptionMapper.deleteByPrimaryKey(specificationOption.getId());
            }
        }
    }

    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

		@Override
		public List<Map> selectOptionList() {
			return specificationMapper.selectOptionList();
		}

}

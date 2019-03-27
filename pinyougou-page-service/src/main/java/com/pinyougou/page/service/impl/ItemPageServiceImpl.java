package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 周大侠
 * 2018-12-13 11:05
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer configurer;
    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper descMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public boolean genItemHtml(Long goodsId)  {
        try {
            Configuration configuration = configurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map dataMap = new HashMap();
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataMap.put("goods",goods);
            TbGoodsDesc goodsDesc = descMapper.selectByPrimaryKey(goodsId);

            dataMap.put("goodsDesc",goodsDesc);

            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dataMap.put("itemCat1", itemCat1);
            dataMap.put("itemCat2", itemCat2);
            dataMap.put("itemCat3", itemCat3);

            TbItemExample itemExample = new TbItemExample();
            TbItemExample.Criteria criteria = itemExample.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            itemExample.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(itemExample);
            dataMap.put("itemList",itemList);
            Writer writer = new FileWriter(pagedir+goodsId+".html");

            template.process(dataMap,writer);
            writer.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleItemHtml(Long goodsId) {
        try {
            File file = new File(pagedir+goodsId+".html");
            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.impl.service.ItemSearchService;
import javafx.beans.binding.ObjectExpression;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.logging.Filter;


/**
 * Created by 周大侠
 * 2018-12-09 17:11
 */
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map searchItemList(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        // 查询列表高亮显示
        map.putAll(searchList(searchMap));
        // 查询商品分类列表
        List<String> categoryList = itemCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //查询品牌和规格信息
        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            map.putAll(searchBrandAndSpecList((String) searchMap.get("category")));
        } else if (!categoryList.isEmpty()) {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }

        return map;
    }

    /**
     * 查询品牌和规格信息
     *
     * @param category
     * @return
     */
    private Map<String, Object> searchBrandAndSpecList(String category) {
        Map<String, Object> map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCartList").get(category);
        if (typeId != null) {
            //查询品牌
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            //查询规格
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;

    }


    /**
     * 查询出商品列表高亮显示
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        // 过滤关键字
        String keywords = (String) searchMap.get("keywords");
        if (!StringUtils.isEmpty(keywords)) {
            Criteria criteria = new Criteria("item_keywords").is(keywords.replace(" ", ""));
            query.addCriteria(criteria);
        }
        // 过滤价格
        String price = (String) searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] prices = price.split("-");

            if (!"0".equals(prices[0])) {

                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery1 = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery1);
            }
            if (!"*".equals(prices[1])) {

                Criteria criteria2 = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery2 = new SimpleFilterQuery(criteria2);
                query.addFilterQuery(filterQuery2);
            }

        }


        // 过滤商品分类
        query.addFilterQuery(filterCondition("item_category", (String) searchMap.get("category")));
        // 过滤品牌
        query.addFilterQuery(filterCondition("item_brand", (String) searchMap.get("brand")));
        // 过滤规格
        Map<String, Object> spec = (Map<String, Object>) searchMap.get("spec");
        for (String s : spec.keySet()) {
            query.addFilterQuery(filterCondition("item_spec_" + s, (String) spec.get(s)));
        }

        //1.6 分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");//提取页码
        if (pageNo == null) {
            pageNo = 1;//默认第一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页记录数
        if (pageSize == null) {
            pageSize = 20;//默认20
        }
        query.setOffset((pageNo - 1) * pageSize);//从第几条记录查询
        query.setRows(pageSize);

        //1.7排序
        String sortValue = (String) searchMap.get("sort");//ASC  DESC
        String sortField = (String) searchMap.get("sortField");//排序字段
        if (sortValue != null && !sortValue.equals("")) {
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlightEntries = page.getHighlighted();
        for (HighlightEntry<TbItem> h : highlightEntries) {
            TbItem item = h.getEntity();

            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());
        map.put("total", page.getSize());
        return map;
    }

    // 组装FilterQuery
    private FilterQuery filterCondition(String fieldName, String conditionName) {
        if (StringUtils.isEmpty(conditionName)) {
            return new SimpleFilterQuery();
        }
        Criteria brandCriteria = new Criteria(fieldName).is(conditionName);
        FilterQuery filterQuery = new SimpleFilterQuery(brandCriteria);
        return filterQuery;

    }

    /**
     * 跟据查询出来的商品分组查询出商品分类
     *
     * @param searchMap
     * @return
     */
    private List<String> itemCategoryList(Map searchMap) {
        List<String> category = new ArrayList<>();
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> itemGroupResult = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = itemGroupResult.getGroupEntries();
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entryList) {
            category.add(entry.getGroupValue());
        }
        return category;
    }

    /**
     * 导入数据
     *
     * @param list
     */
    @Override
    public void importList(List list) {
        System.out.println("jj");
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除数据
     */
    @Override
    public void deleteList(Long[] ids) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


}

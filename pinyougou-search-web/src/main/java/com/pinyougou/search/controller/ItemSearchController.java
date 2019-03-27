package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.impl.service.ItemSearchService;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by 周大侠
 * 2018-12-09 23:55
 */
@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;
    @RequestMapping("/search")
    public Map<String, Object> searchItemList(@RequestBody Map searchMap) {
        return itemSearchService.searchItemList(searchMap);
    }

}


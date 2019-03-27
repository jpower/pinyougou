package com.pinyougou.protal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 周大侠
 * 2018-12-08 20:19
 */
@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    @RequestMapping("/findByCategoryId")
    public List<TbContent> findContentByCategoryId(Long id) {
        return contentService.findByCategoryId(id);
    }

}

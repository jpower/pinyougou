package com.pinyougou.search.service.impl.service;

import java.util.List;
import java.util.Map;

/**
 * Created by 周大侠
 * 2018-12-09 17:08
 */
public interface ItemSearchService {
    public Map searchItemList(Map searchMap);
    /**
     * 导入数据
     * @param list
     */
    public void importList(List list);

    /**
     * 删除数据
     */
    public void deleteList(Long[] ids);

}

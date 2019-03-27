package com.pinyougou.page.service;

import java.io.IOException;

/**
 * Created by 周大侠
 * 2018-12-13 10:54
 */
public interface ItemPageService {

    boolean genItemHtml(Long goodsId);
    boolean deleItemHtml(Long goodsId);
}

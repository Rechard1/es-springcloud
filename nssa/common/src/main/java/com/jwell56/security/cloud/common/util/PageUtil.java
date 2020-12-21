package com.jwell56.security.cloud.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;

/**
 * id组件
 *
 * @author wsg
 * @since 2019/8/22
 */
public class PageUtil<T> {
    public IPage<T> initIPage(IPage iPage) {
        IPage<T> newIPage = new Page<>();
        BeanUtils.copyProperties(iPage, newIPage);
        newIPage.setRecords(new ArrayList<>());
        return newIPage;
    }
}


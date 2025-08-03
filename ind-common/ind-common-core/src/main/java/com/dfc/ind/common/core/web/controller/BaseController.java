package com.dfc.ind.common.core.web.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.page.PageDomain;
import com.dfc.ind.common.core.web.page.TableSupport;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfc.ind.common.core.web.domain.JsonResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * web层通用数据处理
 * 
 * @author admin
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected Page startPage()
    {
        Page page = new Page();
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        //排序列
        if(StringUtils.isNotEmpty(pageDomain.getOrderByColumn())){
            OrderItem orderItem = new OrderItem();
            //设置排序列
            orderItem.setColumn(pageDomain.getOrderByColumn());
            //设置排序规则
            if(StringUtils.isNotEmpty(pageDomain.getIsAsc())){
                orderItem.setAsc(StringUtils.inStringIgnoreCase(pageDomain.getIsAsc(),"true"));
            }
            page.addOrder(orderItem);
        }
        return page;
    }

    /**
     * 根据状态返回
     * @param state
     * @return
     */
    protected JsonResults toResult(boolean state) {
        return state ? JsonResults.success() : JsonResults.error();
    }

    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected JsonResults toAjax(int rows)
    {
        return rows > 0 ? JsonResults.success() : JsonResults.error();
    }
}
